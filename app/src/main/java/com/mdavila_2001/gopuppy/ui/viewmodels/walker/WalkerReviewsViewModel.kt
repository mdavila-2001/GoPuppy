package com.mdavila_2001.gopuppy.ui.viewmodels.walker

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.mdavila_2001.gopuppy.data.repository.AuthRepository
import com.mdavila_2001.gopuppy.data.repository.WalkRepository
import com.mdavila_2001.gopuppy.data.remote.models.review.Review
import com.mdavila_2001.gopuppy.ui.views.walker_reviews.ReviewUiModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import java.time.Duration
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

data class WalkerReviewsState(
    val isLoading: Boolean = false,
    val reviews: List<ReviewUiModel> = emptyList(),
    val averageRating: Double = 0.0,
    val ratingDistribution: Map<Int, Int> = emptyMap(),
    val walkerName: String = "Paseador",
    val walkerPhotoUrl: String? = null,
    val errorMessage: String? = null
)

class WalkerReviewsViewModel(application: Application) : AndroidViewModel(application) {
    private val authRepository = AuthRepository(application.applicationContext)
    private val walkRepository = WalkRepository()

    private val _state = MutableStateFlow(WalkerReviewsState())
    val state: StateFlow<WalkerReviewsState> = _state.asStateFlow()

    init {
        loadProfile()
    }

    private fun loadProfile() {
        viewModelScope.launch {
            authRepository.getProfile().onSuccess { userInfo ->
                _state.value = _state.value.copy(
                    walkerName = userInfo.name,
                    walkerPhotoUrl = userInfo.photoUrl
                )
            }
        }
    }

    fun loadReviews() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, errorMessage = null)

            val result = walkRepository.getMyReviews()
            result.fold(
                onSuccess = { reviews ->
                    val deferred = reviews.map { review ->
                        async { mapReviewToUI(review) }
                    }

                    val uiReviews = deferred.awaitAll()

                    val avgRating = if (uiReviews.isNotEmpty()) uiReviews.map { it.rating }.average() else 0.0

                    val distribution = mutableMapOf<Int, Int>()
                    for (i in 1..5) {
                        distribution[i] = uiReviews.count { it.rating == i }
                    }

                    _state.value = _state.value.copy(
                        isLoading = false,
                        reviews = uiReviews,
                        averageRating = avgRating,
                        ratingDistribution = distribution
                    )
                },
                onFailure = { error ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        errorMessage = error.message ?: "Error al cargar reseñas"
                    )
                }
            )
        }
    }

    private suspend fun mapReviewToUI(review: Review): ReviewUiModel {
        return try {
            val walkResult = walkRepository.getDetail(review.walkId)
            walkResult.fold(
                onSuccess = { walk ->
                    ReviewUiModel(
                        id = review.id,
                        userName = walk.owner.name,
                        userPhotoUrl = walk.owner.photoUrl,
                        petName = walk.pet.name,
                        rating = review.rating,
                        comment = review.comment ?: "",
                        timeAgo = formatTimeAgo(review.createdAt)
                    )
                },
                onFailure = {
                    ReviewUiModel(
                        id = review.id,
                        userName = "Dueño",
                        userPhotoUrl = null,
                        petName = "",
                        rating = review.rating,
                        comment = review.comment ?: "",
                        timeAgo = formatTimeAgo(review.createdAt)
                    )
                }
            )
        } catch (e: Exception) {
            ReviewUiModel(
                id = review.id,
                userName = "Dueño",
                userPhotoUrl = null,
                petName = "",
                rating = review.rating,
                comment = review.comment ?: "",
                timeAgo = formatTimeAgo(review.createdAt)
            )
        }
    }

    private fun formatTimeAgo(createdAt: String?): String {
        if (createdAt.isNullOrBlank()) return ""
        return try {
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            val localDateTime = LocalDateTime.parse(createdAt, formatter)
            val instant = localDateTime.atZone(ZoneId.systemDefault()).toInstant()

            val now = Instant.now()
            val duration = Duration.between(instant, now)
            val seconds = duration.seconds

            when {
                seconds < 60 -> "unos segundos"
                seconds < 3600 -> {
                    val m = seconds / 60
                    "$m ${if (m == 1L) "minuto" else "minutos"}"
                }
                seconds < 86400 -> {
                    val h = seconds / 3600
                    "$h ${if (h == 1L) "hora" else "horas"}"
                }
                seconds < 604800 -> {
                    val d = seconds / 86400
                    "$d ${if (d == 1L) "día" else "días"}"
                }
                seconds < 2592000 -> {
                    val w = seconds / 604800
                    "$w ${if (w == 1L) "semana" else "semanas"}"
                }
                else -> {
                    val zdt = instant.atZone(ZoneId.systemDefault())
                    String.format(Locale.getDefault(), "%02d/%02d/%d", zdt.dayOfMonth, zdt.monthValue, zdt.year)
                }
            }
        } catch (e: Exception) {
            ""
        }
    }

    fun logout(onLogoutComplete: () -> Unit) {
        viewModelScope.launch {
            authRepository.logout()
            onLogoutComplete()
        }
    }
}

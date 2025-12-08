package com.mdavila_2001.gopuppy.ui.viewmodels.walker

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.mdavila_2001.gopuppy.data.remote.models.review.Review
import com.mdavila_2001.gopuppy.data.repository.AuthRepository
import com.mdavila_2001.gopuppy.data.repository.WalkRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

data class ProfileReviewUiModel(
    val id: Int,
    val userName: String,
    val userPhotoUrl: String?,
    val petName: String,
    val rating: Int,
    val comment: String,
    val timeAgo: String
)

data class WalkerProfileState(
    val name: String = "",
    val email: String = "",
    val pricePerHour: String = "",
    val photoUrl: String? = null,
    val rating: Double = 0.0,
    val totalReviews: Int = 0,
    val firstReview: ProfileReviewUiModel? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null
)

class WalkerProfileViewModel(application: Application) : AndroidViewModel(application) {
    private val authRepository = AuthRepository(application.applicationContext)
    private val walkRepository = WalkRepository()

    private val _state = MutableStateFlow(WalkerProfileState())
    val state: StateFlow<WalkerProfileState> = _state.asStateFlow()

    init {
        loadProfile()
    }

    fun loadProfile() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, errorMessage = null)
            
            authRepository.getProfile().fold(
                onSuccess = { userInfo ->
                    _state.value = _state.value.copy(
                        name = userInfo.name,
                        email = userInfo.email,
                        photoUrl = userInfo.photoUrl
                    )
                },
                onFailure = { error ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        errorMessage = error.message ?: "Error al cargar el perfil"
                    )
                }
            )
            
            walkRepository.getMyReviews().fold(
                onSuccess = { reviews ->
                    val avgRating = if (reviews.isNotEmpty()) {
                        reviews.map { it.rating }.average()
                    } else {
                        0.0
                    }
                    
                    val firstReviewUi = reviews.firstOrNull()?.let { review ->
                        mapReviewToUI(review)
                    }

                    _state.value = _state.value.copy(
                        rating = avgRating,
                        totalReviews = reviews.size,
                        firstReview = firstReviewUi,
                        isLoading = false
                    )
                },
                onFailure = {
                    _state.value = _state.value.copy(isLoading = false)
                }
            )
        }
    }

    private suspend fun mapReviewToUI(review: Review): ProfileReviewUiModel {
        return try {
            val walkResult = walkRepository.getDetail(review.walkId)
            walkResult.fold(
                onSuccess = { walk ->
                    ProfileReviewUiModel(
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
                    ProfileReviewUiModel(
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
            ProfileReviewUiModel(
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

    fun updateProfile(name: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, errorMessage = null)
            
            _state.value = _state.value.copy(
                name = name,
                isLoading = false,
                successMessage = "Cambios guardados exitosamente"
            )
        }
    }

    fun logout(onLogoutComplete: () -> Unit) {
        viewModelScope.launch {
            authRepository.logout()
            onLogoutComplete()
        }
    }

    fun clearMessages() {
        _state.value = _state.value.copy(
            errorMessage = null,
            successMessage = null
        )
    }
}

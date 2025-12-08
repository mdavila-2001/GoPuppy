package com.mdavila_2001.gopuppy.ui.views.walker_reviews

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.mdavila_2001.gopuppy.data.repository.AuthRepository
import com.mdavila_2001.gopuppy.data.repository.WalkRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class WalkerReviewsState(
    val isLoading: Boolean = false,
    val reviews: List<Review> = emptyList(),
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
            _state.value = _state.value.copy(isLoading = true)

            // Obtener reseñas de la API
            val result = walkRepository.getMyReviews()
            
            result.fold(
                onSuccess = { walkReviews ->
                    // Convertir WalkReview a Review para la UI
                    // Nota: WalkReview no incluye userName, petName, timeAgo, ni likes
                    // Por ahora mostramos solo rating y comentario
                    val reviews = walkReviews.map { walkReview ->
                        Review(
                            id = walkReview.id,
                            userName = "Usuario", // No disponible en la API
                            userPhotoUrl = null,
                            petName = "", // No disponible en la API
                            rating = walkReview.rating,
                            comment = walkReview.comment ?: "Sin comentario",
                            timeAgo = walkReview.createdAt ?: "Fecha no disponible",
                            likes = 0 // No disponible en la API
                        )
                    }

                    // Calcular rating promedio
                    val avgRating = if (reviews.isNotEmpty()) {
                        reviews.map { it.rating }.average()
                    } else {
                        0.0
                    }

                    // Calcular distribución de ratings
                    val distribution = mutableMapOf<Int, Int>()
                    for (i in 1..5) {
                        distribution[i] = reviews.count { it.rating == i }
                    }

                    _state.value = _state.value.copy(
                        isLoading = false,
                        reviews = reviews,
                        averageRating = avgRating,
                        ratingDistribution = distribution
                    )
                },
                onFailure = { error ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        errorMessage = error.message ?: "Error al cargar reseñas",
                        reviews = emptyList(),
                        averageRating = 0.0,
                        ratingDistribution = emptyMap()
                    )
                }
            )
        }
    }

    fun logout(onLogoutComplete: () -> Unit) {
        viewModelScope.launch {
            authRepository.logout()
            onLogoutComplete()
        }
    }
}

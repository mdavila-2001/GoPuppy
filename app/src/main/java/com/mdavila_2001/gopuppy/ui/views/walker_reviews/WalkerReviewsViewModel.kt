package com.mdavila_2001.gopuppy.ui.views.walker_reviews

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.mdavila_2001.gopuppy.data.repository.AuthRepository
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

            // TODO: Implementar llamada al API cuando esté disponible
            // Por ahora usamos datos de ejemplo
            val mockReviews = listOf(
                Review(
                    id = 1,
                    userName = "María G.",
                    userPhotoUrl = null,
                    petName = "Max",
                    rating = 5,
                    comment = "¡Excelente paseador! Siempre puntual y mi perro Max lo adora. Manda fotos durante el paseo, lo cual es un gran detalle. ¡Totalmente recomendada!",
                    timeAgo = "2 días",
                    likes = 12
                ),
                Review(
                    id = 2,
                    userName = "Carlos P.",
                    userPhotoUrl = null,
                    petName = "Luna",
                    rating = 4,
                    comment = "Buen servicio en general. Luna volvió contenta y cansada, lo que indica que tuvieron un buen paseo. La hora de recogida pero me avisó con tiempo.",
                    timeAgo = "1 semana",
                    likes = 8
                ),
                Review(
                    id = 3,
                    userName = "Ana L.",
                    userPhotoUrl = null,
                    petName = "Rocky",
                    rating = 5,
                    comment = "Rocky siempre se emociona cuando ve que es hora de su paseo. El trato es inmejorable y se nota el cariño con el que cuida a las mascotas. ¡Gracias!",
                    timeAgo = "3 semanas",
                    likes = 5
                ),
                Review(
                    id = 4,
                    userName = "Juan Pérez",
                    userPhotoUrl = null,
                    petName = "Toby",
                    rating = 5,
                    comment = "Excelente profesional. Toby llegó feliz y cansado después del paseo. Muy confiable y responsable.",
                    timeAgo = "1 mes",
                    likes = 15
                ),
                Review(
                    id = 5,
                    userName = "Laura M.",
                    userPhotoUrl = null,
                    petName = "Bella",
                    rating = 5,
                    comment = "La mejor paseadora que hemos tenido. Bella siempre regresa feliz y bien cuidada.",
                    timeAgo = "1 mes",
                    likes = 10
                )
            )

            // Calcular rating promedio
            val avgRating = if (mockReviews.isNotEmpty()) {
                mockReviews.map { it.rating }.average()
            } else {
                0.0
            }

            // Calcular distribución de ratings
            val distribution = mutableMapOf<Int, Int>()
            for (i in 1..5) {
                distribution[i] = mockReviews.count { it.rating == i }
            }

            _state.value = _state.value.copy(
                isLoading = false,
                reviews = mockReviews,
                averageRating = avgRating,
                ratingDistribution = distribution
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

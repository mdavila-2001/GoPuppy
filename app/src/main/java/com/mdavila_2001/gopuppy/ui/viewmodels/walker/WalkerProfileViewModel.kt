package com.mdavila_2001.gopuppy.ui.viewmodels.walker

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.mdavila_2001.gopuppy.data.remote.models.walk.WalkReview
import com.mdavila_2001.gopuppy.data.repository.AuthRepository
import com.mdavila_2001.gopuppy.data.repository.WalkRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class WalkerProfileState(
    val name: String = "",
    val email: String = "",
    val pricePerHour: String = "",
    val photoUrl: String? = null,
    val rating: Double = 0.0,
    val totalReviews: Int = 0,
    val firstReview: WalkReview? = null,
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
            
            // Obtener perfil del usuario
            authRepository.getProfile().fold(
                onSuccess = { userInfo ->
                    _state.value = _state.value.copy(
                        name = userInfo.name,
                        email = userInfo.email,
                        photoUrl = userInfo.photoUrl,
                        pricePerHour = "$15"
                    )
                },
                onFailure = { error ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        errorMessage = error.message ?: "Error al cargar el perfil"
                    )
                }
            )
            
            // Obtener reseñas
            walkRepository.getMyReviews().fold(
                onSuccess = { reviews ->
                    val avgRating = if (reviews.isNotEmpty()) {
                        reviews.map { it.rating }.average()
                    } else {
                        0.0
                    }
                    
                    _state.value = _state.value.copy(
                        rating = avgRating,
                        totalReviews = reviews.size,
                        firstReview = reviews.firstOrNull(),
                        isLoading = false
                    )
                },
                onFailure = {
                    // Si falla la carga de reviews, solo marcamos como no loading
                    _state.value = _state.value.copy(isLoading = false)
                }
            )
        }
    }

    fun updateProfile(name: String, email: String, pricePerHour: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, errorMessage = null)
            
            // TODO: Implementar actualización de perfil cuando el endpoint esté disponible
            // Por ahora solo actualizamos el estado local
            _state.value = _state.value.copy(
                name = name,
                email = email,
                pricePerHour = pricePerHour,
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

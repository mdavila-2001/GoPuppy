package com.mdavila_2001.gopuppy.ui.views.walker_profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mdavila_2001.gopuppy.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class WalkerProfileState(
    val name: String = "",
    val email: String = "",
    val bio: String = "",
    val pricePerHour: String = "",
    val experience: String = "",
    val photoUrl: String? = null,
    val rating: Double = 0.0,
    val totalReviews: Int = 0,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null
)

class WalkerProfileViewModel : ViewModel() {
    private val authRepository = AuthRepository()

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
                        photoUrl = userInfo.photoUrl,
                        // TODO: Agregar más campos cuando estén disponibles en la API
                        bio = "Soy una apasionada de los animales con más de 5 años de experiencia cuidando y paseando perritos de todas las razas y tamaños. ¡Tu mejor amigo estará en buenas manos!",
                        pricePerHour = "$15",
                        experience = "5 años",
                        rating = 4.8,
                        totalReviews = 0,
                        isLoading = false
                    )
                },
                onFailure = { error ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        errorMessage = error.message ?: "Error al cargar el perfil"
                    )
                }
            )
        }
    }

    fun updateProfile(name: String, email: String, bio: String, pricePerHour: String, experience: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, errorMessage = null)
            
            // TODO: Implementar actualización de perfil cuando el endpoint esté disponible
            // Por ahora solo actualizamos el estado local
            _state.value = _state.value.copy(
                name = name,
                email = email,
                bio = bio,
                pricePerHour = pricePerHour,
                experience = experience,
                isLoading = false,
                successMessage = "Cambios guardados exitosamente"
            )
        }
    }

    fun clearMessages() {
        _state.value = _state.value.copy(
            errorMessage = null,
            successMessage = null
        )
    }
}

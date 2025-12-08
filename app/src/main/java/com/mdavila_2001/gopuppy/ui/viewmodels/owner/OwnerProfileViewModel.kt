package com.mdavila_2001.gopuppy.ui.viewmodels.owner

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.mdavila_2001.gopuppy.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class OwnerProfileState(
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val photoUrl: String? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null
)

class OwnerProfileViewModel(application: Application) : AndroidViewModel(application) {
    private val authRepository = AuthRepository(application.applicationContext)

    private val _state = MutableStateFlow(OwnerProfileState())
    val state: StateFlow<OwnerProfileState> = _state.asStateFlow()

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

    fun updateProfile(name: String, email: String, phone: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, errorMessage = null)
            
            // TODO: Implementar actualización de perfil cuando el endpoint esté disponible
            // Por ahora solo actualizamos el estado local
            _state.value = _state.value.copy(
                name = name,
                email = email,
                phone = phone,
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

package com.mdavila_2001.gopuppy.ui.views.login

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mdavila_2001.gopuppy.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class LoginUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null,
    val isWalker: Boolean = false,
    val userName: String? = null
)

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val authRepository = AuthRepository(application.applicationContext)
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun login(email: String, password: String, isWalker: Boolean) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessage = null
            )

            val result = authRepository.login(email, password, isWalker)

            result.fold(
                onSuccess = { loginResponse ->
                    // Obtener el perfil del usuario para obtener su nombre
                    val profileResult = authRepository.getProfile()
                    profileResult.fold(
                        onSuccess = { userInfo ->
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                isSuccess = true,
                                isWalker = isWalker,
                                userName = userInfo.name
                            )
                        },
                        onFailure = {
                            // Si falla obtener el perfil, aún así consideramos exitoso el login
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                isSuccess = true,
                                isWalker = isWalker,
                                userName = "Usuario"
                            )
                        }
                    )
                },
                onFailure = { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isSuccess = false,
                        errorMessage = exception.message ?: "Error al iniciar sesión"
                    )
                }
            )
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    fun resetState() {
        _uiState.value = LoginUiState()
    }
}

package com.mdavila_2001.gopuppy.ui.views.register

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mdavila_2001.gopuppy.data.remote.models.auth.signup.OwnerSignupDTO
import com.mdavila_2001.gopuppy.data.remote.models.auth.signup.WalkerSignupDTO
import com.mdavila_2001.gopuppy.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class RegisterUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null,
    val isWalker: Boolean = false
)

class RegisterViewModel(application: Application) : AndroidViewModel(application) {

    private val authRepository: AuthRepository = AuthRepository(application.applicationContext)
    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    fun register(
        name: String,
        email: String,
        password: String,
        confirmPassword: String,
        isWalker: Boolean,
        pricePerHour: String? = null
    ) {
        viewModelScope.launch {
            // Validaciones básicas
            if (name.isBlank() || email.isBlank() || password.isBlank()) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Todos los campos son obligatorios"
                )
                return@launch
            }

            if (password != confirmPassword) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Las contraseñas no coinciden"
                )
                return@launch
            }

            if (isWalker && pricePerHour.isNullOrBlank()) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "El precio por hora es obligatorio para paseadores"
                )
                return@launch
            }

            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessage = null
            )

            val result = if (isWalker) {
                val walkerData = WalkerSignupDTO(
                    name = name,
                    email = email,
                    password = password,
                    priceHour = pricePerHour ?: ""
                )
                authRepository.WalkerSignup(walkerData)
            } else {
                val ownerData = OwnerSignupDTO(
                    name = name,
                    email = email,
                    password = password
                )
                authRepository.OwnerSignup(ownerData)
            }

            result.fold(
                onSuccess = { authResponse ->
                    // Después del registro exitoso, hacer login para obtener un token real
                    viewModelScope.launch {
                        val loginResult = authRepository.login(email, password, isWalker)
                        loginResult.fold(
                            onSuccess = { loginResponse ->
                                _uiState.value = _uiState.value.copy(
                                    isLoading = false,
                                    isSuccess = true,
                                    isWalker = isWalker
                                )
                            },
                            onFailure = { loginException ->
                                // Si el login falla, aún consideramos el registro exitoso
                                _uiState.value = _uiState.value.copy(
                                    isLoading = false,
                                    isSuccess = true,
                                    isWalker = isWalker,
                                    errorMessage = "Registro exitoso pero error al iniciar sesión automáticamente"
                                )
                            }
                        )
                    }
                },
                onFailure = { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isSuccess = false,
                        errorMessage = exception.message ?: "Error al registrarse"
                    )
                }
            )
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    fun resetState() {
        _uiState.value = RegisterUiState()
    }
}

package com.mdavila_2001.gopuppy.ui.views.register

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.mdavila_2001.gopuppy.data.remote.models.auth.signup.OwnerSignupDTO
import com.mdavila_2001.gopuppy.data.remote.models.auth.signup.WalkerSignupDTO
import com.mdavila_2001.gopuppy.data.repository.AuthRepository
import com.mdavila_2001.gopuppy.data.remote.network.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File

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
        pricePerHour: String? = null,
        photoFile: File? = null
    ) {
        viewModelScope.launch {
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
                onSuccess = {
                    try {
                        val currentToken = RetrofitInstance.authToken
                        val tokenLooksInvalid = currentToken.isNullOrEmpty() || currentToken == "registered"

                        if (tokenLooksInvalid) {
                            Log.d("RegisterVM", "Token ausente o placeholder ('$currentToken'); intentando login automático para poder subir la foto...")
                            val loginResult = authRepository.login(email, password, isWalker)
                            if (loginResult.isFailure) {
                                Log.e("RegisterVM", "Login automático falló: ${loginResult.exceptionOrNull()?.message}")
                            } else {
                                Log.d("RegisterVM", "Login automático exitoso; token ahora: ${RetrofitInstance.authToken?.take(30) ?: "NULL"}")
                            }
                        } else {
                            Log.d("RegisterVM", "Token presente tras registro: ${currentToken?.take(30) ?: "NULL"}")
                        }

                        if (photoFile != null) {
                            if (!photoFile.exists() || photoFile.length() == 0L) {
                                val msg = "El archivo de foto no existe o está vacío: ${photoFile.path}"
                                Log.e("RegisterVM", msg)
                                _uiState.value = _uiState.value.copy(
                                    isLoading = false,
                                    isSuccess = true,
                                    isWalker = isWalker,
                                    errorMessage = "Registro OK, pero la foto no se pudo subir: $msg"
                                )
                                return@launch
                            }

                            val uploadResult = if (isWalker) {
                                authRepository.uploadWalkerPhoto(photoFile)
                            } else {
                                authRepository.uploadOwnerPhoto(photoFile)
                            }

                            if (uploadResult.isSuccess) {
                                Log.d("RegisterVM", "Foto de perfil subida correctamente")
                            } else {
                                val msg = uploadResult.exceptionOrNull()?.message ?: "Error subiendo foto"
                                Log.e("RegisterVM", "Error subiendo foto: $msg")
                                _uiState.value = _uiState.value.copy(
                                    isLoading = false,
                                    isSuccess = true,
                                    isWalker = isWalker,
                                    errorMessage = "Registro OK, pero fallo al subir foto: $msg"
                                )
                                return@launch
                            }
                        }

                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            isSuccess = true,
                            isWalker = isWalker
                        )
                    } catch (e: Exception) {
                        Log.e("RegisterVM", "Error en post-registro: ${e.message}")
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            isSuccess = true,
                            isWalker = isWalker,
                            errorMessage = "Registro completado, pero hubo un error adicional: ${e.message}"
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

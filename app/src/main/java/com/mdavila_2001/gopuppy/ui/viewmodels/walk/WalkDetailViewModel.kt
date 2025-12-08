package com.mdavila_2001.gopuppy.ui.viewmodels.walk

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.mdavila_2001.gopuppy.data.remote.models.walk.Walk
import com.mdavila_2001.gopuppy.data.repository.AuthRepository
import com.mdavila_2001.gopuppy.data.repository.WalkRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File

data class WalkDetailUiState(
    val isLoading: Boolean = false,
    val walk: Walk? = null,
    val errorMessage: String? = null,
    val successMessage: String? = null
)

class WalkDetailViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = WalkRepository()
    private val authRepository = AuthRepository(application.applicationContext)
    private val _uiState = MutableStateFlow(WalkDetailUiState())
    val uiState: StateFlow<WalkDetailUiState> = _uiState.asStateFlow()

    fun loadWalkDetails(walkId: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            repository.getDetail(walkId)
                .onSuccess { walk ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        walk = walk,
                        errorMessage = null
                    )
                }
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Error cargando paseo"
                    )
                }
        }
    }

    fun startWalk() {
        val currentWalk = uiState.value.walk ?: return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            repository.startWalk(currentWalk.id)
                .onSuccess {
                    // Recargamos el detalle para ver el nuevo estado
                    loadWalkDetails(currentWalk.id)
                    _uiState.value = _uiState.value.copy(successMessage = "¡Paseo iniciado!")
                }
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "No se pudo iniciar: ${e.message}"
                    )
                }
        }
    }

    fun endWalk() {
        val currentWalk = uiState.value.walk ?: return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            repository.endWalk(currentWalk.id)
                .onSuccess {
                    loadWalkDetails(currentWalk.id)
                    _uiState.value = _uiState.value.copy(successMessage = "¡Paseo finalizado correctamente!")
                }
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Error al finalizar: ${e.message}"
                    )
                }
        }
    }

    fun uploadPhoto(file: File) {
        val currentWalk = uiState.value.walk ?: return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            repository.uploadWalkPhoto(currentWalk.id, file)
                .onSuccess {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        successMessage = "Foto subida correctamente"
                    )
                }
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Error subiendo foto: ${e.message}"
                    )
                }
        }
    }

    fun clearMessages() {
        _uiState.value = _uiState.value.copy(errorMessage = null, successMessage = null)
    }

    fun logout(onLogoutComplete: () -> Unit) {
        viewModelScope.launch {
            authRepository.logout()
            onLogoutComplete()
        }
    }
}

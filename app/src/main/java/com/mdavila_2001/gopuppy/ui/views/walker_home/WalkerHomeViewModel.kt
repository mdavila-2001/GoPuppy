package com.mdavila_2001.gopuppy.ui.views.walker_home

import android.app.Application
import android.content.Intent
import android.os.Build
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.mdavila_2001.gopuppy.data.remote.models.walk.Walk
import com.mdavila_2001.gopuppy.data.repository.AuthRepository
import com.mdavila_2001.gopuppy.data.repository.WalkRepository
import com.mdavila_2001.gopuppy.data.repository.WalkerRepository
import com.mdavila_2001.gopuppy.services.LocationService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class WalkerHomeUiState(
    val isAvailable: Boolean = false,
    val isLoading: Boolean = false,
    val newRequests: List<Walk> = emptyList(),
    val upcomingWalks: List<Walk> = emptyList(),
    val errorMessage: String? = null,
    val currentWalkerId: Int? = null,
    val userName: String = "Paseador"
)
class WalkerHomeViewModel(application: Application) : AndroidViewModel(application) {
    private val walkerRepository = WalkerRepository()
    private val walkRepository = WalkRepository()
    private val authRepository = AuthRepository(application.applicationContext)
    private val context = application.applicationContext

    private val _uiState = MutableStateFlow(WalkerHomeUiState())
    val uiState: StateFlow<WalkerHomeUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            // Cargar perfil del paseador
            authRepository.getProfile()
                .onSuccess { userInfo ->
                    _uiState.value = _uiState.value.copy(userName = userInfo.name)
                }
                .onFailure { /* Silencioso, usar valor por defecto */ }
            _uiState.value = _uiState.value.copy(isLoading = true)

            val profileResult = authRepository.getProfile()
            val myId = profileResult.getOrNull()?.id

            if (myId == null) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "No se pudo identificar al paseador."
                )
                return@launch
            }

            _uiState.value = _uiState.value.copy(currentWalkerId = myId)

            val pendingResult = walkRepository.getPending()
            val acceptedResult = walkRepository.getAccepted()

            val allPending = pendingResult.getOrDefault(emptyList())
            val allAccepted = acceptedResult.getOrDefault(emptyList())

            val myPending = allPending.filter { it.walkerId == myId }
            val myAccepted = allAccepted.filter { it.walkerId == myId }

            _uiState.value = _uiState.value.copy(
                isLoading = false,
                newRequests = myPending,
                upcomingWalks = myAccepted
            )
        }
    }

    fun toggleAvailability(isChecked: Boolean) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            val result = walkerRepository.setAvailability(isChecked)

            result.onSuccess {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isAvailable = isChecked
                )

                val serviceIntent = Intent(context, LocationService::class.java)

                if (isChecked) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        context.startForegroundService(serviceIntent)
                    } else {
                        context.startService(serviceIntent)
                    }
                } else {
                    context.stopService(serviceIntent)
                }

            }.onFailure { e ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Error al cambiar disponibilidad"
                )
            }
        }
    }

    fun acceptRequest(walkId: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            walkRepository.acceptWalk(walkId)
                .onSuccess {
                    loadData()
                }
                .onFailure {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Error al aceptar la solicitud"
                    )
                }
        }
    }

    fun rejectRequest(walkId: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            walkRepository.rejectWalk(walkId)
                .onSuccess {
                    loadData()
                }
                .onFailure {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Error al rechazar la solicitud"
                    )
                }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
        }
    }
}
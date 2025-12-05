package com.mdavila_2001.gopuppy.ui.views.walker_home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.mdavila_2001.gopuppy.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class WalkerHomeViewModel(application: Application) : AndroidViewModel(application) {
    private val authRepository = AuthRepository(application.applicationContext)
    private val _state = MutableStateFlow(WalkerHomeState())
    val state: StateFlow<WalkerHomeState> = _state.asStateFlow()

    // Simulación de datos
    init {
        loadUserName()
        _state.value = WalkerHomeState(
            upcomingWalks = listOf(
                WalkerWalkUiModel(1, "HOY", "10:00 AM", 60, "Parque Hundido, CDMX", "Fido", "Golden Retriever"),
                WalkerWalkUiModel(2, "MAÑANA", "5:00 PM", 30, "Colonia Roma, CDMX", "Chato", "Pug")
            ),
            newRequests = listOf(
                WalkerRequestUiModel(3, "Mañana", "4:00 PM", 30, "Max", "Bulldog Francés"),
                WalkerRequestUiModel(4, "Viernes", "9:00 AM", 60, "Luna", "Border Collie")
            )
        )
    }
    
    private fun loadUserName() {
        viewModelScope.launch {
            authRepository.getProfile().onSuccess { userInfo ->
                _state.value = _state.value.copy(userName = userInfo.name)
            }
        }
    }

    fun acceptRequest(requestId: Int) {
        // TODO: Implementar lógica de aceptar solicitud
    }

    fun rejectRequest(requestId: Int) {
        // TODO: Implementar lógica de rechazar solicitud
    }
}

data class WalkerHomeState(
    val upcomingWalks: List<WalkerWalkUiModel> = emptyList(),
    val newRequests: List<WalkerRequestUiModel> = emptyList(),
    val userName: String = "Paseador"
)

class WalkerHomeViewModelFactory(
    private val application: Application
) : androidx.lifecycle.ViewModelProvider.Factory {
    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WalkerHomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return WalkerHomeViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

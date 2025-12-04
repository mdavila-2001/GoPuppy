package com.mdavila_2001.gopuppy.ui.views.walker_home

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class WalkerHomeViewModel : ViewModel() {
    private val _state = MutableStateFlow(WalkerHomeState())
    val state: StateFlow<WalkerHomeState> = _state.asStateFlow()

    // Simulación de datos
    init {
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

    fun acceptRequest(requestId: Int) {
        // TODO: Implementar lógica de aceptar solicitud
    }

    fun rejectRequest(requestId: Int) {
        // TODO: Implementar lógica de rechazar solicitud
    }
}

data class WalkerHomeState(
    val upcomingWalks: List<WalkerWalkUiModel> = emptyList(),
    val newRequests: List<WalkerRequestUiModel> = emptyList()
)

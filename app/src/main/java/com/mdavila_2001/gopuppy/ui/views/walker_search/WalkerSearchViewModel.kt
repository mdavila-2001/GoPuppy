package com.mdavila_2001.gopuppy.ui.views.walker_search

import android.util.Log
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class WalkerSearchViewModel : ViewModel() {
    private val _state = MutableStateFlow(WalkerSearchState())
    val state: StateFlow<WalkerSearchState> = _state.asStateFlow()

    init {
        Log.d("WalkerSearchVM", "Inicializando ViewModel")
        // TODO: Cargar paseadores reales del backend cuando esté listo
        _state.value = WalkerSearchState(
            walkers = emptyList(),
            isLoading = false
        )
    }

    private fun loadWalkers() {
        // TODO: Conectar con WalkerRepository para obtener paseadores cercanos
        Log.d("WalkerSearchVM", "Cargando paseadores del backend...")
    }

    fun updateSearchQuery(query: String) {
        _state.value = _state.value.copy(searchQuery = query)
        // TODO: Filtrar paseadores por nombre
    }

    fun updateCalification(calification: String) {
        _state.value = _state.value.copy(selectedCalification = calification)
        // TODO: Filtrar por calificación
    }

    fun updatePrice(price: String) {
        _state.value = _state.value.copy(selectedPrice = price)
        // TODO: Filtrar por precio
    }

    fun updateDistance(distance: String) {
        _state.value = _state.value.copy(selectedDistance = distance)
        // TODO: Filtrar por distancia
    }
}

data class WalkerSearchState(
    val walkers: List<WalkerUiModel> = emptyList(),
    val searchQuery: String = "",
    val selectedCalification: String = "Calificación",
    val selectedPrice: String = "Precio",
    val selectedDistance: String = "Distancia",
    val isLoading: Boolean = false
)

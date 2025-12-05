package com.mdavila_2001.gopuppy.ui.views.walk_history

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

data class WalkHistoryState(
    val walks: List<Walk> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val searchQuery: String = "",
    val selectedFilter: String = "Todos", // Todos, Completados, Cancelados, etc.
    val userName: String = "Usuario"
)

class WalkHistoryViewModel(application: Application) : AndroidViewModel(application) {
    private val walkRepository = WalkRepository()
    private val authRepository = AuthRepository(application.applicationContext)

    private val _state = MutableStateFlow(WalkHistoryState())
    val state: StateFlow<WalkHistoryState> = _state.asStateFlow()

    init {
        loadWalkHistory()
        loadUserName()
    }
    
    private fun loadUserName() {
        viewModelScope.launch {
            authRepository.getProfile().onSuccess { userInfo ->
                _state.value = _state.value.copy(userName = userInfo.name)
            }
        }
    }

    fun loadWalkHistory() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, errorMessage = null)
            
            walkRepository.getHistory().fold(
                onSuccess = { walks ->
                    _state.value = _state.value.copy(
                        walks = walks,
                        isLoading = false
                    )
                },
                onFailure = { error ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        errorMessage = error.message ?: "Error al cargar el historial"
                    )
                }
            )
        }
    }

    fun updateSearchQuery(query: String) {
        _state.value = _state.value.copy(searchQuery = query)
    }

    fun updateFilter(filter: String) {
        _state.value = _state.value.copy(selectedFilter = filter)
    }

    fun getFilteredWalks(): List<Walk> {
        val walks = _state.value.walks
        val query = _state.value.searchQuery.lowercase()
        val filter = _state.value.selectedFilter

        return walks.filter { walk ->
            // Filtrar por búsqueda
            val matchesSearch = if (query.isBlank()) {
                true
            } else {
                walk.pet.name.lowercase().contains(query) ||
                walk.walker.name.lowercase().contains(query)
            }

            // Filtrar por estado
            val matchesFilter = when (filter) {
                "Todos" -> true
                "Completados" -> walk.status == "completed"
                "Cancelados" -> walk.status == "cancelled"
                "En curso" -> walk.status == "in_progress"
                "Pendientes" -> walk.status == "pending"
                else -> true
            }

            matchesSearch && matchesFilter
        }
    }

    fun clearError() {
        _state.value = _state.value.copy(errorMessage = null)
    }
}

class WalkHistoryViewModelFactory(
    private val application: Application
) : androidx.lifecycle.ViewModelProvider.Factory {
    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WalkHistoryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return WalkHistoryViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

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
    val reviewedWalkIds: Set<Int> = emptySet(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val searchQuery: String = "",
    val selectedFilter: String = "Todos",
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
            
            val historyResult = walkRepository.getHistory()
            val reviewsResult = walkRepository.getMyReviews()

            val reviewedIds = reviewsResult.getOrDefault(emptyList()).map { it.walkId }.toSet()

            historyResult.fold(
                onSuccess = { walks ->
                    _state.value = _state.value.copy(
                        walks = walks,
                        reviewedWalkIds = reviewedIds,
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
            val matchesSearch = if (query.isBlank()) {
                true
            } else {
                walk.pet.name.lowercase().contains(query) ||
                walk.walker.name.lowercase().contains(query)
            }

            val matchesFilter = when (filter) {
                "Todos" -> true
                "Completados" -> walk.status == "finished"
                "Cancelados" -> walk.status == "cancelled"
                "En curso" -> walk.status == "in_progress"
                "Pendientes" -> walk.status == "pending"
                else -> true
            }

            matchesSearch && matchesFilter
        }
    }

    fun hasReview(walkId: Int): Boolean {
        return walkId in _state.value.reviewedWalkIds
    }

    fun clearError() {
        _state.value = _state.value.copy(errorMessage = null)
    }

    fun clearMessages() {
        _state.value = _state.value.copy(errorMessage = null, successMessage = null)
    }

    fun submitReview(walkId: Int, rating: Int, comment: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)

            walkRepository.sendReview(walkId, rating, comment)
                .onSuccess {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        successMessage = "¡Gracias por tu calificación!"
                    )
                    loadWalkHistory()
                }
                .onFailure { e ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        errorMessage = "Error al enviar calificación: ${e.message}"
                    )
                }
        }
    }

    fun logout(onLogoutComplete: () -> Unit) {
        viewModelScope.launch {
            authRepository.logout()
            onLogoutComplete()
        }
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

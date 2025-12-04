package com.mdavila_2001.gopuppy.ui.views.walk_detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mdavila_2001.gopuppy.data.remote.models.walk.Walk
import com.mdavila_2001.gopuppy.data.repository.WalkRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class WalkDetailState(
    val walk: Walk? = null,
    val photos: List<String> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class WalkDetailViewModel : ViewModel() {
    private val walkRepository = WalkRepository()

    private val _state = MutableStateFlow(WalkDetailState())
    val state: StateFlow<WalkDetailState> = _state.asStateFlow()

    fun loadWalkDetail(walkId: Int) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, errorMessage = null)
            
            walkRepository.getDetail(walkId).fold(
                onSuccess = { walk ->
                    _state.value = _state.value.copy(
                        walk = walk,
                        isLoading = false
                    )
                    // Cargar fotos si el paseo está finalizado
                    if (walk.status == "completed") {
                        loadWalkPhotos(walkId)
                    }
                },
                onFailure = { error ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        errorMessage = error.message ?: "Error al cargar detalles del paseo"
                    )
                }
            )
        }
    }

    private fun loadWalkPhotos(walkId: Int) {
        viewModelScope.launch {
            walkRepository.getWalkPhotos(walkId).fold(
                onSuccess = { photos ->
                    _state.value = _state.value.copy(photos = photos)
                },
                onFailure = { error ->
                    // No es crítico si falla la carga de fotos
                }
            )
        }
    }

    fun clearError() {
        _state.value = _state.value.copy(errorMessage = null)
    }
}

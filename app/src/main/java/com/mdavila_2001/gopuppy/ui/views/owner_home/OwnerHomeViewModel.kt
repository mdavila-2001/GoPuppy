package com.mdavila_2001.gopuppy.ui.views.owner_home

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.mdavila_2001.gopuppy.data.remote.models.pet.Pet
import com.mdavila_2001.gopuppy.data.repository.AuthRepository
import com.mdavila_2001.gopuppy.data.repository.PetRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class OwnerHomeState(
    val isLoading: Boolean = false,
    val pets: List<Pet> = emptyList(),
    val errorMessage: String? = null
)

class OwnerHomeViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = PetRepository()
    private val authRepository = AuthRepository(application.applicationContext)

    private val _state = MutableStateFlow(OwnerHomeState())
    val state: StateFlow<OwnerHomeState> = _state

    init {
        loadPets()
    }

    fun loadPets() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, errorMessage = null)
            
            repository.getMyPets().onSuccess { pets ->
                _state.value = _state.value.copy(
                    isLoading = false,
                    pets = pets
                )
            }.onFailure { error ->
                Log.e("OwnerHomeVM", "Error al cargar mascotas: ${error.message}", error)
                _state.value = _state.value.copy(
                    isLoading = false,
                    errorMessage = error.message ?: "Error al cargar las mascotas"
                )
            }
        }
    }

    fun deletePet(petId: Int) {
        viewModelScope.launch {
            try {
                Log.d("OwnerHomeVM", "Eliminando mascota con ID: $petId")
                
                repository.deletePet(petId).onSuccess {
                    Log.d("OwnerHomeVM", "Mascota eliminada exitosamente")
                    // Recargar la lista de mascotas
                    loadPets()
                }.onFailure { error ->
                    Log.e("OwnerHomeVM", "Error al eliminar mascota: ${error.message}", error)
                    _state.value = _state.value.copy(
                        errorMessage = "Error al eliminar: ${error.message}"
                    )
                }
            } catch (e: Exception) {
                Log.e("OwnerHomeVM", "Excepción al eliminar: ${e.message}", e)
                _state.value = _state.value.copy(
                    errorMessage = "Error inesperado: ${e.message}"
                )
            }
        }
    }

    fun clearError() {
        _state.value = _state.value.copy(errorMessage = null)
    }

    fun logout(onLogoutComplete: () -> Unit) {
        viewModelScope.launch {
            authRepository.logout()
            onLogoutComplete()
        }
    }
}

package com.mdavila_2001.gopuppy.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mdavila_2001.gopuppy.data.remote.models.pet.Pet
import com.mdavila_2001.gopuppy.data.remote.models.pet.PetDTO
import com.mdavila_2001.gopuppy.data.repository.PetRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class PetFormState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val pet: Pet? = null
)

class PetFormViewModel : ViewModel() {
    private val repository = PetRepository()

    private val _state = MutableStateFlow(PetFormState())
    val state: StateFlow<PetFormState> = _state

    fun loadPet(petId: Int) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, errorMessage = null)
            
            repository.getMyPets().onSuccess { pets ->
                val pet = pets.find { it.id == petId }
                _state.value = _state.value.copy(
                    isLoading = false,
                    pet = pet
                )
            }.onFailure { error ->
                _state.value = _state.value.copy(
                    isLoading = false,
                    errorMessage = error.message ?: "Error al cargar la mascota"
                )
            }
        }
    }

    fun savePet(
        petId: Int?,
        name: String,
        species: String,
        breed: String?,
        birthdate: String?,
        notes: String?,
        onSuccess: () -> Unit
    ) {
        if (name.isBlank()) {
            _state.value = _state.value.copy(errorMessage = "El nombre es obligatorio")
            return
        }

        if (species.isBlank()) {
            _state.value = _state.value.copy(errorMessage = "El tipo es obligatorio")
            return
        }

        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, errorMessage = null)

            // Combinar breed y birthdate en las notas hasta que la API los soporte
            val combinedNotes = buildString {
                if (!breed.isNullOrBlank()) {
                    append("Raza: ${breed.trim()}")
                }
                if (!birthdate.isNullOrBlank()) {
                    if (isNotEmpty()) append("\n")
                    append("Fecha de Nacimiento: $birthdate")
                }
                if (!notes.isNullOrBlank()) {
                    if (isNotEmpty()) append("\n")
                    append(notes.trim())
                }
            }.takeIf { it.isNotBlank() }

            val petDTO = PetDTO(
                name = name.trim(),
                species = species.trim(),
                notes = combinedNotes ?: ""  // Enviar string vacío en lugar de null
            )

            Log.d("PetFormVM", "Guardando mascota: name='${petDTO.name}', species='${petDTO.species}', notes='${petDTO.notes}'")
            Log.d("PetFormVM", "PetId: $petId, isUpdate: ${petId != null && petId > 0}")

            val result = if (petId != null && petId > 0) {
                repository.updatePet(petId, petDTO)
            } else {
                repository.addPet(petDTO)
            }

            result.onSuccess { savedPet ->
                _state.value = _state.value.copy(
                    isLoading = false,
                    successMessage = if (petId != null) "Mascota actualizada" else "Mascota guardada",
                    pet = savedPet
                )
                onSuccess()
            }.onFailure { error ->
                val errorMsg = error.message ?: "Error desconocido"
                Log.e("PetFormVM", "Error al guardar: $errorMsg", error)
                _state.value = _state.value.copy(
                    isLoading = false,
                    errorMessage = "Error al guardar: $errorMsg"
                )
            }
        }
    }

    fun deletePet(petId: Int, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, errorMessage = null)

            repository.deletePet(petId).onSuccess {
                _state.value = _state.value.copy(
                    isLoading = false,
                    successMessage = "Mascota eliminada"
                )
                onSuccess()
            }.onFailure { error ->
                _state.value = _state.value.copy(
                    isLoading = false,
                    errorMessage = error.message ?: "Error al eliminar la mascota"
                )
            }
        }
    }

    fun clearMessages() {
        _state.value = _state.value.copy(errorMessage = null, successMessage = null)
    }
}

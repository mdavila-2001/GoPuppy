package com.mdavila_2001.gopuppy.ui.views.requestwalk

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mdavila_2001.gopuppy.data.remote.models.address.Address
import com.mdavila_2001.gopuppy.data.remote.models.pet.Pet
import com.mdavila_2001.gopuppy.data.remote.models.walk.WalkDTO
import com.mdavila_2001.gopuppy.data.repository.AddressRepository
import com.mdavila_2001.gopuppy.data.repository.PetRepository
import com.mdavila_2001.gopuppy.data.repository.WalkRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime

data class RequestWalkUiState(
    val isLoading: Boolean = false,
    val myPets: List<Pet> = emptyList(),
    val myAddresses: List<Address> = emptyList(),
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val isRequestSuccess: Boolean = false
)

class RequestWalkViewModel(application: Application) : AndroidViewModel(application) {
    private val walkRepository = WalkRepository()
    private val petRepository = PetRepository()
    private val addressRepository = AddressRepository()

    private val _uiState = MutableStateFlow(RequestWalkUiState())
    val uiState: StateFlow<RequestWalkUiState> = _uiState.asStateFlow()

    fun loadInitialData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            val petsResult = petRepository.getMyPets()
            val addressResult = addressRepository.getMyAddresses()

            val pets = petsResult.getOrDefault(emptyList())
            val addresses = addressResult.getOrDefault(emptyList())

            _uiState.value = _uiState.value.copy(
                isLoading = false,
                myPets = pets,
                myAddresses = addresses,
                errorMessage = if (pets.isEmpty()) "No tienes mascotas registradas. Agrega una primero." else null
            )
        }
    }

    fun submitRequest(
        walkerId: Int,
        petId: Int?,
        addressId: Int?,
        scheduledAt: String,
        durationMinutes: Int,
        notes: String
    ) {
        if (petId == null || addressId == null) {
            _uiState.value = _uiState.value.copy(errorMessage = "Debes seleccionar mascota y dirección")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            val requestDto = WalkDTO(
                walkerId = walkerId,
                petId = petId,
                scheduledAt = scheduledAt,
                durationMinutes = durationMinutes,
                addressId = addressId,
                notes = notes
            )

            walkRepository.createWalk(requestDto)
                .onSuccess {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        successMessage = "¡Solicitud enviada con éxito!",
                        isRequestSuccess = true
                    )
                }
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Error al solicitar paseo"
                    )
                }
        }
    }

    fun clearMessages() {
        _uiState.value = _uiState.value.copy(errorMessage = null, successMessage = null)
    }
}

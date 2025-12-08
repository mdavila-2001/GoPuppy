package com.mdavila_2001.gopuppy.ui.viewmodels.walk

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.mdavila_2001.gopuppy.data.remote.models.address.Address
import com.mdavila_2001.gopuppy.data.remote.models.pet.Pet
import com.mdavila_2001.gopuppy.data.remote.models.walk.WalkDTO
import com.mdavila_2001.gopuppy.data.remote.models.walker.Walker
import com.mdavila_2001.gopuppy.data.repository.AddressRepository
import com.mdavila_2001.gopuppy.data.repository.PetRepository
import com.mdavila_2001.gopuppy.data.repository.WalkRepository
import com.mdavila_2001.gopuppy.data.repository.WalkerRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


data class RequestWalkUiState(
    val isLoading: Boolean = false,
    val myPets: List<Pet> = emptyList(),
    val myAddresses: List<Address> = emptyList(),
    val nearbyWalkers: List<Walker> = emptyList(),

    val selectedAddress: Address? = null,
    val selectedWalker: Walker? = null,
    val selectedPet: Pet? = null,

    val successMessage: String? = null,
    val errorMessage: String? = null,
    val isRequestSuccess: Boolean = false
)

class RequestWalkViewModel(application: Application) : AndroidViewModel(application) {
    private val walkRepository = WalkRepository()
    private val petRepository = PetRepository()
    private val addressRepository = AddressRepository()
    private val walkerRepository = WalkerRepository()

    private val _uiState = MutableStateFlow(RequestWalkUiState())
    val uiState: StateFlow<RequestWalkUiState> = _uiState.asStateFlow()

    fun loadInitialData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            val pets = petRepository.getMyPets().getOrDefault(emptyList())
            val addresses = addressRepository.getMyAddresses().getOrDefault(emptyList())

            // Seleccionar primera dirección por defecto si existe
            val defaultAddress = addresses.firstOrNull()

            _uiState.value = _uiState.value.copy(
                isLoading = false,
                myPets = pets,
                myAddresses = addresses,
                selectedAddress = defaultAddress
            )

            if (defaultAddress != null) {
                searchWalkers(defaultAddress)
            }
        }
    }

    fun onAddressSelected(address: Address) {
        _uiState.value = _uiState.value.copy(selectedAddress = address, selectedWalker = null)
        searchWalkers(address)
    }

    private fun searchWalkers(address: Address) {
        viewModelScope.launch {
            val lat = address.latitude?.toDoubleOrNull() ?: -17.7833 // Santa Cruz por defecto
            val lng = address.longitude?.toDoubleOrNull() ?: -63.1821

            val result = walkerRepository.getNearbyWalkers(lat, lng)
            val walkers = result.getOrDefault(emptyList())

            _uiState.value = _uiState.value.copy(nearbyWalkers = walkers)
        }
    }

    fun onWalkerSelected(walker: Walker) {
        _uiState.value = _uiState.value.copy(selectedWalker = walker)
    }

    fun onPetSelected(pet: Pet) {
        _uiState.value = _uiState.value.copy(selectedPet = pet)
    }

    fun submitRequest(
        scheduledAt: String,
        durationMinutes: Int,
        notes: String
    ) {
        val state = _uiState.value

        if (state.selectedWalker == null || state.selectedPet == null || state.selectedAddress == null) {
            _uiState.value = _uiState.value.copy(errorMessage = "Selecciona Paseador, Mascota y Dirección")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            val dto = WalkDTO(
                walkerId = state.selectedWalker.id,
                petId = state.selectedPet.id,
                addressId = state.selectedAddress.id,
                scheduledAt = scheduledAt,
                durationMinutes = durationMinutes,
                notes = notes
            )

            walkRepository.createWalk(dto)
                .onSuccess {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        successMessage = "¡Solicitud enviada!",
                        isRequestSuccess = true
                    )
                }
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Error al solicitar"
                    )
                }
        }
    }

    fun clearMessages() {
        _uiState.value = _uiState.value.copy(errorMessage = null, successMessage = null)
    }

}
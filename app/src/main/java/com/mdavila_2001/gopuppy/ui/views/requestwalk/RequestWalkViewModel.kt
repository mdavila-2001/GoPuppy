package com.mdavila_2001.gopuppy.ui.views.requestwalk

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mdavila_2001.gopuppy.data.remote.models.pet.Pet
import com.mdavila_2001.gopuppy.data.repository.PetRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime

class RequestWalkViewModel : ViewModel() {
    private val petRepository = PetRepository()
    private val walkRepository = com.mdavila_2001.gopuppy.data.repository.WalkRepository()

    private val _state = MutableStateFlow(WalkRequestState())
    val state: StateFlow<WalkRequestState> = _state

    private val _pets = MutableStateFlow<List<Pet>>(emptyList())
    val pets: StateFlow<List<Pet>> = _pets

    init {
        loadPets()
    }

    private fun loadPets() {
        viewModelScope.launch {
            try {
                Log.d("RequestWalkVM", "Cargando mascotas disponibles")
                petRepository.getMyPets().onSuccess { petList ->
                    _pets.value = petList
                    Log.d("RequestWalkVM", "Mascotas cargadas: ${petList.size}")
                }.onFailure { error ->
                    Log.e("RequestWalkVM", "Error al cargar mascotas: ${error.message}", error)
                    _state.value = _state.value.copy(
                        errorMessage = "No se pudieron cargar las mascotas"
                    )
                }
            } catch (e: Exception) {
                Log.e("RequestWalkVM", "Excepción al cargar mascotas: ${e.message}", e)
            }
        }
    }

    fun selectPet(petId: String) {
        _state.value = _state.value.copy(selectedPetId = petId)
        Log.d("RequestWalkVM", "Mascota seleccionada: $petId")
    }

    fun updateDate(date: LocalDate) {
        _state.value = _state.value.copy(date = date)
        Log.d("RequestWalkVM", "Fecha actualizada: $date")
    }

    fun updateTime(time: LocalTime) {
        _state.value = _state.value.copy(time = time)
        Log.d("RequestWalkVM", "Hora actualizada: $time")
    }

    fun updateDuration(minutes: Int) {
        _state.value = _state.value.copy(durationMinutes = minutes)
        Log.d("RequestWalkVM", "Duración actualizada: $minutes minutos")
    }

    fun updateSpecialInstructions(instructions: String) {
        _state.value = _state.value.copy(specialInstructions = instructions)
    }

    fun updatePreferredWalker(walker: String) {
        _state.value = _state.value.copy(preferredWalker = walker)
    }

    fun submitWalkRequest() {
        viewModelScope.launch {
            try {
                val currentState = _state.value

                if (currentState.selectedPetId == null) {
                    _state.value = currentState.copy(
                        errorMessage = "Por favor selecciona una mascota"
                    )
                    return@launch
                }

                _state.value = currentState.copy(isLoading = true, errorMessage = null)

                Log.d("RequestWalkVM", "Enviando solicitud de paseo...")
                Log.d("RequestWalkVM", "Pet ID: ${currentState.selectedPetId}")
                Log.d("RequestWalkVM", "Fecha: ${currentState.date}")
                Log.d("RequestWalkVM", "Hora: ${currentState.time}")
                Log.d("RequestWalkVM", "Duración: ${currentState.durationMinutes} minutos")

                // Combinar fecha y hora en formato ISO 8601
                // Asegurar formato correcto: YYYY-MM-DDTHH:mm:ss
                val scheduledAt = "${currentState.date}T${currentState.time}:00"
                
                // Crear el DTO para enviar al backend
                // Nota: walkerId y addressId son opcionales por ahora
                val walkDTO = com.mdavila_2001.gopuppy.data.remote.models.walk.WalkDTO(
                    petId = currentState.selectedPetId!!.toInt(),
                    scheduledAt = scheduledAt,
                    durationMinutes = currentState.durationMinutes,
                    notes = currentState.specialInstructions.ifBlank { null }
                )

                Log.d("RequestWalkVM", "DTO creado: petId=${walkDTO.petId}, scheduledAt=${walkDTO.scheduledAt}, durationMinutes=${walkDTO.durationMinutes}")
                Log.d("RequestWalkVM", "Fecha/Hora formato completo: $scheduledAt")

                // Llamada al backend
                walkRepository.createWalk(walkDTO).onSuccess { walk ->
                    _state.value = currentState.copy(
                        isLoading = false,
                        successMessage = "¡Paseo solicitado exitosamente! ID: ${walk.id}"
                    )
                    Log.d("RequestWalkVM", "Solicitud de paseo enviada exitosamente: ID ${walk.id}")
                }.onFailure { error ->
                    Log.e("RequestWalkVM", "Error del backend: ${error.message}", error)
                    throw error
                }

            } catch (e: Exception) {
                Log.e("RequestWalkVM", "Error al enviar solicitud: ${e.message}", e)
                val errorMsg = when {
                    e.message?.contains("walker", ignoreCase = true) == true -> 
                        "No hay paseadores disponibles en este momento"
                    e.message?.contains("address", ignoreCase = true) == true -> 
                        "Debes configurar una dirección primero"
                    e.message?.contains("404") == true -> 
                        "Recurso no encontrado. Verifica tus datos"
                    e.message?.contains("400") == true -> 
                        "Datos inválidos. Verifica fecha, hora y mascota"
                    else -> "Error al solicitar el paseo: ${e.message}"
                }
                _state.value = _state.value.copy(
                    isLoading = false,
                    errorMessage = errorMsg
                )
            }
        }
    }

    fun clearMessages() {
        _state.value = _state.value.copy(
            errorMessage = null,
            successMessage = null
        )
    }

    fun resetForm() {
        _state.value = WalkRequestState()
        Log.d("RequestWalkVM", "Formulario reiniciado")
    }
}

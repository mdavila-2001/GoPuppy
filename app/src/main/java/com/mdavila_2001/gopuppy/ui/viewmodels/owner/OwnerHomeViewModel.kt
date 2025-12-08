package com.mdavila_2001.gopuppy.ui.viewmodels.owner

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.mdavila_2001.gopuppy.data.remote.models.pet.Pet
import com.mdavila_2001.gopuppy.data.remote.models.walk.Walk
import com.mdavila_2001.gopuppy.data.repository.AuthRepository
import com.mdavila_2001.gopuppy.data.repository.PetRepository
import com.mdavila_2001.gopuppy.data.repository.WalkRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class OwnerHomeState(
    val isLoading: Boolean = false,
    val pets: List<Pet> = emptyList(),
    val activeWalks: List<Walk> = emptyList(),
    val finishedWalks: List<Walk> = emptyList(),
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val userName: String = "Usuario",
    val userPhotoUrl: String? = null
)

class OwnerHomeViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = PetRepository()
    private val walkRepository = WalkRepository()
    private val authRepository = AuthRepository(application.applicationContext)

    private val _state = MutableStateFlow(OwnerHomeState())
    val state: StateFlow<OwnerHomeState> = _state

    init {
        loadData()
    }

    fun loadData() {
        loadUserName()
        loadPets()
        loadActiveWalks()
    }
    
    private fun loadUserName() {
        viewModelScope.launch {
            authRepository.getProfile().onSuccess { userInfo ->
                _state.value = _state.value.copy(
                    userName = userInfo.name,
                    userPhotoUrl = userInfo.photoUrl
                )
            }
        }
    }

    private fun loadActiveWalks() {
        viewModelScope.launch {
            val acceptedResult = walkRepository.getAccepted()
            val pendingResult = walkRepository.getPending()
            val historyResult = walkRepository.getHistory()
            val reviewsResult = walkRepository.getMyReviews()

            val accepted = acceptedResult.getOrDefault(emptyList())
            val pending = pendingResult.getOrDefault(emptyList())
            val history = historyResult.getOrDefault(emptyList())
            val myReviews = reviewsResult.getOrDefault(emptyList())

            val reviewedWalkIds = myReviews.map { it.walkId }.toSet()

            val activeStatuses = listOf("accepted", "pending", "scheduled", "in_progress", "started", "en curso")
            val allWalks = (accepted + pending + history).filter { walk ->
                activeStatuses.any { it.equals(walk.status, ignoreCase = true) }
            }.distinctBy { it.id }

            val finishedStatuses = listOf("finished", "completed", "finalizado")
            val finished = history.filter { walk ->
                finishedStatuses.any { it.equals(walk.status, ignoreCase = true) } &&
                walk.id !in reviewedWalkIds
            }.distinctBy { it.id }.take(5)

            Log.d("OwnerHomeVM", "Paseos activos: ${allWalks.size}, Finalizados sin review: ${finished.size}, Reviews existentes: ${myReviews.size}")

            _state.value = _state.value.copy(
                activeWalks = allWalks,
                finishedWalks = finished
            )
        }
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
                    loadData()
                }
                .onFailure { e ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        errorMessage = "Error al enviar calificación: ${e.message}"
                    )
                }
        }
    }

    fun clearMessages() {
        _state.value = _state.value.copy(errorMessage = null, successMessage = null)
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

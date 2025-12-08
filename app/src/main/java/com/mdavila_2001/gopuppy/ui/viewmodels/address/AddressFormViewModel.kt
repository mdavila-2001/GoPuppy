package com.mdavila_2001.gopuppy.ui.viewmodels.address

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.mdavila_2001.gopuppy.data.repository.AddressRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AddressFormUiState(
    val isLoading: Boolean = false,
    val successMessage: String? = null,
    val errorMessage: String? = null
)

class AddressFormViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = AddressRepository()
    private val _uiState = MutableStateFlow(AddressFormUiState())
    val uiState: StateFlow<AddressFormUiState> = _uiState.asStateFlow()

    fun saveAddress(label: String, addressText: String, lat: Double, lng: Double, onSuccess: () -> Unit) {
        if (label.isBlank() || addressText.isBlank()) {
            _uiState.value = _uiState.value.copy(errorMessage = "Por favor, llena todos los campos")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            repository.addAddress(label, addressText, lat, lng)
                .onSuccess {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        successMessage = "Dirección guardada correctamente"
                    )
                    onSuccess()
                }
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Error al guardar la dirección"
                    )
                }
        }
    }

    fun clearMessages() {
        _uiState.value = _uiState.value.copy(errorMessage = null, successMessage = null)
    }
}
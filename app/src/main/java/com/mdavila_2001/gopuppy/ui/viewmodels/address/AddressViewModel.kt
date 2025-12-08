package com.mdavila_2001.gopuppy.ui.viewmodels.address

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.mdavila_2001.gopuppy.data.remote.models.address.Address
import com.mdavila_2001.gopuppy.data.repository.AddressRepository
import com.mdavila_2001.gopuppy.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AddressListUiState(
    val isLoading: Boolean = false,
    val addresses: List<Address> = emptyList(),
    val errorMessage: String? = null,
    val userName: String = "Usuario",
    val userPhotoUrl: String? = null
)

class AddressListViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = AddressRepository()
    private val authRepository = AuthRepository(application.applicationContext)

    private val _uiState = MutableStateFlow(AddressListUiState())
    val uiState: StateFlow<AddressListUiState> = _uiState.asStateFlow()

    init {
        loadUserProfile()
        loadAddresses()
    }

    private fun loadUserProfile() {
        viewModelScope.launch {
            authRepository.getProfile().onSuccess { userInfo ->
                _uiState.value = _uiState.value.copy(
                    userName = userInfo.name,
                    userPhotoUrl = userInfo.photoUrl
                )
            }
        }
    }

    fun loadAddresses() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            repository.getMyAddresses()
                .onSuccess { list ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        addresses = list,
                        errorMessage = null
                    )
                }
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Error al cargar lugares"
                    )
                }
        }
    }

    fun deleteAddress(id: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            repository.deleteAddress(id)
                .onSuccess {
                    loadAddresses()
                }
                .onFailure {
                    _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = "No se pudo eliminar")
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
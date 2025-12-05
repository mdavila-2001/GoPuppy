package com.mdavila_2001.gopuppy.data.repository

import android.util.Log
import com.mdavila_2001.gopuppy.data.remote.models.address.Address
import com.mdavila_2001.gopuppy.data.remote.models.address.AddressDTO
import com.mdavila_2001.gopuppy.data.remote.network.RetrofitInstance

class AddressRepository {
    private val api = RetrofitInstance.apiService

    suspend fun getMyAddresses(): Result<List<Address>> {
        return try {
            val response = api.getAddresses()
            if (response.isSuccessful) {
                Result.success(response.body() ?: emptyList())
            } else {
                Result.failure(Exception("Error al cargar direcciones: ${response.code()}"))
            }
        } catch (e: Exception) {
            Log.e("AddressRepo", "Error getAddresses: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun addAddress(label: String, address: String, lat: Double, lng: Double): Result<Address> {
        return try {
            val dto = AddressDTO(
                label = label,
                address = address,
                lat = lat.toString(),
                lng = lng.toString()
            )
            val response = api.addAddress(dto)

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error al guardar dirección"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteAddress(id: Int): Result<Boolean> {
        return try {
            val response = api.deleteAddress(id)
            if (response.isSuccessful) Result.success(true)
            else Result.failure(Exception("No se pudo eliminar la dirección"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
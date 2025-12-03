package com.mdavila_2001.gopuppy.data.repository

import android.util.Log
import com.mdavila_2001.gopuppy.data.remote.models.walker.Availability
import com.mdavila_2001.gopuppy.data.remote.models.walker.Location
import com.mdavila_2001.gopuppy.data.remote.models.walker.Walker
import com.mdavila_2001.gopuppy.data.remote.network.RetrofitInstance
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class WalkerRepository {
    private val api = RetrofitInstance.apiService

    suspend fun getNearbyWalkers(lat: Double, lng: Double): Result<List<Walker>> {
        return try {
            val location = Location(lat.toString(), lng.toString())
            val response = api.getNearbyWalkers(location)

            if (response.isSuccessful) {
                Result.success(response.body() ?: emptyList())
            } else {
                Result.failure(Exception("Error buscando paseadores: ${response.code()}"))
            }
        } catch (e: Exception) {
            Log.e("WalkerRepo", "Error getNearby: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun getWalkerDetail(id: Int): Result<Walker> {
        return try {
            val response = api.getWalkerDetail(id)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error al obtener detalle del paseador"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun setAvailability(isAvailable: Boolean): Result<Boolean> {
        return try {
            val dto = Availability(isAvailable)
            val response = api.setAvailability(dto)

            if (response.isSuccessful) {
                Result.success(true)
            } else {
                Result.failure(Exception("No se pudo cambiar el estado: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun sendLocation(lat: Double, lng: Double): Result<Boolean> {
        return try {
            val dto = Location(lat.toString(), lng.toString())
            val response = api.sendLocation(dto)

            if (response.isSuccessful) {
                Log.d("WalkerRepo", "Ubicación enviada: $lat, $lng")
                Result.success(true)
            } else {
                Result.failure(Exception("Error enviando ubicación"))
            }
        } catch (e: Exception) {
            Log.e("WalkerRepo", "Fallo silencioso envío ubicación: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun uploadProfilePhoto(file: File): Result<Boolean> {
        return try {
            val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
            val body = MultipartBody.Part.createFormData("photo", file.name, requestFile)

            val response = api.uploadWalkerPhoto(body)
            if (response.isSuccessful) Result.success(true)
            else Result.failure(Exception("Error subiendo foto de perfil"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
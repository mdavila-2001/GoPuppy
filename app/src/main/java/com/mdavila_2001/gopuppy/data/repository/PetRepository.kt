package com.mdavila_2001.gopuppy.data.repository

import android.util.Log
import com.mdavila_2001.gopuppy.data.remote.models.pet.Pet
import com.mdavila_2001.gopuppy.data.remote.models.pet.PetDTO
import com.mdavila_2001.gopuppy.data.remote.network.RetrofitInstance
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class PetRepository {
    private val api = RetrofitInstance.apiService

    suspend fun getMyPets(): Result<List<Pet>> {
        return try {
            val response = api.getMyPets()
            if (response.isSuccessful) {
                Result.success(response.body() ?: emptyList())
            } else {
                Result.failure(Exception("Error al cargar mascotas: ${response.code()}"))
            }
        } catch (e: Exception) {
            Log.e("PetRepo", "Error getMyPets: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun addPet(
        pet: PetDTO
    ): Result<Pet> {
        return try {
            Log.d("PetRepo", "=== INICIANDO CREACIÓN DE MASCOTA ===")
            Log.d("PetRepo", "Nombre: ${pet.name}, Tipo: ${pet.species}, Notas: ${pet.notes}")
            Log.d("PetRepo", "Token presente: ${RetrofitInstance.authToken != null}")
            Log.d("PetRepo", "Token (primeros 30 chars): ${RetrofitInstance.authToken?.take(30) ?: "NULL"}")
            
            val response = api.addPet(pet)
            Log.d("PetRepo", "Response code: ${response.code()}, isSuccessful: ${response.isSuccessful}")
            
            if (response.isSuccessful && response.body() != null) {
                Log.d("PetRepo", "Mascota creada exitosamente: ${response.body()?.name}")
                Result.success(response.body()!!)
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMsg = when (response.code()) {
                    500 -> "Error del servidor (500). Por favor intenta de nuevo más tarde."
                    401 -> "No autorizado. Por favor inicia sesión nuevamente."
                    400 -> "Datos inválidos: ${errorBody ?: response.message()}"
                    else -> "Error ${response.code()}: ${response.message()}"
                }
                Log.e("PetRepo", "Error addPet: $errorMsg")
                Log.e("PetRepo", "Error body: $errorBody")
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Log.e("PetRepo", "Error addPet exception: ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun updatePet(
        id: Int,
        pet: PetDTO
    ): Result<Pet> {
        return try {
            val response = api.updatePet(id, pet)

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMsg = "Error ${response.code()}: ${response.message()}${if (errorBody != null) " - $errorBody" else ""}"
                Log.e("PetRepo", "Error updatePet: $errorMsg")
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Log.e("PetRepo", "Error updatePet exception: ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun deletePet(id: Int): Result<Boolean> {
        return try {
            val response = api.deletePet(id)
            if (response.isSuccessful) {
                Result.success(true)
            } else {
                Result.failure(Exception("No se pudo eliminar la mascota"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun uploadPhoto(
        id: Int,
        file: File
    ): Result<Boolean> {
        return try {
            val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
            val body = MultipartBody.Part.createFormData("photo", file.name, requestFile)
            val response = api.uploadPetPhoto(id, body)

            if (response.isSuccessful) {
                Result.success(true)
            } else {
                Result.failure(Exception("Error al subir foto: ${response.code()}"))
            }
        } catch (e: Exception) {
            Log.e("PetRepo", "Error uploadPhoto: ${e.message}")
            Result.failure(e)
        }
    }
}
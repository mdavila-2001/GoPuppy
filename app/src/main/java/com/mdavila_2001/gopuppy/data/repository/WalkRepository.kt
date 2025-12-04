package com.mdavila_2001.gopuppy.data.repository

import android.util.Log
import com.mdavila_2001.gopuppy.data.remote.models.walk.ReviewDTO
import com.mdavila_2001.gopuppy.data.remote.models.walk.Walk
import com.mdavila_2001.gopuppy.data.remote.models.walk.WalkDTO
import com.mdavila_2001.gopuppy.data.remote.network.RetrofitInstance
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class WalkRepository {
    private val api = RetrofitInstance.apiService

    suspend fun getHistory(): Result<List<Walk>> = safeApiCall { api.getWalksHistory() }
    suspend fun getAccepted(): Result<List<Walk>> = safeApiCall { api.getAcceptedWalks() }

    suspend fun getDetail(id: Int): Result<Walk> {
        return try {
            val response = api.getWalkDetail(id)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error obteniendo detalle: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createWalk(dto: WalkDTO): Result<Walk> {
        return try {
            Log.d("WalkRepo", "=== CREANDO PASEO ===")
            Log.d("WalkRepo", "petId: ${dto.petId}")
            Log.d("WalkRepo", "scheduledAt: ${dto.scheduledAt}")
            Log.d("WalkRepo", "durationMinutes: ${dto.durationMinutes}")
            Log.d("WalkRepo", "walkerId: ${dto.walkerId}")
            Log.d("WalkRepo", "addressId: ${dto.addressId}")
            Log.d("WalkRepo", "notes: ${dto.notes}")
            
            val response = api.createWalk(dto)
            
            Log.d("WalkRepo", "Código de respuesta: ${response.code()}")
            Log.d("WalkRepo", "Es exitoso: ${response.isSuccessful}")
            Log.d("WalkRepo", "Headers: ${response.headers()}")
            
            if (response.isSuccessful && response.body() != null) {
                Log.d("WalkRepo", "✅ Paseo creado exitosamente: ${response.body()!!.id}")
                Result.success(response.body()!!)
            } else {
                val errorBody = response.errorBody()?.string()
                val errorBodyPreview = errorBody?.take(500) ?: "NULL"
                Log.e("WalkRepo", "❌ Error Body (primeros 500 chars): $errorBodyPreview")
                Log.e("WalkRepo", "❌ Response Code: ${response.code()}")
                Log.e("WalkRepo", "❌ Response Message: ${response.message()}")
                Log.e("WalkRepo", "❌ Content-Type: ${response.headers()["Content-Type"]}")
                
                // Extraer mensaje útil si el error body es texto plano
                val cleanError = when {
                    errorBody?.contains("<!DOCTYPE", ignoreCase = true) == true -> 
                        "El servidor devolvió HTML. Revisa la URL del API"
                    errorBody?.contains("walker", ignoreCase = true) == true -> 
                        "El backend requiere un paseador asignado (walker_id)"
                    errorBody?.contains("address", ignoreCase = true) == true -> 
                        "El backend requiere una dirección (address_id)"
                    else -> errorBody
                }
                
                val errorMsg = when (response.code()) {
                    400 -> "Datos inválidos: $cleanError"
                    401 -> "No autorizado. Inicia sesión nuevamente"
                    404 -> "Recurso no encontrado: $cleanError"
                    422 -> "Error de validación: $cleanError"
                    500 -> "Error del servidor: $cleanError"
                    else -> "Error ${response.code()}: $cleanError"
                }
                Result.failure(Exception(errorMsg))
            }
        } catch (e: com.google.gson.JsonSyntaxException) {
            Log.e("WalkRepo", "💥 Error de parseo JSON", e)
            Log.e("WalkRepo", "El backend no está devolviendo JSON válido")
            Result.failure(Exception("El servidor devolvió una respuesta inválida. Contacta al administrador del backend."))
        } catch (e: Exception) {
            Log.e("WalkRepo", "💥 Excepción al crear paseo", e)
            Log.e("WalkRepo", "Tipo de error: ${e.javaClass.simpleName}")
            Log.e("WalkRepo", "Mensaje: ${e.message}")
            Result.failure(Exception("Error de conexión: ${e.message}"))
        }
    }

    suspend fun acceptWalk(id: Int): Result<Boolean> = actionCall { api.acceptWalk(id) }
    suspend fun rejectWalk(id: Int): Result<Boolean> = actionCall { api.rejectWalk(id) }
    suspend fun startWalk(id: Int): Result<Boolean> = actionCall { api.startWalk(id) }
    suspend fun endWalk(id: Int): Result<Boolean> = actionCall { api.endWalk(id) }

    suspend fun uploadWalkPhoto(id: Int, file: File): Result<Boolean> {
        return try {
            val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
            val body = MultipartBody.Part.createFormData("photo", file.name, requestFile)

            val response = api.uploadWalkPhoto(id, body)
            if (response.isSuccessful) Result.success(true)
            else Result.failure(Exception("Error subiendo foto"))
        } catch (e: Exception) {
            Log.e("WalkRepo", "Error uploadPhoto: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun getWalkPhotos(id: Int): Result<List<String>> {
        return try {
            val response = api.getWalkPhotos(id)
            if (response.isSuccessful) {
                Result.success(response.body() ?: emptyList())
            } else {
                Result.failure(Exception("Error cargando fotos"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun sendReview(id: Int, rating: Int, comment: String): Result<Boolean> {
        return try {
            val dto = ReviewDTO(rating, comment)
            val response = api.sendReview(id, dto)

            if (response.isSuccessful) Result.success(true)
            else Result.failure(Exception("Error enviando review"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun safeApiCall(
        call: suspend () -> retrofit2.Response<List<Walk>>
    ): Result<List<Walk>> {
        return try {
            val response = call()
            if (response.isSuccessful) {
                Result.success(response.body() ?: emptyList())
            } else {
                Result.failure(Exception("Error de servidor: ${response.code()}"))
            }
        } catch (e: Exception) {
            Log.e("WalkRepo", "API Error: ${e.message}")
            Result.failure(e)
        }
    }

    private suspend fun actionCall(
        call: suspend () -> retrofit2.Response<Void>
    ): Result<Boolean> {
        return try {
            val response = call()
            if (response.isSuccessful) Result.success(true)
            else Result.failure(Exception("Acción fallida: ${response.code()}"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
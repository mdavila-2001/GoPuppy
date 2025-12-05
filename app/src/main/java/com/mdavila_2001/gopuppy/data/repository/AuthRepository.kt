package com.mdavila_2001.gopuppy.data.repository

import android.content.Context
import android.util.Log
import com.mdavila_2001.gopuppy.data.local.TokenManager
import com.mdavila_2001.gopuppy.data.remote.models.auth.LoginRequest
import com.mdavila_2001.gopuppy.data.remote.models.auth.UserInfo
import com.mdavila_2001.gopuppy.data.remote.models.auth.signup.AuthResponse
import com.mdavila_2001.gopuppy.data.remote.models.auth.signup.OwnerSignupDTO
import com.mdavila_2001.gopuppy.data.remote.models.auth.signup.WalkerSignupDTO
import com.mdavila_2001.gopuppy.data.remote.network.RetrofitInstance
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class AuthRepository(context: Context) {
    private val api = RetrofitInstance.apiService
    private val tokenManager = TokenManager(context)

    suspend fun login(
        email: String,
        password: String,
        isWalker: Boolean
    ): Result<AuthResponse> {
        return try {
            val request = LoginRequest(email, password)
            Log.d("AuthRepo", "Intentando login como ${if (isWalker) "Walker" else "Owner"} con email: $email")

            val response = if (isWalker) {
                api.loginWalker(request)
            } else {
                api.loginOwner(request)
            }

            Log.d("AuthRepo", "Respuesta del servidor - Código: ${response.code()}, Success: ${response.isSuccessful}")
            
            if (response.isSuccessful && response.body() != null) {
                val authBody = response.body()!!
                val tokenReal = authBody.accessToken ?: authBody.token

                if (tokenReal != null) {
                    Log.d("AuthRepo", "Token recibido del servidor: ${tokenReal.take(30)}...")
                    RetrofitInstance.authToken = tokenReal
                    Log.d("AuthRepo", "Verificando token guardado: ${RetrofitInstance.authToken?.take(30) ?: "NULL"}")
                    tokenManager.saveToken(tokenReal)
                    tokenManager.saveRole(isWalker)
                    Result.success(authBody)
                } else {
                    Log.e("AuthRepo", "Token vacío en respuesta del servidor")
                    Result.failure(Exception("Token vacío"))
                }
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e("AuthRepo", "Error en login: ${response.code()} - $errorBody")
                
                val errorMsg = when (response.code()) {
                    404 -> "Usuario no encontrado. Verifica tu email o regístrate"
                    401 -> "Contraseña incorrecta"
                    422 -> "Datos inválidos. Verifica el formato del email"
                    else -> "Error al iniciar sesión (${response.code()})"
                }
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Log.e("AuthRepo", "Excepción en login: ${e.message}", e)
            Result.failure(Exception("Error de conexión: ${e.message}"))
        }
    }

    suspend fun OwnerSignup(
        ownerData: OwnerSignupDTO
    ): Result<AuthResponse> {
        return try {
            val response = api.registerOwner(ownerData)
            handleRegistrationResponse(response, isOwner = true)
        } catch (e: Exception) {
            if (e.message?.contains("JSON", ignoreCase = true) == true) {
                Result.success(AuthResponse(accessToken = "registered", token = "registered", tokenType = "Bearer"))
            } else {
                Result.failure(e)
            }
        }
    }

    suspend fun WalkerSignup(
        walkerData: WalkerSignupDTO
    ): Result<AuthResponse> {
        return try {
            val response = api.registerWalker(walkerData)
            handleRegistrationResponse(response, isOwner = false)
        } catch (e: Exception) {
            if (e.message?.contains("JSON", ignoreCase = true) == true) {
                Result.success(AuthResponse(accessToken = "registered", token = "registered", tokenType = "Bearer"))
            } else {
                Result.failure(e)
            }
        }
    }

    suspend fun logout() {
        tokenManager.clearToken()
        RetrofitInstance.authToken = null
    }

    private suspend fun handleRegistrationResponse(response: retrofit2.Response<AuthResponse>, isOwner: Boolean): Result<AuthResponse> {
        return try {
            if (response.isSuccessful) {
                if (response.body() != null) {
                    val authBody = response.body()!!
                    val token = authBody.accessToken ?: authBody.token

                    if (!token.isNullOrEmpty()) {
                        RetrofitInstance.authToken = token
                        tokenManager.saveToken(token)
                        return Result.success(authBody)
                    }
                }
                Result.success(AuthResponse(accessToken = "registered", token = "registered", tokenType = "Bearer"))
            } else {
                Result.failure(Exception("Error en registro: ${response.code()}"))
            }
        } catch (e: Exception) {
            if (response.isSuccessful) {
                Result.success(AuthResponse(accessToken = "registered", token = "registered", tokenType = "Bearer"))
            } else {
                Result.failure(e)
            }
        }
    }

    suspend fun getProfile(): Result<UserInfo> {
        return try {
            val response = api.getProfile()
            
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error al obtener perfil: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun uploadOwnerPhoto(file: File): Result<Boolean> {
        return try {
            val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
            val body = MultipartBody.Part.createFormData("photo", file.name, requestFile)

            val response = api.uploadOwnerPhoto(body)
            if (response.isSuccessful) Result.success(true)
            else Result.failure(Exception("Error subiendo foto de perfil"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun uploadWalkerPhoto(file: File): Result<Boolean> {
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
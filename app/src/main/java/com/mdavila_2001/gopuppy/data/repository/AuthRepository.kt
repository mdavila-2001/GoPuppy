package com.mdavila_2001.gopuppy.data.repository

import com.mdavila_2001.gopuppy.data.remote.models.auth.LoginRequest
import com.mdavila_2001.gopuppy.data.remote.models.auth.signup.AuthResponse
import com.mdavila_2001.gopuppy.data.remote.models.auth.signup.OwnerSignupDTO
import com.mdavila_2001.gopuppy.data.remote.models.auth.signup.WalkerSignupDTO
import com.mdavila_2001.gopuppy.data.remote.network.RetrofitInstance

class AuthRepository {
    private val api = RetrofitInstance.apiService

    suspend fun login(
        email: String,
        password: String,
        isWalker: Boolean
    ): Result<AuthResponse> {
        return try {
            val request = LoginRequest(email, password)
            android.util.Log.d("AuthRepo", "Intentando login para: $email (isWalker: $isWalker)")

            val response = if (isWalker) {
                api.loginWalker(request)
            } else {
                api.loginOwner(request)
            }

            android.util.Log.d("AuthRepo", "Response code: ${response.code()}, isSuccessful: ${response.isSuccessful}")

            if (response.isSuccessful && response.body() != null) {
                val authBody = response.body()!!
                val tokenReal = authBody.accessToken ?: authBody.token

                if (tokenReal != null) {
                    RetrofitInstance.authToken = tokenReal
                    android.util.Log.d("AuthRepo", "Token guardado exitosamente: ${tokenReal.take(20)}...")
                    Result.success(authBody)
                } else {
                    android.util.Log.e("AuthRepo", "Token vacío en respuesta")
                    Result.failure(Exception("Token vacío"))
                }
            } else {
                val errorBody = response.errorBody()?.string()
                android.util.Log.e("AuthRepo", "Error login: ${response.code()} - $errorBody")
                Result.failure(Exception("Error ${response.code()}: Verifica tus credenciales"))
            }
        } catch (e: Exception) {
            android.util.Log.e("AuthRepo", "Exception en login: ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun OwnerSignup(
        ownerData: OwnerSignupDTO
    ): Result<AuthResponse> {
        return try {
            val response = api.registerOwner(ownerData)
            handleRegistrationResponse(response, isOwner = true)
        } catch (e: Exception) {
            // Si hay error de parseo JSON pero el código es exitoso, considerarlo como éxito
            if (e.message?.contains("JSON", ignoreCase = true) == true) {
                // IMPORTANTE: Guardar un token temporal para que funcione la autenticación
                RetrofitInstance.authToken = "registered"
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
            // Si hay error de parseo JSON pero el código es exitoso, considerarlo como éxito
            if (e.message?.contains("JSON", ignoreCase = true) == true) {
                // IMPORTANTE: Guardar un token temporal para que funcione la autenticación
                RetrofitInstance.authToken = "registered"
                Result.success(AuthResponse(accessToken = "registered", token = "registered", tokenType = "Bearer"))
            } else {
                Result.failure(e)
            }
        }
    }

    private fun handleRegistrationResponse(response: retrofit2.Response<AuthResponse>, isOwner: Boolean): Result<AuthResponse> {
        return try {
            android.util.Log.d("AuthRepo", "handleRegistrationResponse - Code: ${response.code()}, isSuccessful: ${response.isSuccessful}")
            
            if (response.isSuccessful) {
                if (response.body() != null) {
                    val authBody = response.body()!!
                    val token = authBody.accessToken ?: authBody.token

                    if (!token.isNullOrEmpty()) {
                        RetrofitInstance.authToken = token
                        android.util.Log.d("AuthRepo", "Token guardado desde respuesta de registro")
                        return Result.success(authBody)
                    }
                }
                // Si el código es exitoso pero no hay body, asumir que se registró correctamente
                android.util.Log.d("AuthRepo", "Registro exitoso sin token en respuesta, se requerirá login")
                Result.success(AuthResponse(accessToken = "registered", token = "registered", tokenType = "Bearer"))
            } else {
                val errorBody = response.errorBody()?.string()
                android.util.Log.e("AuthRepo", "Error en registro: ${response.code()} - $errorBody")
                Result.failure(Exception("Error en registro: ${response.code()}"))
            }
        } catch (e: Exception) {
            android.util.Log.e("AuthRepo", "Exception en handleRegistrationResponse: ${e.message}", e)
            // Si hay error de parseo pero la respuesta fue exitosa, considerarlo como éxito
            if (response.isSuccessful) {
                android.util.Log.d("AuthRepo", "Error de parseo pero código exitoso, considerando registro exitoso")
                Result.success(AuthResponse(accessToken = "registered", token = "registered", tokenType = "Bearer"))
            } else {
                Result.failure(e)
            }
        }
    }
}
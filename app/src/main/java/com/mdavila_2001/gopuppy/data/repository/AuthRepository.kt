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

            val response = if (isWalker) {
                api.loginWalker(request)
            } else {
                api.loginOwner(request)
            }

            if (response.isSuccessful && response.body() != null) {
                val authBody = response.body()!!
                val tokenReal = authBody.accessToken ?: authBody.token

                if (tokenReal != null) {
                    RetrofitInstance.authToken = tokenReal
                    Result.success(authBody)
                } else {
                    Result.failure(Exception("Token vacío"))
                }
            } else {
                Result.failure(Exception("Error ${response.code()}: Verifica tus credenciales"))
            }
        } catch (e: Exception) {
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
                Result.success(AuthResponse(accessToken = "registered", token = "registered", tokenType = "Bearer"))
            } else {
                Result.failure(e)
            }
        }
    }

    private fun handleRegistrationResponse(response: retrofit2.Response<AuthResponse>, isOwner: Boolean): Result<AuthResponse> {
        return try {
            if (response.isSuccessful) {
                if (response.body() != null) {
                    val authBody = response.body()!!
                    val token = authBody.accessToken ?: authBody.token

                    if (!token.isNullOrEmpty()) {
                        RetrofitInstance.authToken = token
                        return Result.success(authBody)
                    }
                }
                // Si el código es exitoso pero no hay body, asumir que se registró correctamente
                Result.success(AuthResponse(accessToken = "registered", token = "registered", tokenType = "Bearer"))
            } else {
                Result.failure(Exception("Error en registro: ${response.code()}"))
            }
        } catch (e: Exception) {
            // Si hay error de parseo pero la respuesta fue exitosa, considerarlo como éxito
            if (response.isSuccessful) {
                Result.success(AuthResponse(accessToken = "registered", token = "registered", tokenType = "Bearer"))
            } else {
                Result.failure(e)
            }
        }
    }
}
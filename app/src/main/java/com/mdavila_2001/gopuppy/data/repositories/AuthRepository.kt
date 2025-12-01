package com.mdavila_2001.gopuppy.data.repositories

import com.mdavila_2001.gopuppy.data.remote.models.auth.LoginRequest
import com.mdavila_2001.gopuppy.data.remote.models.auth.signup.AuthResponse
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

    suspend fun
}
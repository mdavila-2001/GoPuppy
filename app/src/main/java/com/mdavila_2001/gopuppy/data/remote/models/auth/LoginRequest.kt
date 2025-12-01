package com.mdavila_2001.gopuppy.data.remote.models.auth

import com.google.gson.annotations.SerializedName

data class LoginRequest (
    @SerializedName("email")
    val email: String,
    @SerializedName("password")
    val password: String
)
package com.mdavila_2001.gopuppy.data.remote.models.auth.signup

import com.google.gson.annotations.SerializedName

class AuthResponse (
    @SerializedName("access_token")
    val accessToken: String?,
    @SerializedName("token")
    val token: String?,
    @SerializedName("token_type")
    val tokenType: String?,
)
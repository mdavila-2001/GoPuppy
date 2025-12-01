package com.mdavila_2001.gopuppy.data.remote.models.auth.signup

import com.google.gson.annotations.SerializedName

data class WalkerSignup (
    @SerializedName("name")
    val name: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("password")
    val password: String,
    @SerializedName("price_hour")
    val priceHour: String,
)
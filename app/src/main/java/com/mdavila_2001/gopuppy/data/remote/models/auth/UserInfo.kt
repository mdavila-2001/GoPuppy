package com.mdavila_2001.gopuppy.data.remote.models.auth

import com.google.gson.annotations.SerializedName

data class UserInfo (
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("role")
    val role: String,
    @SerializedName("photoUrl")
    val photoUrl: String
)
package com.mdavila_2001.gopuppy.data.remote.models.owner

import com.google.gson.annotations.SerializedName

data class Owner (
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("photoUrl")
    val photoUrl: String
)
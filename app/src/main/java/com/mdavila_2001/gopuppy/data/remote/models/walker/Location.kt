package com.mdavila_2001.gopuppy.data.remote.models.walker

import com.google.gson.annotations.SerializedName

data class Location (
    @SerializedName("latitude")
    val latitude: String,
    @SerializedName("longitude")
    val longitude: String
)
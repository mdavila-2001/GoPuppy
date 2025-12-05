package com.mdavila_2001.gopuppy.data.remote.models.walker.status

import com.google.gson.annotations.SerializedName

data class WalkerStatus(
    @SerializedName("id")
    val id: Int,
    @SerializedName("is_available")
    val isAvailable: Int,
    @SerializedName("current_latitude")
    val currentLatitude: String?,
    @SerializedName("current_longitude")
    val currentLongitude: String?
)
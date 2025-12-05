package com.mdavila_2001.gopuppy.data.remote.models.walker

import com.google.gson.annotations.SerializedName
import com.mdavila_2001.gopuppy.data.remote.models.walker.extras.WalkerExtras
import com.mdavila_2001.gopuppy.data.remote.models.walker.status.WalkerStatus

data class Walker (
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("role")
    val role: String? = null,
    @SerializedName("photoUrl")
    val photoUrl: String?,

    @SerializedName("extras")
    val extras: WalkerExtras? = null,
    @SerializedName("walker_status")
    val status: WalkerStatus? = null
) {
    val priceHour: String
        get() = extras?.priceHour ?: "A convenir"

    val rating: Double
        get() = extras?.ratingSummary ?: 5.0

    val latitude: Double?
        get() = status?.currentLatitude?.toDoubleOrNull()

    val longitude: Double?
        get() = status?.currentLongitude?.toDoubleOrNull()
}
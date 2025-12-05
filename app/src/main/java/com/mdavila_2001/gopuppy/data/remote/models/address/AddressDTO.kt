package com.mdavila_2001.gopuppy.data.remote.models.address

import com.google.gson.annotations.SerializedName

data class AddressDTO (
    @SerializedName("label")
    val label: String,
    @SerializedName("address")
    val address: String,
    @SerializedName("lat")
    val lat: String,
    @SerializedName("lng")
    val lng: String
)
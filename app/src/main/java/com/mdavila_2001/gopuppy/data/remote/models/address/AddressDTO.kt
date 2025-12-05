package com.mdavila_2001.gopuppy.data.remote.models.address

import com.google.gson.annotations.SerializedName

data class AddressDTO (
    @SerializedName("label")
    val label: String,
    @SerializedName("address")
    val address: String,
    @SerializedName("latitude")
    val latitude: String,
    @SerializedName("longitude")
    val longitude: String
)
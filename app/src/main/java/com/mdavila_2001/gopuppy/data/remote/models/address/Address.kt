package com.mdavila_2001.gopuppy.data.remote.models.address

import com.google.gson.annotations.SerializedName

data class Address (
    @SerializedName("id")
    val id: Int,
    @SerializedName("label")
    val label: String,
    @SerializedName("address")
    val address: String,
    @SerializedName(value = "latitude", alternate = ["lat"])
    val latitude: String?,
    @SerializedName(value = "longitude", alternate = ["lng"])
    val longitude: String?
)
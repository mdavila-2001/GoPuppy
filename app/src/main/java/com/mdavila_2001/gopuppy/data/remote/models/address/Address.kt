package com.mdavila_2001.gopuppy.data.remote.models.address

import com.google.gson.annotations.SerializedName

data class Address (
    @SerializedName("id")
    val id: Int,
    @SerializedName("label")
    val label: String,
    @SerializedName("address")
    val address: String,
    @SerializedName("lat")
    val lat: String,
    @SerializedName("lng")
    val lng: String
)
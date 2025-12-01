package com.mdavila_2001.gopuppy.data.remote.models.walker

import com.google.gson.annotations.SerializedName

data class Walker (
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("price_hour")
    val priceHour: String,
    @SerializedName("rating")
    val rating: Double,
    @SerializedName("photoUrl")
    val photoUrl: String
)
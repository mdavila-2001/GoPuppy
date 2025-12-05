package com.mdavila_2001.gopuppy.data.remote.models.walker.extras

import com.google.gson.annotations.SerializedName

data class WalkerExtras(
    @SerializedName("price_hour")
    val priceHour: String?,
    @SerializedName("rating_summary")
    val ratingSummary: Double?
)
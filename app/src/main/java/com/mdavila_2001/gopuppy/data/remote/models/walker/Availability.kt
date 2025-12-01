package com.mdavila_2001.gopuppy.data.remote.models.walker

import com.google.gson.annotations.SerializedName

data class Availability (
    @SerializedName("is_available")
    val isAvailable: Boolean,
)
package com.mdavila_2001.gopuppy.data.remote.models.imgbb

import com.google.gson.annotations.SerializedName

data class ImgBbResponse (
    @SerializedName("data")
    val data: ImgBbData,

    @SerializedName("success")
    val success: Boolean
)
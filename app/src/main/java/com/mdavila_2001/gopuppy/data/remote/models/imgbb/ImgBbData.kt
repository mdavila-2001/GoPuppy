package com.mdavila_2001.gopuppy.data.remote.models.imgbb

import com.google.gson.annotations.SerializedName

data class ImgBbData (
    @SerializedName("id")
    val id: String,

    @SerializedName("display_url")
    val displayUrl: String,

    @SerializedName("url")
    val url: String,

    @SerializedName("delete_url")
    val deleteUrl: String
)
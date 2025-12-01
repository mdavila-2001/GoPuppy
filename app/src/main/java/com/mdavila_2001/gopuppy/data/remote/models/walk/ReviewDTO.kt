package com.mdavila_2001.gopuppy.data.remote.models.walk

import com.google.gson.annotations.SerializedName

data class ReviewDTO (
    @SerializedName("rating")
    val rating: Int,
    @SerializedName("comment")
    val comment: String
)
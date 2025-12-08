package com.mdavila_2001.gopuppy.data.remote.models.review

import com.google.gson.annotations.SerializedName

data class ReviewDTO (
    @SerializedName("rating")
    val rating: Int,
    @SerializedName("comment")
    val comment: String
)
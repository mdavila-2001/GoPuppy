package com.mdavila_2001.gopuppy.data.remote.models.review

import com.google.gson.annotations.SerializedName

data class Review(
    @SerializedName("id")
    val id: Int,
    @SerializedName("walk_id")
    val walkId: Int,
    @SerializedName("walker_id")
    val walkerId: Int,
    @SerializedName("rating")
    val rating: Int,
    @SerializedName("comment")
    val comment: String?,
    @SerializedName("created_at")
    val createdAt: String?
)
package com.mdavila_2001.gopuppy.data.remote.models.pet

import com.google.gson.annotations.SerializedName

data class Pet (
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("species")
    val species: String,
    @SerializedName("notes")
    val notes: String?,
    @SerializedName("photoUrl")
    val photoUrl: String?
)
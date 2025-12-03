package com.mdavila_2001.gopuppy.data.remote.models.pet

import com.google.gson.annotations.SerializedName

data class PetDTO (
    @SerializedName("name")
    val name: String,
    @SerializedName("type")
    val species: String,
    @SerializedName("notes")
    val notes: String
)
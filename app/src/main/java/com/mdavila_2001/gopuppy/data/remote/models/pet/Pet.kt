package com.mdavila_2001.gopuppy.data.remote.models.pet

import com.google.gson.annotations.SerializedName

data class Pet (
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("species") // El backend devuelve "species" en lugar de "type"
    private val _type: String?,
    @SerializedName("notes")
    val notes: String?,
    @SerializedName("photoUrl")
    val photoUrl: String?
) {
    // Exponemos el campo como "type" para mantener consistencia en la app
    val type: String? get() = _type
}
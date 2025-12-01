package com.mdavila_2001.gopuppy.data.remote.models.walk

import com.google.gson.annotations.SerializedName

data class WalkDTO (
    @SerializedName("walker_id")
    val walkerId: Int,
    @SerializedName("pet_id")
    val petId: Int,
    @SerializedName("scheduled_at")
    val scheduledAt: String,
    @SerializedName("duration_minutes")
    val durationMinutes: Int,
    @SerializedName("user_address_id")
    val addressId: Int,
    val notes: String?
)
package com.mdavila_2001.gopuppy.data.remote.models.walk

import com.google.gson.annotations.SerializedName
import com.mdavila_2001.gopuppy.data.remote.models.address.Address
import com.mdavila_2001.gopuppy.data.remote.models.owner.Owner
import com.mdavila_2001.gopuppy.data.remote.models.pet.Pet
import com.mdavila_2001.gopuppy.data.remote.models.walker.Walker

data class Walk (
    @SerializedName("id")
    val id: Int,
    @SerializedName("owner_id")
    val ownerId: Int,
    @SerializedName("walker_id")
    val walkerId: Int,
    @SerializedName("pet_id")
    val petId: Int,
    @SerializedName("user_address_id")
    val userAddressId: Int,
    @SerializedName("status")
    val status: String,
    @SerializedName("scheduled_at")
    val scheduledAt: String,
    @SerializedName("duration_minutes")
    val durationMinutes: Int,
    @SerializedName("notes")
    val notes: String?,
    @SerializedName("pet")
    val pet: Pet,
    @SerializedName("owner")
    val owner: Owner,
    @SerializedName("walker")
    val walker: Walker,
    @SerializedName("address")
    val address: Address
)
package com.mdavila_2001.gopuppy.ui.views.requestwalk

import com.google.gson.annotations.SerializedName
import java.time.LocalDate
import java.time.LocalTime

data class WalkRequest(
    @SerializedName("pet_id")
    val petId: String,
    val date: String, // Format: "YYYY-MM-DD"
    val time: String, // Format: "HH:mm"
    @SerializedName("duration_minutes")
    val durationMinutes: Int,
    @SerializedName("special_instructions")
    val specialInstructions: String? = null,
    @SerializedName("preferred_walker_id")
    val preferredWalkerId: String? = null
)

data class WalkRequestDTO(
    @SerializedName("pet_id")
    val petId: String,
    val date: String,
    val time: String,
    @SerializedName("duration_minutes")
    val durationMinutes: Int,
    @SerializedName("special_instructions")
    val specialInstructions: String? = null,
    @SerializedName("preferred_walker_id")
    val preferredWalkerId: String? = null
)

data class WalkRequestState(
    val selectedPetId: String? = null,
    val date: LocalDate = LocalDate.now(),
    val time: LocalTime = LocalTime.now(),
    val durationMinutes: Int = 30,
    val specialInstructions: String = "",
    val preferredWalker: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null
)

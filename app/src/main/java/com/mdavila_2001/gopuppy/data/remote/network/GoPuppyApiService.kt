package com.mdavila_2001.gopuppy.data.remote.network

import com.mdavila_2001.gopuppy.data.remote.models.address.Address
import com.mdavila_2001.gopuppy.data.remote.models.address.AddressDTO
import com.mdavila_2001.gopuppy.data.remote.models.auth.LoginRequest
import com.mdavila_2001.gopuppy.data.remote.models.auth.UserInfo
import com.mdavila_2001.gopuppy.data.remote.models.auth.signup.AuthResponse
import com.mdavila_2001.gopuppy.data.remote.models.auth.signup.OwnerSignupDTO
import com.mdavila_2001.gopuppy.data.remote.models.auth.signup.WalkerSignupDTO
import com.mdavila_2001.gopuppy.data.remote.models.pet.Pet
import com.mdavila_2001.gopuppy.data.remote.models.pet.PetDTO
import com.mdavila_2001.gopuppy.data.remote.models.review.ReviewDTO
import com.mdavila_2001.gopuppy.data.remote.models.walk.Walk
import com.mdavila_2001.gopuppy.data.remote.models.walk.WalkDTO
import com.mdavila_2001.gopuppy.data.remote.models.review.Review
import com.mdavila_2001.gopuppy.data.remote.models.walker.Availability
import com.mdavila_2001.gopuppy.data.remote.models.walker.Location
import com.mdavila_2001.gopuppy.data.remote.models.walker.Walker
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path

interface GoPuppyApiService {
    @POST("auth/clientlogin")
    suspend fun loginOwner(@Body loginRequest: LoginRequest): Response<AuthResponse>
    @POST("auth/clientregister")
    suspend fun registerOwner(@Body request: OwnerSignupDTO): Response<AuthResponse>
    @POST("auth/walkerlogin")
    suspend fun loginWalker(@Body loginRequest: LoginRequest): Response<AuthResponse>
    @POST("auth/walkerregister")
    suspend fun registerWalker(@Body request: WalkerSignupDTO): Response<AuthResponse>
    @GET("me")
    suspend fun getProfile(): Response<UserInfo>

    @GET("pets")
    suspend fun getMyPets(): Response<List<Pet>>
    @POST("pets")
    suspend fun addPet(@Body pet: PetDTO): Response<Pet>
    @PUT("pets/{id}")
    suspend fun updatePet(@Path("id") id: Int, @Body pet: PetDTO): Response<Pet>
    @DELETE("pets/{id}")
    suspend fun deletePet(@Path("id") id: Int): Response<Pet>
    @Multipart
    @POST("pets/{id}/photo")
    suspend fun uploadPetPhoto(
        @Path("id") id: Int,
        @Part photo: MultipartBody.Part
    ): Response<Void>

    @POST("walkers/nearby")
    suspend fun getNearbyWalkers(@Body location: Location): Response<List<Walker>>
    @GET("walkers/{id}")
    suspend fun getWalkerDetail(@Path("id") id: Int): Response<Walker>
    @POST("walkers/availability")
    suspend fun setAvailability(@Body availability: Availability): Response<Void>
    @POST("walkers/location")
    suspend fun sendLocation(@Body location: Location): Response<Void>
    @Multipart
    @POST("walkers/photo")
    suspend fun uploadWalkerPhoto(@Part photo: MultipartBody.Part): Response<Void>

    @GET("walks")
    suspend fun getWalksHistory(): Response<List<Walk>>
    @GET("walks/pending")
    suspend fun getPendingWalks(): Response<List<Walk>>
    @GET("walks/accepted")
    suspend fun getAcceptedWalks(): Response<List<Walk>>
    @GET("walks/{id}")
    suspend fun getWalkDetail(@Path("id") id: Int): Response<Walk>
    @POST("walks")
    suspend fun createWalk(@Body request: WalkDTO): Response<Walk>

    @POST("walks/{id}/accept")
    suspend fun acceptWalk(@Path("id") id: Int): Response<Void>
    @POST("walks/{id}/reject")
    suspend fun rejectWalk(@Path("id") id: Int): Response<Void>
    @POST("walks/{id}/start")
    suspend fun startWalk(@Path("id") id: Int): Response<Void>
    @POST("walks/{id}/end")
    suspend fun endWalk(@Path("id") id: Int): Response<Void>

    @Multipart
    @POST("walks/{id}/photo")
    suspend fun uploadWalkPhoto(
        @Path("id") id: Int,
        @Part photo: MultipartBody.Part
    ): Response<Void>
    @GET("walks/{id}/photos")
    suspend fun getWalkPhotos(@Path("id") id: Int): Response<List<String>>
    @POST("walks/{id}/review")
    suspend fun sendReview(
        @Path("id") id: Int,
        @Body review: ReviewDTO
    ): Response<Void>

    @GET("reviews")
    suspend fun getMyReviews(): Response<List<Review>>

    @Multipart
    @POST("owners/photo")
    suspend fun uploadOwnerPhoto(@Part photo: MultipartBody.Part): Response<Void>

    @GET("addresses")
    suspend fun getAddresses(): Response<List<Address>>

    @POST("addresses")
    suspend fun addAddress(
        @Body address: AddressDTO
    ): Response<Address>

    @DELETE("addresses/{id}")
    suspend fun deleteAddress(@Path("id") id: Int): Response<Void>
}
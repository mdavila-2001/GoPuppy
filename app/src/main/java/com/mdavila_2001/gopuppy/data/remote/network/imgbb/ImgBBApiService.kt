package com.mdavila_2001.gopuppy.data.remote.network.imgbb

import com.mdavila_2001.gopuppy.data.remote.models.imgbb.ImgBbResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface ImgBBApiService {
    @Multipart
    @POST("1/upload")
    suspend fun uploadImage(
        @Query("key") apiKey: String,
        @Part image: MultipartBody.Part
    ): Response<ImgBbResponse>
}
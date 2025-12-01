package com.mdavila_2001.gopuppy.data.repositories

import android.content.Context
import android.net.Uri
import com.mdavila_2001.gopuppy.data.remote.network.imgbb.ImgBBApiService
import com.mdavila_2001.gopuppy.data.remote.network.imgbb.ImgBBRetrofitInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

class ImageRepository(
    private val api: ImgBBApiService = ImgBBRetrofitInstance.api
) {
    private val apiKey = "96e77af03c73f4698389152db9bb5e37"

    suspend fun uploadImage(context: Context, imageUri: Uri): String? {
        return withContext(Dispatchers.IO) {
            try {
                val inputStream = context.contentResolver.openInputStream(imageUri)
                val imageBytes = inputStream?.readBytes()
                inputStream?.close()

                if (imageBytes == null) {
                    return@withContext null
                }

                val requestBody = imageBytes.toRequestBody(
                    "image/jpeg".toMediaTypeOrNull(),
                    0,
                    imageBytes.size
                )
                val imagePart = MultipartBody.Part.createFormData(
                    "image",
                    "image.jpg",
                    requestBody
                )

                val response = api.uploadImage(apiKey, imagePart)

                if (response.isSuccessful) {
                    response.body()?.data?.displayUrl
                } else {
                    null
                }
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }
}
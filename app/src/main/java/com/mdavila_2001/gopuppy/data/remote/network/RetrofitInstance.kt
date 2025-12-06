package com.mdavila_2001.gopuppy.data.remote.network

import android.content.Context
import com.google.gson.GsonBuilder
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitInstance {
    private const val BASE_URL = "https://apimascotas.jmacboy.com/api/"
    private const val PREFS_NAME = "gopuppy_prefs"
    private const val KEY_TOKEN = "auth_token"

    @Volatile
    private var appContext: Context? = null
    
    fun init(context: Context) {
        if (appContext == null) {
            appContext = context.applicationContext
            android.util.Log.d("RetrofitInstance", "Contexto inicializado correctamente")
        }
    }

    var authToken: String? = null
        get() {
            if (field == null && appContext != null) {
                val prefs = appContext!!.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                field = prefs.getString(KEY_TOKEN, null)
                android.util.Log.d("RetrofitInstance", "Token recuperado de SharedPrefs: ${field?.take(30) ?: "NULL"}")
            }
            return field
        }
        set(value) {
            field = value
            android.util.Log.d("RetrofitInstance", "Guardando token: ${value?.take(30) ?: "NULL"}")
            appContext?.let { context ->
                val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                val success = prefs.edit().putString(KEY_TOKEN, value).commit()
                android.util.Log.d("RetrofitInstance", "Token guardado en SharedPrefs: $success")
            } ?: run {
                android.util.Log.e("RetrofitInstance", "appContext es NULL, no se puede guardar el token")
            }
        }

    private val authInterceptor = Interceptor { chain ->
        val originalRequest = chain.request()
        val requestBuilder = originalRequest.newBuilder()

        authToken?.let { token ->
            requestBuilder.addHeader("Authorization", "Bearer $token")
        }
        requestBuilder.addHeader("Accept", "application/json")
        chain.proceed(requestBuilder.build())
    }

    private val responseInterceptor = Interceptor { chain ->
        val request = chain.request()
        val response = chain.proceed(request)
        if (request.url.toString().contains("/walks")) {
            android.util.Log.d("RetrofitResponse", "========================================")
            android.util.Log.d("RetrofitResponse", "URL: ${request.url}")
            android.util.Log.d("RetrofitResponse", "Method: ${request.method}")
            android.util.Log.d("RetrofitResponse", "Response Code: ${response.code}")
            android.util.Log.d("RetrofitResponse", "Response Message: ${response.message}")
            android.util.Log.d("RetrofitResponse", "Content-Type: ${response.header("Content-Type")}")
            val responseBody = response.peekBody(Long.MAX_VALUE)
            val bodyString = responseBody.string()
            android.util.Log.d("RetrofitResponse", "Body (primeros 1000 chars): ${bodyString.take(1000)}")
            android.util.Log.d("RetrofitResponse", "========================================")
        }
        response
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .addInterceptor(responseInterceptor)
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    private val gson = GsonBuilder()
        .setLenient()
        .create()

    val apiService: GoPuppyApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(GoPuppyApiService::class.java)
    }
}
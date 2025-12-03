package com.mdavila_2001.gopuppy.data.remote.network

import android.content.Context
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

    private var appContext: Context? = null
    
    fun init(context: Context) {
        appContext = context.applicationContext
    }

    var authToken: String? = null
        get() {
            if (field == null && appContext != null) {
                // Intentar cargar desde SharedPreferences
                val prefs = appContext!!.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                field = prefs.getString(KEY_TOKEN, null)
                android.util.Log.d("RetrofitInstance", "Token recuperado de SharedPrefs: ${field?.take(30) ?: "NULL"}")
            }
            return field
        }
        set(value) {
            field = value
            android.util.Log.d("RetrofitInstance", "Guardando token: ${value?.take(30) ?: "NULL"}")
            // Guardar en SharedPreferences
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

        chain.proceed(requestBuilder.build())
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    val apiService: GoPuppyApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GoPuppyApiService::class.java)
    }
}
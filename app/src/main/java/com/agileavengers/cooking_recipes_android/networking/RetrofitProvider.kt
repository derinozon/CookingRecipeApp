package com.agileavengers.cooking_recipes_android.networking

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitProvider {
    // Change this value to the IP address of where the backend API resides.
    // If you are using the Android Emulator and running the backend in your localhost, use "10.0.2.2" as the IP address.
    // Make sure to disable your firewall if you are running the backend locally
    private const val apiUrl = "http://192.168.178.30:8080"

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()

    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(apiUrl)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
}

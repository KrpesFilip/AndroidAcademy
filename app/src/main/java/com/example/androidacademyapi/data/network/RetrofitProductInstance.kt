package com.example.androidacademyapi.data.network

import com.example.androidacademyapi.data.network.apiservice.RetrofitProductApiService
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import kotlin.getValue
import kotlin.jvm.java

val jsonConfig = Json {
    ignoreUnknownKeys = true
}
object RetrofitProductInstance {
    private const val BASE_URL = "https://dummyjson.com/"
    private val contentType = "application/json".toMediaType()


    val api: RetrofitProductApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(jsonConfig.asConverterFactory(contentType))
            .build()
            .create(RetrofitProductApiService::class.java)
    }
}
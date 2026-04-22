package com.example.androidacademyapi

import com.example.androidacademyapi.data.network.apiservice.KtorProductApiService
import com.example.androidacademyapi.data.network.jsonConfig
import com.example.androidacademyapi.data.repository.KtorProductRepository
import com.example.androidacademyapi.data.repository.ProductRepository
import com.example.androidacademyapi.data.repository.RetrofitProductRepository
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json

object AppContainer {

    private val client = HttpClient(Android) {
        install(Logging){
            logger = Logger.DEFAULT
            level = LogLevel.ALL
        }
        install(ContentNegotiation) {
            json(jsonConfig)
        }
    }

    private val ktorApiService by lazy {
        KtorProductApiService(
            client
        )
    }

    private const val USE_KTOR = false

    val productRepository: ProductRepository by lazy {
        if (USE_KTOR) {
            KtorProductRepository(ktorApiService)
        } else {
            RetrofitProductRepository()
        }
    }

}
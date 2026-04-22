package com.example.androidacademyapi.data.network.apiservice

import com.example.androidacademyapi.data.model.Product
import com.example.androidacademyapi.data.model.ProductRequest
import com.example.androidacademyapi.data.model.ProductResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.utils.EmptyContent.contentType
import io.ktor.http.ContentType
import io.ktor.http.contentType
import okhttp3.OkHttpClient

class KtorProductApiService(private val client: HttpClient) {
    private val baseUrl = "https://dummyjson.com"

    suspend fun getProducts(): ProductResponse {
        return client.get("$baseUrl/products").body()
    }

    suspend fun addProduct(request: ProductRequest): Product {
        return client.post("$baseUrl/products/add") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    suspend fun updateProduct(id: Int, request: ProductRequest): Product {
        return client.put("$baseUrl/products/$id") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    suspend fun deleteProduct(id: Int): Product {
        return client.delete("$baseUrl/products/$id").body()
    }
}
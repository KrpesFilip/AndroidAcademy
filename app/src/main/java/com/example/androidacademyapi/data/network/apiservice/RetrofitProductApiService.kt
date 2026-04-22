package com.example.androidacademyapi.data.network.apiservice

import com.example.androidacademyapi.data.model.Product
import com.example.androidacademyapi.data.model.ProductRequest
import com.example.androidacademyapi.data.model.ProductResponse
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface RetrofitProductApiService {
    @GET("products")
    suspend fun getProducts(): ProductResponse

    @POST("products/add")
    suspend fun addProduct(@Body product: ProductRequest): Product

    @PUT("products/{id}")
    suspend fun updateProduct(
        @Path("id") id: Int,
        @Body product: ProductRequest
    ): Product

    @DELETE("products/{id}")
    suspend fun deleteProduct(
        @Path("id") id: Int
    ): Product

    @GET("products/{id}")
    suspend fun getProduct(@Path("id") id: Int): Product
}
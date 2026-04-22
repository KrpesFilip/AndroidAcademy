package com.example.androidacademyapi.data.repository

import com.example.androidacademyapi.data.model.Product
import com.example.androidacademyapi.data.model.ProductRequest

interface ProductRepository {
    suspend fun getProducts(): Result<List<Product>>
    suspend fun addProduct(request: ProductRequest): Result<Product>
    suspend fun updateProduct(id: Int, request: ProductRequest): Result<Product>
    suspend fun deleteProduct(id: Int): Result<Product>
    suspend fun getProduct(productId: Int): Result<Product>
}
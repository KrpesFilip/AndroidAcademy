package com.example.androidacademyapi.data.model

import kotlinx.serialization.Serializable

@Serializable
data class ProductResponse(
    val products: List<Product>
)
@Serializable
data class Product(
    val id: Int,
    val title: String,
    val description: String? = null,
    val thumbnail: String? = null,
    val images: List<String> = listOf()
)

package com.example.androidacademyapi.data.model

import kotlinx.serialization.Serializable

@Serializable
data class ProductRequest(
    val title: String,
    val description: String
)

package com.example.androidacademyapi.ui.productdetails

import com.example.androidacademyapi.data.model.Product

sealed interface ProductDetailsUIState {
    object Loading : ProductDetailsUIState
    data class Success(val product: Product) : ProductDetailsUIState
    data class Error(val message: String) : ProductDetailsUIState
}
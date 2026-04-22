package com.example.androidacademyapi.ui.productlistscreen

import com.example.androidacademyapi.data.model.Product

sealed interface ProductListUIState{
    data object Loading: ProductListUIState
    data class Error(val e: Throwable): ProductListUIState
    data class Success(val products: List<Product>): ProductListUIState
    data object NoInternet : ProductListUIState
}
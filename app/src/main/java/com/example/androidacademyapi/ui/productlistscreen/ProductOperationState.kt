package com.example.androidacademyapi.ui.productlistscreen

sealed interface ProductOperationState {
    data object Idle: ProductOperationState
    data class Error(val message: String): ProductOperationState
    data class Success(val message: String): ProductOperationState
}
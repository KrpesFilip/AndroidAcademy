package com.example.androidacademyapi.ui.productlistscreen

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.androidacademyapi.data.model.ProductRequest
import com.example.androidacademyapi.data.repository.ProductRepository
import kotlinx.coroutines.launch

class ProductListViewModel(private val productRepository: ProductRepository) : ViewModel() {
    private val _productListUIState: MutableState<ProductListUIState> = mutableStateOf(
        ProductListUIState.Loading
    )
    val productListUIState: State<ProductListUIState> = _productListUIState

    private val _productOperationState: MutableState<ProductOperationState> = mutableStateOf(
        ProductOperationState.Idle)
    val productOperationState: State<ProductOperationState> = _productOperationState

    init {
        getProductList()
    }

    fun getProductList() {
        viewModelScope.launch {
            _productListUIState.value = ProductListUIState.Loading
            productRepository.getProducts()
                .onSuccess {
                    _productListUIState.value = ProductListUIState.Success(it)
                }
                .onFailure { e ->
                    println("Exception: ${e.message}")

                    if (e is java.io.IOException) {
                        _productListUIState.value = ProductListUIState.NoInternet
                    } else {
                        _productListUIState.value = ProductListUIState.Error(e)
                    }
                }

        }
    }

    fun addProduct(
        title: String,
        description: String
    ) {
        viewModelScope.launch {
            productRepository.addProduct(
                request = ProductRequest(
                    title = title,
                    description = description
                )
            )
                .onSuccess {
                    _productOperationState.value = ProductOperationState.Success("You have added ${it.title}")

                }
                .onFailure {
                    println(it.message)
                    _productOperationState.value = ProductOperationState.Error("Adding product failed")

                }
        }
    }

    fun editProduct(
        title: String,
        description: String,
        id: Int
    ) {
        viewModelScope.launch {
            productRepository.updateProduct(
                request = ProductRequest(
                    title = title,
                    description = description
                ),
                id = id
            )
                .onSuccess {
                    _productOperationState.value = ProductOperationState.Success("You have edited ${it.title}")

                }
                .onFailure {
                    _productOperationState.value = ProductOperationState.Error("Editing product failed")

                }
        }
    }

    fun deleteProduct(
        id: Int
    ) {
        viewModelScope.launch {
            productRepository.deleteProduct(id = id)
                .onSuccess {
                    _productOperationState.value = ProductOperationState.Success("You have deleted product")

                }
                .onFailure {
                    _productOperationState.value = ProductOperationState.Error("Deleting product failed")

                }
        }
    }

    fun resetProductOperationState(){
        viewModelScope.launch {
            _productOperationState.value = ProductOperationState.Idle
        }
    }

}

class ProductListViewModelFactory(
    private val repository: ProductRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProductListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProductListViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
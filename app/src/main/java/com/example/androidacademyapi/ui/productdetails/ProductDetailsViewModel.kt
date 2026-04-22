package com.example.androidacademyapi.ui.productdetails

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.androidacademyapi.data.repository.ProductRepository
import com.example.androidacademyapi.ui.productlistscreen.ProductListViewModel
import kotlinx.coroutines.launch
import androidx.compose.runtime.State

class ProductDetailsViewModel(
    private val repository: ProductRepository,
    private val productId: Int
) : ViewModel() {
    private val _uiState = mutableStateOf<ProductDetailsUIState>(ProductDetailsUIState.Loading)
    val uiState: State<ProductDetailsUIState> = _uiState

    init {
        getProduct()
    }

    private fun getProduct() {
        viewModelScope.launch {
            if (productId < 0) {
                _uiState.value = ProductDetailsUIState.Error("Invalid product ID")
                return@launch
            }

            _uiState.value = ProductDetailsUIState.Loading

            repository.getProduct(productId)
                .onSuccess {
                    _uiState.value = ProductDetailsUIState.Success(it)
                }
                .onFailure {
                    _uiState.value = ProductDetailsUIState.Error(it.message ?: "Error")
                }
        }
    }

}

class ProductDetailsViewModelFactory(
    private val repository: ProductRepository,
    private val productId: Int
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProductDetailsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProductDetailsViewModel(repository,productId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
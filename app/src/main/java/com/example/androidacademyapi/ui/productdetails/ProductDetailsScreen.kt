package com.example.androidacademyapi.ui.productdetails

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.androidacademyapi.AppContainer
import com.example.androidacademyapi.ui.productlistscreen.ProductListViewModel
import com.example.androidacademyapi.ui.productlistscreen.ProductListViewModelFactory

@Composable
fun ProductDetailsScreen(navController: NavController,productId: Int){
    val viewModel: ProductDetailsViewModel =
        viewModel(factory = ProductDetailsViewModelFactory(AppContainer.productRepository,productId))

    val uiState = viewModel.uiState.value

    ProductDetailsContent (
        uiState = uiState,
        onNavigateBack = { navController.popBackStack()
        }
    )

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailsContent(
    uiState: ProductDetailsUIState,
    onNavigateBack:()-> Unit
){
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text("Product title", style = MaterialTheme.typography.titleMedium)
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = null
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)

            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {

            when (uiState) {

                is ProductDetailsUIState.Loading -> {
                    Text("Loading...")
                }

                is ProductDetailsUIState.Error -> {
                    Text("Error: ${uiState.message}")
                }

                is ProductDetailsUIState.Success -> {
                    val product = uiState.product

                    Text(
                        text = product.title,
                        style = MaterialTheme.typography.titleLarge
                    )

                    Text(
                        text = product.description ?: ""
                    )
                }
            }
        }
    }
}
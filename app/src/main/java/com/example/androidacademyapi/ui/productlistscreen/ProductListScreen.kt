package com.example.androidacademyapi.ui.productlistscreen

import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.androidacademyapi.AppContainer
import com.example.androidacademyapi.Screen
import com.example.androidacademyapi.data.model.Product
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductListScreen(navController: NavController){
    val viewModel: ProductListViewModel =
        viewModel(factory = ProductListViewModelFactory(AppContainer.productRepository))
    val productListUIState = viewModel.productListUIState.value
    val productOperationState = viewModel.productOperationState.value

    when(productOperationState){
        ProductOperationState.Idle -> {
            //idle state, nothing should be done
        }

        is ProductOperationState.Error -> {
            ProductOperationAlertDialog(
                title = productOperationState.message,
                onDismissRequest = {
                    viewModel.resetProductOperationState()

                }
            )
        }
        is ProductOperationState.Success -> {
            ProductOperationAlertDialog(
                title = productOperationState.message,
                onDismissRequest = {
                    viewModel.resetProductOperationState()
                }
            )
        }
    }
    ProductListContent(
        productListUIState = productListUIState,
        onAddClick = {
            viewModel.addProduct(
                "New product",
                "New description"
            )

        },
        onEditClick = { id->
            viewModel.editProduct(
                title = "Edited product title",
                description = "Edited description",
                id = id
            )

        },
        onDeleteClick = { id ->
            viewModel.deleteProduct(id)

        },
        onItemClick = { id->
            navController.navigate(
                Screen.ProductDetail.createRoute(id)
            )

        }
    )

}

@Composable
fun ProductListContent(
    productListUIState: ProductListUIState,
    onAddClick:()-> Unit,
    onDeleteClick: (Int) -> Unit,
    onEditClick: (Int) -> Unit,
    onItemClick: (Int) -> Unit
){
    Scaffold(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddClick
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null
                )
            }
        }

    ) { paddingValues ->
        when(productListUIState){
            is ProductListUIState.Error -> {
                Column(
                    modifier = Modifier.fillMaxSize().padding(paddingValues),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Oops! Something went wrong",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            ProductListUIState.Loading -> {
                Column(
                    modifier = Modifier.fillMaxSize().padding(paddingValues),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is ProductListUIState.Success -> {
                LazyColumn(modifier = Modifier.padding(paddingValues)) {
                    items(productListUIState.products){ product ->
                        ListItemWithMenu(
                            product = product,
                            onDeleteClick = onDeleteClick,
                            onEditClick = onEditClick,
                            onItemClick = onItemClick
                        )

                    }

                }
            }
            ProductListUIState.NoInternet -> {
                Column(
                    modifier = Modifier.fillMaxSize().padding(paddingValues),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "No Internet Connection",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text("Please check your connection")
                }
            }
        }
    }
}

@Composable
fun ListItemWithMenu(
    product: Product,
    onDeleteClick:(Int) -> Unit,
    onEditClick:(Int)-> Unit,
    onItemClick:(Int)-> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        ListItem(
            leadingContent = {
                product.thumbnail?.let { imageUrl ->
                    AsyncImage(
                        model = imageUrl,
                        contentDescription = product.title,
                        modifier = Modifier
                            .size(56.dp)

                    )
                }
            },
            headlineContent = {
                Text(
                    text = product.title,
                    style = MaterialTheme.typography.titleMedium,
                )
            },
            supportingContent = {
                product.description?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.labelMedium
                    )
                }

            },
            modifier = Modifier
                .fillMaxWidth()
                .combinedClickable(
                    onClick = {
                        onItemClick(product.id)
                    },
                    onLongClick = {
                        expanded = true
                    }
                )
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text("Edit") },
                onClick = {
                    expanded = false
                    onEditClick(product.id)
                    // Handle edit
                }
            )
            DropdownMenuItem(
                text = { Text("Delete") },
                onClick = {
                    expanded = false
                    onDeleteClick(product.id)
                    // Handle delete
                }
            )
        }
    }
}
package com.example.androidacademyapi

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.androidacademyapi.ui.productdetails.ProductDetailsScreen
import com.example.androidacademyapi.ui.productlistscreen.ProductListScreen
import com.example.androidacademyapi.ui.theme.AndroidAcademyAPITheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AndroidAcademyAPITheme {
                AppNavigation()
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.ProductList.route
    ) {

        composable(Screen.ProductList.route) {
            ProductListScreen(navController)
        }

        composable(
            route = Screen.ProductDetail.route,
            arguments = listOf(
                navArgument("productId") { type = NavType.IntType }
            )
        ) { backStackEntry ->

            val productId =
                backStackEntry.arguments?.getInt("productId") ?: 0

            ProductDetailsScreen(navController,productId)
        }
    }
}

sealed class Screen(val route: String) {
    object ProductList : Screen("product_list")
    object ProductDetail : Screen("product_detail/{productId}") {
        fun createRoute(productId: Int) = "product_detail/$productId"
    }
}
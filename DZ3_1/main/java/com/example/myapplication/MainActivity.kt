package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.example.myapplication.ui.theme.MyApplicationTheme
import androidx.compose.material3.Button
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.clickable
import androidx.navigation.NavController
import androidx.navigation.compose.*
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                val navController = rememberNavController()

                val data = listOf(
                    MyData(1, "Title 1", "DES 1"),
                    MyData(2, "Title 2", "DES 2"),
                    MyData(3, "Title 3", "DES 3"),
                    MyData(4, "Title 4", "DES 4"),
                    MyData(5, "Title 5", "DES 5")
                )

                NavHost(navController = navController, startDestination = "list") {

                    composable("list") {
                        ListScreen(navController, data)
                    }

                    composable("detail/{id}") { backStackEntry ->
                        val id = backStackEntry.arguments?.getString("id")?.toInt() ?: 0
                        val item = data.find { it.id == id }

                        if (item != null) {
                            DetailScreen(navController, item)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TitleText(title: String) {
    Text(
        text = title,
        fontSize = 26.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF6200EE),
        letterSpacing = 1.5.sp
    )
}

@Composable
fun DescriptionText(description: String) {
    Text(
        text = description,
        fontSize = 14.sp,
        maxLines = 3,
        color = Color.Gray,
        lineHeight = 20.sp
    )
}

@Composable
fun CustomButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF6200EE),
            contentColor = Color.White
        )
    ) {
        Text(text = text)
    }
}

data class MyData(
    val id: Int,
    val title: String,
    val description: String
)

@Composable
fun ItemCard(data: MyData) {
    Card(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            Icon(
                imageVector = Icons.Default.Favorite,
                contentDescription = null,
                modifier = Modifier.size(48.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            TitleText(data.title)

            Spacer(modifier = Modifier.height(4.dp))

            DescriptionText(data.description)

            Spacer(modifier = Modifier.height(12.dp))

            Row {

                Button(onClick = { }) {
                    Text("Favorite")
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(onClick = { }) {
                    Text("Save")
                }
            }
        }
    }
}

@Composable
fun MyItemList(initialItems: List<MyData>) {

    var items by remember { mutableStateOf(initialItems) }

    Column(modifier = Modifier.fillMaxSize()) {

        Button(
            onClick = {
                items = items.shuffled()
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text("Shuffle")
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(items) { item ->
                ItemCard(item)

                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun ListScreen(navController: NavController, data: List<MyData>) {

    var items by remember { mutableStateOf(data) }

    Column {

        Button(
            onClick = { items = items.shuffled() },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text("Shuffle")
        }

        LazyColumn {
            items(items) { item ->

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            navController.navigate("detail/${item.id}")
                        }
                ) {
                    ItemCard(item)
                }
            }
        }
    }
}

@Composable
fun DetailScreen(navController: NavController, item: MyData) {

    Column(modifier = Modifier.padding(16.dp)) {

        TitleText(item.title)

        Spacer(modifier = Modifier.height(8.dp))

        DescriptionText(item.description)

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            navController.popBackStack()
        }) {
            Text("Back")
        }
    }
}
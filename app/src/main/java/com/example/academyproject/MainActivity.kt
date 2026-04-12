package com.example.academyproject

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Button
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavController
import androidx.navigation.compose.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {

            var notes by remember {
                mutableStateOf(
                    mutableListOf(
                        Note(1, "tit1", ""),
                        Note(2, "tit2", "")
                    )
                )
            }

            val navController = rememberNavController()
            Scaffold { innerPadding ->

                Box(modifier = Modifier.padding(innerPadding)) {
                    //notesScreen()
                    NavHost(
                        navController = navController,
                        startDestination = "notes"
                    ) {
                        composable("notes") {
                            notesScreen(navController, notes)
                        }

                        composable("description/{noteId}") {backStackEntry ->
                            val noteId = backStackEntry.arguments?.getString("noteId")?.toInt() ?: -1

                            descriptionScreen(
                                navController = navController,
                                notes = notes,
                                noteId = noteId
                            )
                        }
                    }
                }
            }
        }
    }
}


data class Note(
    val id:Int,
    var title:String,
    var description:String
)

@Composable
fun noteElement(note:Note, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(50.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = note.title,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Left
            )

        }

    }
}

@Composable
fun editableNoteElement(note: Note, onChange:(Note)->Unit) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(50.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            TextField(
                value = note.title,
                onValueChange = {
                    onChange(note.copy(title = it))
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                placeholder = {
                    Text("Enter title...")
                },
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent
                )
            )
        }
    }
}

@Composable
fun notesScreenTopBanner(onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Notes",
            fontWeight = FontWeight.Bold,
            fontSize = 30.sp,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center
        )

        Button(
            onClick = onClick

        ) {
            Text(
                text = "+",
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun descriptionScreenTopBanner(onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(
            onClick = onClick

        ) {
            Text(
                text = "<-",
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun notesList(notes: List<Note>, navController: NavController){

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ){
        items(notes) { item ->
            noteElement(item) {
                navController.navigate("description/${item.id}")
            }
        }
    }
}

@Composable
fun notesScreen(navController: NavController, notes: List<Note>){


    Column {
        notesScreenTopBanner {
            val newId = (notes.maxOfOrNull { it.id } ?: 0) + 1
            navController.navigate("description/$newId")
        }
        notesList(notes, navController)
    }

}

@Composable
fun descriptionBox(note: Note, onChange: (Note) -> Unit) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        TextField(
            value = note.description,
            onValueChange = {
                onChange(note.copy(description = it))
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .padding(16.dp),
            placeholder = {
                Text("Enter description...")
            },
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                disabledContainerColor = Color.Transparent
            )
        )
    }
}

@Composable
fun descriptionScreen(
    navController: NavController,
    notes: MutableList<Note>,
    noteId: Int){

    val focusManager = LocalFocusManager.current

    var note by remember {
        mutableStateOf(
            notes.find { it.id == noteId }
                ?: Note(noteId, "", "")
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                focusManager.clearFocus()
            },
        verticalArrangement = Arrangement.spacedBy(8.dp)) {
        descriptionScreenTopBanner {
            navController.popBackStack()
        }
        editableNoteElement(note){
            note = it
        }
        descriptionBox(note){
            note = it
        }

        Button(
            onClick = {
                val index = notes.indexOfFirst { it.id == note.id }

                if (index >= 0) {
                    notes[index] = note
                } else {
                    notes.add(note)
                }

                navController.popBackStack()
            },
            modifier = Modifier.padding(16.dp)
        ) {
            Text("Save")
        }

    }

}



/*
@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    AcademyProjectTheme {
        Greeting("Android")
    }
}

 */
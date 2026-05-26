package com.example.academyproject

import PutTaskRequest
import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.*
import com.example.academyproject.model.CreateTaskRequest
import com.example.academyproject.model.LoginRequest
import com.example.academyproject.model.Task
import com.example.academyproject.network.RetrofitInstance
import com.example.academyproject.network.SessionManager
import kotlinx.coroutines.launch
import androidx.compose.foundation.combinedClickable

class MainActivity : ComponentActivity() {
    @SuppressLint("CoroutineCreationDuringComposition")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        lifecycleScope.launch {
            try {
                val response = RetrofitInstance.api.login(
                    LoginRequest(
                        username = "test@test.com",
                        password = "123456"
                    )
                )

                SessionManager.token = response.token

                Log.d("LOGIN", response.token)

                setContent{
                    AppNavigation()
                }

            } catch (e: Exception) {
                Log.e("LOGIN", e.message.toString())
            }
        }




    }
}

@Composable
fun AppNavigation() {

    val navController = rememberNavController()
    val viewModel: TaskViewModel = viewModel() // 👈 ONE shared instance

    Scaffold { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {

            NavHost(
                navController = navController,
                startDestination = "notes"
            ) {
                composable("notes") {
                    notesScreen(navController, viewModel)
                }

                composable("edit/{noteId}") {
                    val id = it.arguments?.getString("noteId") ?: ""
                    descriptionScreen(navController, id, viewModel)
                }
            }
        }
    }
}

class TaskViewModel : ViewModel() {

    var tasks = mutableStateListOf<Task>()
        private set

    var selectedTask by mutableStateOf<Task?>(null)
        private set


    fun setTask(task: Task?) {
        selectedTask = task
    }
    fun updateSelectedTask(update: (Task) -> Task) {
        selectedTask = selectedTask?.let(update)
    }

    fun loadTaskById(id: String) {
        viewModelScope.launch {
            try {
                selectedTask = RetrofitInstance.api.getTask(id)
            } catch (e: Exception) {
                Log.e("TASK", e.message.toString())
            }
        }
    }

    fun loadTasks() {
        viewModelScope.launch {
            try {
                val result = RetrofitInstance.api.getTasks()
                tasks.clear()
                tasks.addAll(result.tasks)
            } catch (e: Exception) {
                Log.e("TASKS", e.message.toString())
            }
        }
    }

    fun deleteTask(id: String?) {
        if (id == null) return

        viewModelScope.launch {
            try {
                RetrofitInstance.api.deleteTask(id)
                loadTasks()
            } catch (e: Exception) {
                Log.e("DELETE", e.message.toString())
            }
        }
    }

    fun createTask(title: String, body: String) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.createTask(
                    CreateTaskRequest(title, body)
                )

                Log.d("CREATE", "Created task with id: ${response.id}")

                loadTasks()
            } catch (e: Exception) {
                Log.e("CREATE", e.message.toString())
            }
        }
    }


    fun updateTask(id: String, title: String, body: String) {
        viewModelScope.launch {
            try {
                RetrofitInstance.api.updateTask(
                    id,
                    PutTaskRequest(title, body)
                )
                loadTasks()
            } catch (e: Exception) {
                Log.e("UPDATE", e.message.toString())
            }
        }
    }
}



@Composable
fun editableTaskElement(task: Task, onChange:(Task)->Unit) {

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
                value = task.title,
                onValueChange = {
                    onChange(task.copy(title = it))
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
fun notesScreen(
    navController: NavController,
    viewModel: TaskViewModel) {

    var showDeleteDialog by remember { mutableStateOf(false) }
    var taskToDelete by remember { mutableStateOf<Task?>(null) }



    LaunchedEffect(Unit) {
        viewModel.loadTasks()
    }

    Column {

        notesScreenTopBanner {
            navController.navigate("edit/-1")
        }

        LazyColumn {
            items(viewModel.tasks) { task ->

                taskElement(
                    task = task,
                    onClick = {
                        navController.navigate("edit/${task.id}")
                    },
                    onLongClick = {
                        taskToDelete = task
                        showDeleteDialog = true
                    }
                )
            }
        }

        if (showDeleteDialog) {
            androidx.compose.material3.AlertDialog(
                onDismissRequest = {
                    showDeleteDialog = false
                    taskToDelete = null
                },
                title = {
                    Text("Delete task?")
                },
                text = {
                    Text("Are you sure you want to delete this task?")
                },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.deleteTask(taskToDelete?.id)
                            showDeleteDialog = false
                            taskToDelete = null
                        }
                    ) {
                        Text("Delete")
                    }
                },
                dismissButton = {
                    Button(
                        onClick = {
                            showDeleteDialog = false
                            taskToDelete = null
                        }
                    ) {
                        Text("Cancel")
                    }
                }
            )
        }

    }
}

@Composable
fun taskElement(
    task: Task,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            ),
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
            Text(task.title)
        }
    }
}

@Composable
fun descriptionBox(task: Task, onChange: (Task) -> Unit) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        TextField(
            value = task.body,
            onValueChange = {
                onChange(task.copy(body = it))
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
    noteId: String,
    viewModel: TaskViewModel
) {
    val task = viewModel.selectedTask
    val scope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current
    val isNew = noteId == "-1"

    LaunchedEffect(noteId) {
        if (isNew) {
            viewModel.setTask(
                Task(id = null, title = "", body = "")
            )
        } else {
            viewModel.loadTaskById(noteId)
        }
    }

    if (task == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Loading...")
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                focusManager.clearFocus()
            }
            .padding(16.dp)
    ) {

        descriptionScreenTopBanner {
            navController.popBackStack()
        }

        TextField(
            value = task.title,
            onValueChange = {
                viewModel.updateSelectedTask { current ->
                    current.copy(title = it)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Enter title...") }
        )

        TextField(
            value = task.body,
            onValueChange = {
                viewModel.updateSelectedTask { current ->
                    current.copy(body = it)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            placeholder = { Text("Enter description...") }
        )

        Button(onClick = {
            scope.launch {
                if (isNew) {
                    viewModel.createTask(task.title, task.body)
                } else {
                    viewModel.updateTask(noteId, task.title, task.body)
                }
                navController.popBackStack()
            }
        }) {
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
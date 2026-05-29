package com.example.academyproject

import kotlinx.coroutines.delay
import PutTaskRequest
import TaskRepository
import android.annotation.SuppressLint
import android.app.Application
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
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.academyproject.data.local.DatabaseProvider
import com.example.academyproject.data.local.TaskEntity
import com.example.academyproject.data.repository.TaskRepositoryContract
import com.example.academyproject.util.AppLogger
import java.util.UUID

class MainActivity : ComponentActivity() {
    @SuppressLint("CoroutineCreationDuringComposition")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val repository = TaskRepository(
            DatabaseProvider.getDatabase(this).taskDao(),
            RetrofitInstance.api,
            AppLogger("TaskRepository")
        )

        lifecycleScope.launch {
            try {
                val response = RetrofitInstance.api.login(
                    LoginRequest("test@test.com", "123456")
                )

                SessionManager.token = response.token




                setContent {
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
    val context = LocalContext.current

    val viewModel: TaskViewModel = viewModel(
        factory = TaskViewModelFactory(context.applicationContext as Application)
    )

    LaunchedEffect(Unit) {
        viewModel.syncOnStart()
    }

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


            if (viewModel.isSyncing) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp),
                    contentAlignment = Alignment.BottomEnd
                ) {
                    androidx.compose.material3.CircularProgressIndicator(
                        strokeWidth = 2.dp
                    )
                }
            }
        }
    }
}

class TaskViewModel(
    application: Application,
    private val repository: TaskRepositoryContract
) : AndroidViewModel(application) {

    private val db = com.example.academyproject.data.local.DatabaseProvider
        .getDatabase(application)

    private val dao = db.taskDao()

    var tasks = androidx.compose.runtime.mutableStateListOf<com.example.academyproject.data.local.TaskEntity>()
        private set

    var selectedTask by androidx.compose.runtime.mutableStateOf<com.example.academyproject.data.local.TaskEntity?>(null)
        private set

    var isSyncing by mutableStateOf(false)
        private set

    private val logger = AppLogger("TaskViewModel")

    init {
        observeTasks()
        logger.logD("ViewModel initialized")
    }

    private fun observeTasks() {
        viewModelScope.launch {
            logger.logD("Observing tasks")

            repository.getTasks().collect {
                tasks.clear()
                tasks.addAll(it)

                logger.logD("Tasks updated: ${it.size}")
            }
        }
    }

    fun syncOnStart() {
        viewModelScope.launch {

            logger.logI("syncOnStart started")

            isSyncing = true
            val startTime = System.currentTimeMillis()

            try {
                repository.syncFromRemote()
                logger.logD("syncFromRemote success")
            } catch (e: Exception) {
                logger.logE("syncOnStart failed: ${e.message}")
            }

            val elapsed = System.currentTimeMillis() - startTime
            if (elapsed < 2000) {
                delay(2000 - elapsed)
            }

            isSyncing = false
            logger.logI("syncOnStart finished")
        }
    }

    fun setTask(task: TaskEntity?) {
        selectedTask = task
        logger.logD("Selected task set: ${task?.id}")
    }

    fun updateSelectedTask(
        update: (TaskEntity) -> TaskEntity
    ) {
        selectedTask = selectedTask?.let(update)
        logger.logD("Selected task updated")
    }

    fun createTask(title: String, body: String) {
        viewModelScope.launch {

            logger.logI("createTask started")

            isSyncing = true
            val startTime = System.currentTimeMillis()

            val localTask = TaskEntity(
                id = UUID.randomUUID().toString(),
                title = title,
                body = body,
                isSynced = false
            )

            repository.insertLocal(localTask)
            logger.logD("Local task inserted: ${localTask.id}")

            try {
                val newId = repository.createRemoteTask(title, body)

                repository.deleteLocal(localTask.id)
                repository.insertLocal(
                    localTask.copy(
                        id = newId,
                        isSynced = true
                    )
                )

                logger.logI("Remote task created: $newId")

            } catch (e: Exception) {
                logger.logE("createTask failed: ${e.message}")
            }

            val elapsed = System.currentTimeMillis() - startTime
            if (elapsed < 2000) delay(2000 - elapsed)

            isSyncing = false
            logger.logI("createTask finished")
        }
    }

    fun deleteTask(id: String?) {
        if (id == null) {
            logger.logW("deleteTask called with null id")
            return
        }

        viewModelScope.launch {

            logger.logI("deleteTask started: $id")

            isSyncing = true
            val startTime = System.currentTimeMillis()

            repository.deleteLocal(id)
            logger.logD("Local task deleted: $id")

            try {
                repository.deleteRemoteTask(id)
                logger.logI("Remote task deleted: $id")
            } catch (e: Exception) {
                logger.logE("deleteTask failed: ${e.message}")
            }

            val elapsed = System.currentTimeMillis() - startTime
            if (elapsed < 2000) delay(2000 - elapsed)

            isSyncing = false
            logger.logI("deleteTask finished: $id")
        }
    }

    fun updateTask(task: TaskEntity) {
        viewModelScope.launch {

            logger.logI("updateTask started: ${task.id}")

            isSyncing = true
            val startTime = System.currentTimeMillis()

            repository.updateLocal(task.copy(isSynced = false))
            logger.logD("Marked task unsynced: ${task.id}")

            try {
                repository.updateRemoteTask(task)

                repository.updateLocal(task.copy(isSynced = true))
                logger.logI("Remote update successful: ${task.id}")

            } catch (e: Exception) {
                logger.logE("updateTask failed: ${e.message}")
            }

            val elapsed = System.currentTimeMillis() - startTime
            if (elapsed < 2000) delay(2000 - elapsed)

            isSyncing = false
            logger.logI("updateTask finished: ${task.id}")
        }
    }
}

class TaskViewModelFactory(
    private val application: Application
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val db = DatabaseProvider.getDatabase(application)
        val dao = db.taskDao()

        val repository = TaskRepository(
            dao,
            RetrofitInstance.api,
            AppLogger("TaskRepository")
        )

        return TaskViewModel(application, repository) as T
    }
}



/*  OLD VM
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

*/

@Composable
fun editableTaskElement(task: TaskEntity, onChange:(TaskEntity)->Unit) {

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
    var taskToDelete by remember { mutableStateOf<TaskEntity?>(null) }





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
    task: TaskEntity,
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
fun descriptionBox(task: TaskEntity, onChange: (TaskEntity) -> Unit) {

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
    val task = viewModel.selectedTask ?: TaskEntity(
        id = "",
        title = "",
        body = "",
        isSynced = false
    )
    val scope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current
    val isNew = noteId == "-1"

    LaunchedEffect(noteId) {
        if (isNew) {
            viewModel.setTask(
                TaskEntity(
                    id = java.util.UUID.randomUUID().toString(),
                    title = "",
                    body = "",
                    isSynced = false
                )
            )
        } else {
            val task = viewModel.tasks.find { it.id == noteId }
            viewModel.setTask(task)
        }
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
                    viewModel.updateTask(task)
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
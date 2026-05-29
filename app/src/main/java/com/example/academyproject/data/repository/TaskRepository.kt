import android.util.Log
import com.example.academyproject.data.local.TaskDao
import com.example.academyproject.data.local.TaskEntity
import com.example.academyproject.model.CreateTaskRequest
import com.example.academyproject.network.ApiService

class TaskRepository(
    private val dao: TaskDao,
    private val api: ApiService
) {

    fun getTasks() = dao.getTasks()

    suspend fun insertLocal(task: TaskEntity) {
        dao.insert(task)
    }

    suspend fun deleteLocal(id: String) {
        dao.deleteById(id)
    }

    suspend fun updateLocal(task: TaskEntity) {
        dao.update(task)
    }

    suspend fun syncFromRemote() {
        try {
            val response = api.getTasks()

            dao.clearAll()

            response.tasks.forEach { task ->
                val id = task.id ?: return@forEach

                dao.insert(
                    TaskEntity(
                        id = id,
                        title = task.title,
                        body = task.body,
                        isSynced = true
                    )
                )
            }

        } catch (e: Exception) {
            Log.e("SYNC", "Failed sync: ${e.message}")
        }
    }

    suspend fun deleteRemoteTask(id: String) {
        api.deleteTask(id)
    }

    suspend fun createRemoteTask(title: String, body: String): String {
        val response = api.createTask(
            CreateTaskRequest(title, body)
        )
        return response.id
    }

    suspend fun updateRemoteTask(task: TaskEntity) {
        api.updateTask(
            task.id,
            PutTaskRequest(task.title, task.body)
        )
    }

}
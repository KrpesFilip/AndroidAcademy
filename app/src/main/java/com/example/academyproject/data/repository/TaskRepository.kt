import android.util.Log
import com.example.academyproject.data.local.TaskDao
import com.example.academyproject.data.local.TaskEntity
import com.example.academyproject.data.repository.TaskRepositoryContract
import com.example.academyproject.model.CreateTaskRequest
import com.example.academyproject.network.ApiService
import com.example.academyproject.util.AppLogger
import com.example.academyproject.util.Logger

class TaskRepository(
    private val dao: TaskDao,
    private val api: ApiService,
    private val logger: Logger
): TaskRepositoryContract {


    override fun getTasks() = dao.getTasks()

    override suspend fun insertLocal(task: TaskEntity) {
        dao.insert(task)
        logger.logD("Inserted local task: ${task.id}")
    }

    override suspend fun deleteLocal(id: String) {
        dao.deleteById(id)
        logger.logD("Deleted local task: $id")
    }

    override suspend fun updateLocal(task: TaskEntity) {
        dao.update(task)
        logger.logD("Updated local task: ${task.id}")
    }

    override suspend fun syncFromRemote() {
        try {
            logger.logI("syncFromRemote started")

            val response = api.getTasks()

            logger.logD("Fetched ${response.tasks.size} tasks from server")

            dao.clearAll()
            logger.logW("Local database cleared before sync")

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

            logger.logI("syncFromRemote completed successfully")

        } catch (e: Exception) {
            logger.logE("syncFromRemote failed: ${e.message}")
        }
    }

    override suspend fun deleteRemoteTask(id: String) {
        try {
            logger.logI("Deleting remote task: $id")
            api.deleteTask(id)
            logger.logD("Remote delete success: $id")
        } catch (e: Exception) {
            logger.logE("deleteRemoteTask failed: ${e.message}")
            throw e
        }
    }

    override suspend fun createRemoteTask(title: String, body: String): String {
        try {
            logger.logI("Creating remote task: $title")

            val response = api.createTask(
                CreateTaskRequest(title, body)
            )

            logger.logD("Remote task created with id: ${response.id}")

            return response.id

        } catch (e: Exception) {
            logger.logE("createRemoteTask failed: ${e.message}")
            throw e
        }
    }

    override suspend fun updateRemoteTask(task: TaskEntity) {
        try {
            logger.logI("Updating remote task: ${task.id}")

            api.updateTask(
                task.id,
                PutTaskRequest(task.title, task.body)
            )

            logger.logD("Remote update success: ${task.id}")

        } catch (e: Exception) {
            logger.logE("updateRemoteTask failed: ${e.message}")
            throw e
        }
    }
}
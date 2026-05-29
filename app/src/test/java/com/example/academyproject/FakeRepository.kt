import com.example.academyproject.data.local.TaskEntity
import com.example.academyproject.data.repository.TaskRepositoryContract
import kotlinx.coroutines.flow.flowOf

class FakeTaskRepository : TaskRepositoryContract {

    val tasks = mutableListOf<TaskEntity>()

    override fun getTasks() = flowOf(tasks)

    override suspend fun insertLocal(task: TaskEntity) {
        tasks.add(task)
    }

    override suspend fun deleteLocal(id: String) {
        tasks.removeIf { it.id == id }
    }

    override suspend fun updateLocal(task: TaskEntity) {
        deleteLocal(task.id)
        tasks.add(task)
    }

    override suspend fun syncFromRemote() {}

    override suspend fun deleteRemoteTask(id: String) {}

    override suspend fun createRemoteTask(title: String, body: String): String {
        return "123"
    }

    override suspend fun updateRemoteTask(task: TaskEntity) {}
}
import com.example.academyproject.data.local.TaskDao
import com.example.academyproject.data.local.TaskEntity

class FakeTaskDao : TaskDao {

    val tasks = mutableListOf<TaskEntity>()

    override fun getTasks() = kotlinx.coroutines.flow.flow {
        emit(tasks)
    }

    override suspend fun insert(task: TaskEntity) {
        tasks.add(task)
    }

    override suspend fun insertAll(tasks: List<TaskEntity>) {
        this.tasks.addAll(tasks)
    }

    override suspend fun deleteById(id: String) {
        tasks.removeIf { it.id == id }
    }

    override suspend fun update(task: TaskEntity) {
        tasks.replaceAll { if (it.id == task.id) task else it }
    }

    override suspend fun clearAll() {
        tasks.clear()
    }
}
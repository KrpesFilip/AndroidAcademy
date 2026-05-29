import com.example.academyproject.data.local.TaskEntity
import kotlinx.coroutines.test.runTest
import org.junit.*

class TaskRepositoryTest {

    private lateinit var dao: FakeTaskDao
    private lateinit var api: FakeApiService
    private lateinit var repo: TaskRepository

    @Before
    fun setup() {
        dao = FakeTaskDao()
        api = FakeApiService()
        repo = TaskRepository(dao, api, FakeLogger())
    }

    @Test
    fun insertLocal_addsTaskToDao() = runTest {
        val task = TaskEntity("1", "A", "B", false)

        repo.insertLocal(task)

        assert(dao.tasks.contains(task))
    }

    @Test
    fun deleteLocal_removesTask() = runTest {
        val task = TaskEntity("1", "A", "B", false)
        dao.tasks.add(task)

        repo.deleteLocal("1")

        assert(dao.tasks.isEmpty())
    }

    @Test
    fun syncFromRemote_clearsAndInserts() = runTest {
        dao.tasks.add(TaskEntity("old", "old", "old", false))

        repo.syncFromRemote()

        assert(dao.tasks.size == 1)
        assert(dao.tasks[0].id == "1")
    }
}
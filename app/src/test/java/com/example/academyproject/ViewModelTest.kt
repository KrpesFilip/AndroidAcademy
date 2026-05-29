import android.app.Application
import androidx.test.core.app.ApplicationProvider
import com.example.academyproject.TaskViewModel
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class TaskViewModelTest {

    private lateinit var viewModel: TaskViewModel
    private lateinit var repo: FakeTaskRepository

    @Before
    fun setup() {
        repo = FakeTaskRepository()

        val application = ApplicationProvider.getApplicationContext<Application>()

        viewModel = TaskViewModel(application, repo)
    }

    @Test
    fun createTask_setsSyncingFalse() = runTest {
        viewModel.createTask("Title", "Body")

        assertFalse(viewModel.isSyncing)
    }

    @Test
    fun deleteTask_doesNotCrash() = runTest {
        viewModel.deleteTask("1")

        assertFalse(viewModel.isSyncing)
    }
}
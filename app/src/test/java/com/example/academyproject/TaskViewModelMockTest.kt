import com.example.academyproject.TaskViewModel
import org.junit.Test
import org.mockito.Mockito.*
import org.mockito.kotlin.verify

class TaskViewModelMockTest {

    private val viewModel = mock(TaskViewModel::class.java)

    @Test
    fun `verify createTask is called`() {

        // call method
        viewModel.createTask("Title", "Body")

        // verify interaction
        verify(viewModel).createTask("Title", "Body")
    }
}
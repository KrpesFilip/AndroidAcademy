import com.example.academyproject.model.*
import com.example.academyproject.network.ApiService

class FakeApiService : ApiService {

    var shouldFail = false

    override suspend fun login(request: LoginRequest): LoginResponse {
        return LoginResponse("fake-token")
    }

    override suspend fun getTasks(): TaskResponse {
        return TaskResponse(
            tasks = listOf(
                Task(
                    id = "1",
                    title = "Title",
                    body = "Body"
                )
            )
        )
    }

    override suspend fun getTask(id: String): Task {
        return Task(
            id = id,
            title = "Title",
            body = "Body"
        )
    }

    override suspend fun createTask(request: CreateTaskRequest): CreateTaskResponse {
        return CreateTaskResponse("123")
    }

    override suspend fun updateTask(id: String, request: PutTaskRequest) {

    }

    override suspend fun deleteTask(id: String) {

    }
}
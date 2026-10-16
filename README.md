# Taskie

**Android task manager with offline-first local storage and backend sync**

An Android app for creating, editing and deleting tasks. Tasks are saved in a local Room database, so changes appear straight away, and are then synced with a remote REST backend. The app is built with Jetpack Compose and the MVVM pattern, and has unit tests for the ViewModel and repository.

> Made for the Android Academy 2026 course. Author: Filip Krpes.

## Features

- Automatic login to the backend at startup; a Bearer token is attached to every request
- Task list with create, edit and delete
- Local-first changes: each change is saved to Room first, then sent to the API
- Sync tracking with an `isSynced` flag on every task
- Full sync from the server at app start, with a progress indicator
- Two-screen navigation (task list and task editor)
- Logging through a `Logger` interface, with a fake logger used in tests
- Unit tests for the ViewModel and repository using fakes and Mockito

## Tech stack

| Area | Technology |
|---|---|
| Language | Kotlin |
| UI | Jetpack Compose, Material 3 |
| Navigation | Navigation Compose |
| Architecture | MVVM, Repository pattern, manual dependency injection |
| Local database | Room (KSP) |
| Networking | Retrofit, Gson, OkHttp (logging and auth interceptors) |
| Concurrency | Kotlin Coroutines, Flow |
| Testing | JUnit 4, kotlinx-coroutines-test, Mockito / mockito-kotlin |
| Min / target SDK | 30 / 36 |

## Architecture

```
Compose UI (notes screen, edit screen)
     │  observes state
     ▼
TaskViewModel
     │
     ▼
TaskRepository ──── Room database   (local tasks, isSynced flag)
     │
     └──── Retrofit ApiService      (Taskie REST backend)
              AuthInterceptor adds the Bearer token
```

## Project structure

```
app/src/main/java/com/example/academyproject/
├── MainActivity.kt          # Entry point, navigation, screens, TaskViewModel
├── data/
│   ├── local/               # AppDatabase, DatabaseProvider, TaskDao, TaskEntity
│   └── repository/          # TaskRepository, TaskRepositoryContract
├── model/                   # API request and response models
├── network/                 # ApiService, RetrofitInstance, AuthInterceptor, SessionManager
├── ui/theme/                # Compose theme
└── util/                    # Logger, AppLogger

app/src/test/java/com/example/academyproject/
├── TaskRepositoryTest.kt
├── ViewModelTest.kt
├── TaskViewModelMockTest.kt
└── Fake*.kt                 # FakeApi, FakeTaskDao, FakeRepository, FakeLogger
```

## API

The base URL is `https://ada-taskie-backend.osc-fr1.scalingo.io/`.

| Method | Endpoint | Purpose |
|---|---|---|
| POST | `/login` | Log in and get a token |
| GET | `/tasks/all` | Get all tasks |
| GET | `/tasks/{id}` | Get one task |
| POST | `/tasks/create` | Create a task |
| PUT | `/tasks/{id}` | Update a task |
| DELETE | `/tasks/{id}` | Delete a task |

## Getting started

1. Clone the repository and open it in Android Studio.
2. Let Gradle sync.
3. Run the `app` configuration on an emulator or device with API 30 or higher.

To run the unit tests:

```bash
./gradlew test
```

## Development history

The app was built step by step across the Android Academy 2026 course assignments:

| Assignment | Added |
|---|---|
| 4 | Basic Compose UI |
| 5 | Networking with the Taskie backend (Retrofit) |
| 7 | Room database and API sync |
| 8 | Dependency injection, logger in the ViewModel and repository |
| 9 | Unit tests for the ViewModel and repository |

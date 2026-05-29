package com.example.academyproject.data.repository

import com.example.academyproject.data.local.TaskEntity
import kotlinx.coroutines.flow.Flow

interface TaskRepositoryContract {
    fun getTasks(): Flow<List<TaskEntity>>

    suspend fun insertLocal(task: TaskEntity)

    suspend fun deleteLocal(id: String)

    suspend fun updateLocal(task: TaskEntity)

    suspend fun syncFromRemote()

    suspend fun deleteRemoteTask(id: String)

    suspend fun createRemoteTask(title: String, body: String): String

    suspend fun updateRemoteTask(task: TaskEntity)
}
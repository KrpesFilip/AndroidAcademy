package com.example.academyproject.network

import PutTaskRequest
import com.example.academyproject.model.*

import retrofit2.http.*

interface ApiService {

    @POST("login")
    suspend fun login(
        @Body request: LoginRequest
    ): LoginResponse


    @GET("tasks/all")
    suspend fun getTasks(): TaskResponse

    @GET("tasks/{id}")
    suspend fun getTask(
        @Path("id") id: String
    ): Task

    @POST("tasks/create")
    suspend fun createTask(
        @Body request: CreateTaskRequest
    ): CreateTaskResponse

    @PUT("tasks/{id}")
    suspend fun updateTask(
        @Path("id") id: String,
        @Body request: PutTaskRequest
    )

    @DELETE("tasks/{id}")
    suspend fun deleteTask(
        @Path("id") id: String
    )
}



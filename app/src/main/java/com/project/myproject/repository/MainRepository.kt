package com.project.myproject.repository

import com.project.myproject.network.retrofit.RetrofitService
import com.project.myproject.network.retrofit.models.CreateRequest
import com.project.myproject.network.retrofit.models.LoginRequest
import com.project.myproject.network.retrofit.response.AuthorizationResponse
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MainRepository @Inject constructor(private val retrofitService: RetrofitService) {

    suspend fun createUser(createRequest: CreateRequest): Response<AuthorizationResponse> {
        return retrofitService.createUser(createRequest)
    }

    suspend fun loginUser(loginRequest: LoginRequest): Response<AuthorizationResponse> {
        return retrofitService.loginUser(loginRequest)
    }
}


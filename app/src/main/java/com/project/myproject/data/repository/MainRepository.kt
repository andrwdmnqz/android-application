package com.project.myproject.data.repository

import com.project.myproject.data.network.RetrofitService
import com.project.myproject.data.requests.CreateRequest
import com.project.myproject.data.requests.EditUserRequest
import com.project.myproject.data.requests.LoginRequest
import com.project.myproject.data.responces.AuthorizationResponse
import com.project.myproject.data.responces.GetUserResponse
import com.project.myproject.data.responces.TokenResponse
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

    suspend fun refreshTokens(refreshToken: String): Response <TokenResponse> {
        return retrofitService.refreshTokens(refreshToken)
    }

    suspend fun getUser(userId: Int, accessToken: String): Response<GetUserResponse> {
        return retrofitService.getUser(userId, accessToken)
    }

    suspend fun editUser(userId: Int, accessToken: String, request: EditUserRequest):
            Response <GetUserResponse> {

        return retrofitService.editUser(userId, accessToken, "application/json", request)
    }
}

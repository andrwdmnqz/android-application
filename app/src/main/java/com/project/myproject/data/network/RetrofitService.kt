package com.project.myproject.data.network

import com.project.myproject.data.requests.CreateRequest
import com.project.myproject.data.requests.EditUserRequest
import com.project.myproject.data.requests.LoginRequest
import com.project.myproject.data.responces.AuthorizationResponse
import com.project.myproject.data.responces.GetUserResponse
import com.project.myproject.data.responces.TokenResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface RetrofitService {

    @POST("users")
    suspend fun createUser(@Body request: CreateRequest): Response<AuthorizationResponse>

    @POST("login")
    suspend fun loginUser(@Body request: LoginRequest): Response<AuthorizationResponse>

    @POST("refresh")
    suspend fun refreshTokens(@Header("Authorization") refreshToken: String): Response<TokenResponse>

    @PUT("users/{userId}")
    suspend fun editUser(
        @Path("userId") userId: Int,
        @Header("Authorization") accessToken: String,
        @Header("Content-type") contentType: String,
        @Body request: EditUserRequest
    ): Response<GetUserResponse>

    @GET("users/{userId}")
    suspend fun getUser(
        @Path("userId") userId: Int,
        @Header("Authorization") accessToken: String
    ): Response<GetUserResponse>
}
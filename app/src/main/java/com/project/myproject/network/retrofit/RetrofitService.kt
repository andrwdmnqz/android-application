package com.project.myproject.network.retrofit

import com.project.myproject.network.retrofit.models.CreateRequest
import com.project.myproject.network.retrofit.models.EditUserRequest
import com.project.myproject.network.retrofit.models.LoginRequest
import com.project.myproject.network.retrofit.response.AuthorizationResponse
import com.project.myproject.network.retrofit.response.GetUserResponse
import com.project.myproject.network.retrofit.response.TokenResponse
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
        @Body request: EditUserRequest
    ): Response<GetUserResponse>

    @GET("users/{userId}")
    suspend fun getUser(
        @Path("userId") userId: Int,
        @Header("Authorization") accessToken: String
    ): Response<GetUserResponse>
}
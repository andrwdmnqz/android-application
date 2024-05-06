package com.project.myproject.network.retrofit

import com.project.myproject.Constants
import com.project.myproject.network.retrofit.models.CreateRequest
import com.project.myproject.network.retrofit.models.LoginRequest
import com.project.myproject.network.retrofit.response.AuthorizationResponse
import com.project.myproject.network.retrofit.response.UserResponse
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface RetrofitService {

    @POST("users")
    suspend fun createUser(@Body request: CreateRequest): Response<AuthorizationResponse>

    @POST("login")
    suspend fun loginUser(@Body request: LoginRequest): Response<AuthorizationResponse>

    @GET("users/{userId}")
    suspend fun getUser(
        @Path("userId") userId: Int,
        @Header("Authorization") accessToken: String
    ): Response<UserResponse>

    companion object {
        private var retrofitService: RetrofitService? = null

        fun getInstance(): RetrofitService {
            if (retrofitService == null) {
                val retrofit = Retrofit.Builder()
                    .baseUrl(Constants.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()

                retrofitService = retrofit.create(RetrofitService::class.java)
            }
            return retrofitService!!
        }
    }
}
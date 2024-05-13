package com.project.myproject.data.network

import com.project.myproject.data.requests.AddContactRequest
import com.project.myproject.data.requests.CreateRequest
import com.project.myproject.data.requests.EditUserRequest
import com.project.myproject.data.requests.LoginRequest
import com.project.myproject.data.responses.AuthorizationResponse
import com.project.myproject.data.responses.AllUsersResponse
import com.project.myproject.data.responses.UserContactsResponse
import com.project.myproject.data.responses.GetUserResponse
import com.project.myproject.data.responses.TokenResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
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

    @PUT("users/{userId}/contacts")
    suspend fun addContact(
        @Path("userId") userId: Int,
        @Header("Authorization") accessToken: String,
        @Header("Content-type") contentType: String,
        @Body request: AddContactRequest
    ): Response<UserContactsResponse>

    @GET("users/{userId}")
    suspend fun getUser(
        @Path("userId") userId: Int,
        @Header("Authorization") accessToken: String
    ): Response<GetUserResponse>

    @GET("users/{userId}/contacts")
    suspend fun getUserContacts(
        @Path("userId") userId: Int,
        @Header("Authorization") accessToken: String,
        @Header("Content-type") contentType: String
    ): Response<UserContactsResponse>

    @GET("users")
    suspend fun getAllUsers(
        @Header("Authorization") accessToken: String
    ): Response<AllUsersResponse>

    @DELETE("users/{userId}/contacts/{contactId}")
    suspend fun deleteUserContact(
        @Path("userId") userId: Int,
        @Path("contactId") contactId: Int,
        @Header("Authorization") accessToken: String
    ): Response<UserContactsResponse>
}
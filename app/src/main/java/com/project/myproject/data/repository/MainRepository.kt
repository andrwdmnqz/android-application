package com.project.myproject.data.repository

import com.project.myproject.data.network.RetrofitService
import com.project.myproject.data.requests.AddContactRequest
import com.project.myproject.data.requests.CreateRequest
import com.project.myproject.data.requests.EditUserRequest
import com.project.myproject.data.requests.LoginRequest
import com.project.myproject.data.responses.AllUsersResponse
import com.project.myproject.data.responses.AuthorizationResponse
import com.project.myproject.data.responses.UserContactsResponse
import com.project.myproject.data.responses.GetUserResponse
import com.project.myproject.data.responses.TokenResponse
import com.project.myproject.utils.Constants
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

    suspend fun editUser(userId: Int, accessToken: String, request: EditUserRequest):
            Response <GetUserResponse> {

        return retrofitService.editUser(userId, accessToken, Constants.APPLICATION_JSON_TYPE, request)
    }

    suspend fun addContact(
        userId: Int,
        accessToken: String,
        request: AddContactRequest
    ): Response<UserContactsResponse> {
        return retrofitService.addContact(userId, accessToken, Constants.APPLICATION_JSON_TYPE, request)
    }

    suspend fun getUser(userId: Int, accessToken: String): Response<GetUserResponse> {
        return retrofitService.getUser(userId, accessToken)
    }

    suspend fun getUserContacts(userId: Int, accessToken: String): Response<UserContactsResponse> {
        return retrofitService.getUserContacts(userId, accessToken, Constants.APPLICATION_JSON_TYPE)
    }

    suspend fun getAllUsers(accessToken: String): Response<AllUsersResponse> {
        return retrofitService.getAllUsers(accessToken)
    }

    suspend fun deleteUserContact(
        userId: Int,
        contactId: Int,
        accessToken: String
    ): Response<UserContactsResponse> {
        return retrofitService.deleteUserContact(userId, contactId, accessToken)
    }
}

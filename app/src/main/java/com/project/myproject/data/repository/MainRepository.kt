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

    suspend fun refreshTokens(): Response <TokenResponse> {
        return retrofitService.refreshTokens()
    }

    suspend fun editUser(userId: Int, request: EditUserRequest):
            Response <GetUserResponse> {

        return retrofitService.editUser(userId, request)
    }

    suspend fun addContact(
        userId: Int,
        request: AddContactRequest
    ): Response<UserContactsResponse> {
        return retrofitService.addContact(userId, request)
    }

    suspend fun getUser(userId: Int): Response<GetUserResponse> {
        return retrofitService.getUser(userId)
    }

    suspend fun getUserContacts(userId: Int): Response<UserContactsResponse> {
        return retrofitService.getUserContacts(userId)
    }

    suspend fun getAllUsers(): Response<AllUsersResponse> {
        return retrofitService.getAllUsers()
    }

    suspend fun deleteUserContact(
        userId: Int,
        contactId: Int
    ): Response<UserContactsResponse> {
        return retrofitService.deleteUserContact(userId, contactId)
    }
}

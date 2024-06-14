package com.project.myproject.data.repository

import com.project.myproject.R
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
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MainRepository @Inject constructor(
    private val retrofitService: RetrofitService
) {

    suspend fun createUser(createRequest: CreateRequest): AuthorizationResponse? {
        val response = retrofitService.createUser(createRequest)
        return if (response.isSuccessful) {
            response.body()
        } else {
            null
        }
    }

    suspend fun loginUser(loginRequest: LoginRequest): AuthorizationResponse? {
        val response = retrofitService.loginUser(loginRequest)
        return if (response.isSuccessful) {
            response.body()
        } else {
            null
        }
    }

    suspend fun refreshTokens(): TokenResponse? {
        val response = retrofitService.refreshTokens()
        return if (response.isSuccessful) {
            response.body()
        } else {
            null
        }
    }

    suspend fun editUser(userId: Int, request: EditUserRequest): GetUserResponse? {
        val response = retrofitService.editUser(userId, request)
        return if (response.isSuccessful) {
            response.body()
        } else {
            null
        }
    }

    suspend fun addContact(
        userId: Int,
        request: AddContactRequest
    ): UserContactsResponse? {
        val response = retrofitService.addContact(userId, request)
        return if (response.isSuccessful) {
            response.body()
        } else {
            null
        }
    }

    suspend fun getUser(userId: Int): GetUserResponse? {
        val response = retrofitService.getUser(userId)
        return if (response.isSuccessful) {
            response.body()
        } else {
            null
        }
    }

    suspend fun getUserContacts(userId: Int): UserContactsResponse? {
        val response = retrofitService.getUserContacts(userId)
        return if (response.isSuccessful) {
            response.body()
        } else {
            null
        }
    }

    suspend fun getAllUsers(): AllUsersResponse? {
        val response = retrofitService.getAllUsers()
        return if (response.isSuccessful) {
            response.body()
        } else {
            null
        }
    }

    suspend fun deleteUserContact(
        userId: Int,
        contactId: Int
    ): UserContactsResponse? {
        val response = retrofitService.deleteUserContact(userId, contactId)
        return if (response.isSuccessful) {
            response.body()
        } else {
            null
        }
    }
}

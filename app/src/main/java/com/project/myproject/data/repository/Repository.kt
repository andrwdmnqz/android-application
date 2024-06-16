package com.project.myproject.data.repository

import com.project.myproject.data.requests.AddContactRequest
import com.project.myproject.data.requests.CreateRequest
import com.project.myproject.data.requests.EditUserRequest
import com.project.myproject.data.requests.LoginRequest
import com.project.myproject.data.responses.AllUsersResponse
import com.project.myproject.data.responses.AuthorizationResponse
import com.project.myproject.data.responses.GetUserResponse
import com.project.myproject.data.responses.TokenResponse
import com.project.myproject.data.responses.UserContactsResponse

interface Repository {

    suspend fun createUser(createRequest: CreateRequest): AuthorizationResponse?

    suspend fun loginUser(loginRequest: LoginRequest): AuthorizationResponse?

    suspend fun refreshTokens(): TokenResponse?

    suspend fun editUser(userId: Int, request: EditUserRequest): GetUserResponse?

    suspend fun addContact(
        userId: Int,
        request: AddContactRequest
    ): UserContactsResponse?

    suspend fun getUser(userId: Int): GetUserResponse?

    suspend fun getUserContacts(userId: Int): UserContactsResponse?

    suspend fun getAllUsers(): AllUsersResponse?

    suspend fun deleteUserContact(
        userId: Int,
        contactId: Int
    ): UserContactsResponse?
}
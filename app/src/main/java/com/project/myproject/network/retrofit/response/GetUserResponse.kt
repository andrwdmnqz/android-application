package com.project.myproject.network.retrofit.response

data class GetUserResponse (
    val status: String,
    val code: Int,
    val message: String,
    val data: UserData
)

data class UserData (
    val user: UserResponse
)
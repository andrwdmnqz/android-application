package com.project.myproject.network.retrofit.response

data class CreateResponse(
    val status: String,
    val code: Int,
    val message: String,
    val data: Data
)

data class Data(
    val user: UserResponse,
    val accessToken: String,
    val refreshToken: String
)
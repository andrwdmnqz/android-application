package com.project.myproject.network.retrofit.response

data class TokenResponse (
    val status: String,
    val code: Int,
    val message: String,
    val data: TokenData
)

data class TokenData (
    val accessToken: String,
    val refreshToken: String
)
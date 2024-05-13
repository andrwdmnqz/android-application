package com.project.myproject.data.models

data class UserWithTokensData(
    val user: User,
    val accessToken: String,
    val refreshToken: String
)
package com.project.myproject.data.models

import com.project.myproject.data.responces.UserResponse

data class UserWithTokensData(
    val user: UserResponse,
    val accessToken: String,
    val refreshToken: String
)
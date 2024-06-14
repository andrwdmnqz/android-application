package com.project.myproject.data.models

class UserWithTokensData(
    val user: User,
    val accessToken: String,
    val refreshToken: String
)
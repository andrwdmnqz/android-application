package com.project.myproject.data.responses

import com.project.myproject.data.room.entities.User

class UserWithTokensData(
    val user: User,
    val accessToken: String,
    val refreshToken: String
)
package com.project.myproject.data.responses

import com.project.myproject.data.models.UserWithTokensData

class AuthorizationResponse(
    val status: String,
    val code: Int,
    val message: String,
    val data: UserWithTokensData
)

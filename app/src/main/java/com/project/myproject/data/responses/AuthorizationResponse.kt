package com.project.myproject.data.responses

class AuthorizationResponse(
    val status: String,
    val code: Int,
    val message: String,
    val data: UserWithTokensData
)

package com.project.myproject.data.responces

import com.project.myproject.data.models.UserWithTokensData

data class AuthorizationResponse(
    val status: String,
    val code: Int,
    val message: String,
    val data: UserWithTokensData
)

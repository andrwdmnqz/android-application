package com.project.myproject.data.responses

import com.project.myproject.data.models.TokenData

class TokenResponse (
    val status: String,
    val code: Int,
    val message: String,
    val data: TokenData
)
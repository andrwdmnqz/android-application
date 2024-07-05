package com.project.myproject.data.responses

class TokenResponse (
    val status: String,
    val code: Int,
    val message: String,
    val data: TokenData
)
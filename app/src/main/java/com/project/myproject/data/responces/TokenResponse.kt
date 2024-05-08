package com.project.myproject.data.responces

import com.project.myproject.data.models.TokenData

data class TokenResponse (
    val status: String,
    val code: Int,
    val message: String,
    val data: TokenData
)
package com.project.myproject.data.responses

import com.project.myproject.data.models.UserData

data class GetUserResponse (
    val status: String,
    val code: Int,
    val message: String,
    val data: UserData
)
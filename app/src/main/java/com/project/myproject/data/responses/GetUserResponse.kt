package com.project.myproject.data.responses

class GetUserResponse (
    val status: String,
    val code: Int,
    val message: String,
    val data: UserData
)
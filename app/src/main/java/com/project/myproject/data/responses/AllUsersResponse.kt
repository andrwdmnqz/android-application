package com.project.myproject.data.responses

class AllUsersResponse(
    val status: String,
    val code: Int,
    val message: String,
    val data: UserListData
)
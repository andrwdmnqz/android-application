package com.project.myproject.data.responses

import com.project.myproject.data.models.UserListData

class AllUsersResponse(
    val status: String,
    val code: Int,
    val message: String,
    val data: UserListData
)
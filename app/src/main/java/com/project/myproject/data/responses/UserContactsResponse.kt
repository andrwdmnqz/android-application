package com.project.myproject.data.responses

class UserContactsResponse(
    val status: String,
    val code: Int,
    val message: String,
    val data: ContactsData
)
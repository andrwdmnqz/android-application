package com.project.myproject.data.responses

import com.project.myproject.data.models.ContactsData

class UserContactsResponse(
    val status: String,
    val code: Int,
    val message: String,
    val data: ContactsData
)
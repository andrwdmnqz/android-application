package com.project.myproject.data.requests

import java.util.Date

data class EditUserRequest (
    val name: String? = null,
    val phone: String? = null,
    val address: String? = null,
    val career: String? = null,
    val birthday: Date? = null,
    val facebook: String? = null,
    val instagram: String? = null,
    val twitter: String? = null,
    val linkedin: String? = null
)
package com.project.myproject.network.retrofit.response

import com.google.gson.annotations.SerializedName

data class UserResponse (
    val id: Int,
    val name: String?,
    val email: String,
    val phone: String?,
    val career: String?,
    val address: String?,
    val birthday: String?,
    val facebook: String?,
    val instagram: String?,
    val twitter: String?,
    val linkedin: String?,
    val image: String?,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("updated_at")
    val updatedAt: String
)
package com.project.myproject.data.responces

import com.google.gson.annotations.SerializedName

data class UserResponse (
    val id: Int,
    var name: String?,
    var email: String,
    var phone: String?,
    var career: String?,
    var address: String?,
    var birthday: String?,
    var facebook: String?,
    var instagram: String?,
    var twitter: String?,
    var linkedin: String?,
    var image: String?,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("updated_at")
    var updatedAt: String
)
package com.project.myproject.data.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.project.myproject.utils.FilterableItem
import kotlinx.parcelize.Parcelize

@Parcelize
class Contact(
    val id: Int,
    override var name: String?,
    var email: String,
    var phone: String?,
    override var career: String?,
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
    var updatedAt: String,
    var isSelected: Boolean = false
) : Parcelable, FilterableItem
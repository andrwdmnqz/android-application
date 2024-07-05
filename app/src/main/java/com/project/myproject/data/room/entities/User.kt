package com.project.myproject.data.room.entities

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import com.project.myproject.utils.FilterableItem
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "users")
class User(
    @PrimaryKey val id: Int,
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
    var updatedAt: String
) : Parcelable, FilterableItem {
    fun toContact(): Contact {
        return Contact(
            id = id,
            name = name,
            email = email,
            phone = phone,
            career = career,
            address = address,
            birthday = birthday,
            facebook = facebook,
            instagram = instagram,
            twitter = twitter,
            linkedin = linkedin,
            image = image,
            createdAt = createdAt,
            updatedAt = updatedAt,
            isSelected = false
        )
    }
}
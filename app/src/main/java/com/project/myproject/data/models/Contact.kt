package com.project.myproject.data.models

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class Contact(
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
    var updatedAt: String,
    var isSelected: Boolean = false
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString(),
        parcel.readString() ?: "",
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readByte() != 0.toByte()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(name)
        parcel.writeString(email)
        parcel.writeString(phone)
        parcel.writeString(career)
        parcel.writeString(address)
        parcel.writeString(birthday)
        parcel.writeString(facebook)
        parcel.writeString(instagram)
        parcel.writeString(twitter)
        parcel.writeString(linkedin)
        parcel.writeString(image)
        parcel.writeString(createdAt)
        parcel.writeString(updatedAt)
        parcel.writeByte(if (isSelected) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Contact> {
        override fun createFromParcel(parcel: Parcel): Contact {
            return Contact(parcel)
        }

        override fun newArray(size: Int): Array<Contact?> {
            return arrayOfNulls(size)
        }
    }
}
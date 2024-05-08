package com.project.myproject.data.models

import android.os.Parcel
import android.os.Parcelable

data class User(
    val id: Int,
    val photo: String,
    val name: String,
    val career: String,
    val address: String,
    var isSelected: Boolean = false
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readByte() != 0.toByte()
    )

    companion object {
        private var idCount = 0

        fun generateId(): Int {
            return ++idCount
        }

        @JvmField
        val CREATOR = object : Parcelable.Creator<User> {
            override fun createFromParcel(parcel: Parcel): User {
                return User(parcel)
            }

            override fun newArray(size: Int): Array<User?> {
                return arrayOfNulls(size)
            }
        }
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(photo)
        parcel.writeString(name)
        parcel.writeString(career)
        parcel.writeString(address)
        parcel.writeByte(if (isSelected) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }
}

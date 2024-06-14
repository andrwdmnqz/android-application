package com.project.myproject.data.mappers

import com.project.myproject.data.models.Contact
import com.project.myproject.data.models.User

object UserToContactMapper {
    fun map(user: User): Contact {
        return Contact(
            id = user.id,
            name = user.name,
            email = user.email,
            phone = user.phone,
            career = user.career,
            address = user.address,
            birthday = user.birthday,
            facebook = user.facebook,
            instagram = user.instagram,
            twitter = user.twitter,
            linkedin = user.linkedin,
            image = user.image,
            createdAt = user.createdAt,
            updatedAt = user.updatedAt,
            isSelected = false
        )
    }

    fun map(users: List<User>): List<Contact> {
        return users.map { map(it) }
    }
}
package com.project.myproject.data.repository

import com.project.myproject.data.datasource.UserDataStorage
import com.project.myproject.data.models.User

class UserRepository {
    private var userDataStorage = UserDataStorage()
    suspend fun addUsersToList() : List<User> = userDataStorage.addUserToList()

    suspend fun deleteUser(id: Int) {
        userDataStorage.deleteUser(id)
    }

    suspend fun addUser(position: Int, user: User) {
        userDataStorage.addUser(position, user)
    }
}
package com.project.myproject.data.repository

import com.project.myproject.data.dao.UserDao
import com.project.myproject.data.models.User
import javax.inject.Inject

class UserRepository @Inject constructor(private val userDao: UserDao) {

    suspend fun getAllUsers(): List<User> = userDao.getAllUsers()

    suspend fun replaceUsers(users: List<User>) {
        userDao.deleteAllUsers()
        userDao.saveUsers(users)
    }

    suspend fun deleteAllUsers() = userDao.deleteAllUsers()

    suspend fun getUser(id: Int) = userDao.getUser(id)
}
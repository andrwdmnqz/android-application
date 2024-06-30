package com.project.myproject.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.project.myproject.data.dao.ContactDao
import com.project.myproject.data.dao.UserDao
import com.project.myproject.data.models.Contact
import com.project.myproject.data.models.User

@Database(entities = [User::class, Contact::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun contactDao(): ContactDao
}
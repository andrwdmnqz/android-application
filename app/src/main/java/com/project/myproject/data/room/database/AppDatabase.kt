package com.project.myproject.data.room.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.project.myproject.data.room.entities.Contact
import com.project.myproject.data.room.entities.User
import com.project.myproject.data.room.dao.ContactDao
import com.project.myproject.data.room.dao.UserDao

@Database(entities = [User::class, Contact::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun contactDao(): ContactDao
}
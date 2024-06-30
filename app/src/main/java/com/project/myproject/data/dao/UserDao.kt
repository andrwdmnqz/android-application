package com.project.myproject.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.project.myproject.data.models.User

@Dao
interface UserDao {
    @Query("SELECT * FROM users")
    suspend fun getAllUsers(): List<User>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveUsers(users: List<User>)

    @Query("DELETE FROM users")
    suspend fun deleteAllUsers()

    @Query("SELECT * FROM users WHERE id = :id")
    suspend fun getUser(id: Int): User
}
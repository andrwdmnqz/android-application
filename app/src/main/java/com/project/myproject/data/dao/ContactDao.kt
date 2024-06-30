package com.project.myproject.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.project.myproject.data.models.Contact

@Dao
interface ContactDao {
    @Query("SELECT * FROM contacts")
    suspend fun getAllContacts(): List<Contact>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveContacts(contacts: List<Contact>)

    @Query("DELETE FROM contacts")
    suspend fun deleteAllContacts()
}
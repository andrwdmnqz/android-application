package com.project.myproject.data.repository

import com.project.myproject.data.dao.ContactDao
import com.project.myproject.data.models.Contact
import javax.inject.Inject

class ContactRepository @Inject constructor(private val contactDao: ContactDao) {

    suspend fun getAllContacts(): List<Contact> = contactDao.getAllContacts()

    suspend fun replaceContacts(contacts: List<Contact>) {
        contactDao.deleteAllContacts()
        contactDao.saveContacts(contacts)
    }

    suspend fun deleteAllContacts() = contactDao.deleteAllContacts()
}
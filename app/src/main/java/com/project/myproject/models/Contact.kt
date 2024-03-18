package com.project.myproject.models

class Contact(val name: String, val career: String) {

    companion object {
        private var lastContactId = 0
        fun createContactsList(numContacts: Int) : ArrayList<Contact> {
            val contacts = ArrayList<Contact>()
            for (i in 0..numContacts) {
                contacts.add(Contact("Contact ${i + 1}", "Career ${i + 1}"))
            }
            return contacts
        }
    }
}
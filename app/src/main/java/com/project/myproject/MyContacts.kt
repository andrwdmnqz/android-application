package com.project.myproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.project.myproject.adapters.ContactsAdapter
import com.project.myproject.decorators.ContactItemDecorator
import com.project.myproject.models.Contact

class MyContacts : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.my_contacts_activity)

        val contactsRV = findViewById<View>(R.id.rv_contacts) as RecyclerView
        val contacts = Contact.createContactsList(15)
        val adapter = ContactsAdapter(contacts)

        contactsRV.adapter = adapter
        contactsRV.addItemDecoration(ContactItemDecorator(20))
        contactsRV.layoutManager = LinearLayoutManager(this)
    }
}
package com.project.myproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.project.myproject.adapters.UserAdapter
import com.project.myproject.decorators.UserItemDecorator
import com.project.myproject.viewmodels.UserViewModel

class MyContacts : AppCompatActivity() {
    private lateinit var userViewModel: UserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.my_contacts_activity)

        val contactsRV = findViewById<View>(R.id.rv_contacts) as RecyclerView
        val adapter = UserAdapter(ArrayList())

        contactsRV.adapter = adapter
        contactsRV.addItemDecoration(UserItemDecorator(20))
        contactsRV.layoutManager = LinearLayoutManager(this)

        userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)
        userViewModel.init(15)
        userViewModel.users.observe(this) { users ->
            adapter.contactsList = users
            adapter.notifyDataSetChanged()
        }
    }
}
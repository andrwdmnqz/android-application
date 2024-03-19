package com.project.myproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.project.myproject.adapters.UserAdapter
import com.project.myproject.decorators.UserItemDecorator
import com.project.myproject.extensions.loadImageByGlide
import com.project.myproject.viewmodels.UserViewModel
import kotlinx.coroutines.launch

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
        userViewModel.init()

        lifecycleScope.launch {
            userViewModel.users.collect { users ->
                adapter.contactsList = users
                adapter.notifyDataSetChanged()
            }
        }
    }
}
package com.project.myproject.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.project.myproject.models.User

class UserViewModel() : ViewModel() {

    private val _users = MutableLiveData<ArrayList<User>>()
    val users: LiveData<ArrayList<User>>
        get() = _users

    fun init(contactsNumber: Int) {
        generateList(contactsNumber)
    }

    fun generateList(contactsNumber: Int) {
        val userList = User.createUserList(contactsNumber)
        _users.value = userList
    }
}
package com.project.myproject.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.project.myproject.models.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class UserViewModel() : ViewModel() {

    private val _users = MutableStateFlow<ArrayList<User>>(ArrayList())
    val users: StateFlow<ArrayList<User>> = _users

    fun init(contactsNumber: Int) {
        generateList(contactsNumber)
    }

    fun generateList(contactsNumber: Int) {
        val userList = User.createUserList(contactsNumber)
        _users.value = userList
    }
}
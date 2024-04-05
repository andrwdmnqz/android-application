package com.project.myproject.viewmodels

import androidx.lifecycle.ViewModel
import com.project.myproject.models.User
import com.project.myproject.repository.UserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class UserViewModel : ViewModel() {

    private val userRepository = UserRepository()

    private val _users = MutableStateFlow<List<User>>(emptyList())
    val users = _users.asStateFlow()

    fun init() {
        addUsersToList()
    }

    private fun addUsersToList() {
        CoroutineScope(Dispatchers.Main).launch {
            _users.value = userRepository.addUsersToList()
        }
    }

    fun deleteUser(user: User) {
        CoroutineScope(Dispatchers.Main).launch {
            userRepository.deleteUser(user)
            _users.value = _users.value.filter { it != user }
        }
    }

    fun addUser(position: Int, user: User) {
        CoroutineScope(Dispatchers.Main).launch {
            userRepository.addUser(position, user)

            val currentList = _users.value.toMutableList()
            currentList.add(position, user)

            _users.value = currentList
        }
    }
}
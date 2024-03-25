package com.project.myproject.viewmodels

import androidx.lifecycle.ViewModel
import com.project.myproject.models.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class UserViewModel() : ViewModel() {

    private val _users = MutableStateFlow<ArrayList<User>>(ArrayList())
    val users: StateFlow<ArrayList<User>> = _users

    fun init() {
        generateList()
    }

    private fun generateList() {
        val userList = arrayListOf(
            User("https://i.pinimg.com/564x/4b/cc/54/4bcc54ebe6d0e6700e3df3047c1129c8.jpg", "User 1", "Career 1"),
            User("https://i.pinimg.com/564x/5e/96/4b/5e964b4d1a6a514bf141c694f5037537.jpg", "User 2", "Career 2"),
            User("https://i.pinimg.com/564x/55/80/5e/55805e4aa3c42e91d39d1e1fd2013e60.jpg", "User 3", "Career 3"),
            User("https://i.pinimg.com/564x/e6/33/ee/e633eefbeb77cd4323a1557d33c91c83.jpg", "User 4", "Career 4"),
            User("https://i.pinimg.com/564x/66/43/f6/6643f6c4e45cb63fdf2e97e5f546027c.jpg", "User 5", "Career 5"),
            User("https://i.pinimg.com/564x/e7/61/d8/e761d858832d4368d0ee0cd91aa62cdf.jpg", "User 6", "Career 6"),
            User("https://i.pinimg.com/564x/55/a5/89/55a589c4c118141188f1396ebada5e9b.jpg", "User 7", "Career 7"),
            User("https://i.pinimg.com/564x/61/1a/4d/611a4dd854bd3894b0069a45a1aba33b.jpg", "User 8", "Career 8"),
            User("https://i.pinimg.com/564x/b3/f8/a9/b3f8a9ffb414e67cc998f6a5ae244dcd.jpg", "User 9", "Career 9"),
            User("https://i.pinimg.com/564x/a2/10/da/a210da10f17f0e15dbbe861016c135d1.jpg", "User 10", "Career 10")
        )
        _users.value = userList
    }
}
package com.project.myproject.models

class User(val name: String, val career: String) {

    companion object {
        fun createUserList(numContacts: Int) : ArrayList<User> {
            val users = ArrayList<User>()
            for (i in 0..numContacts) {
                users.add(User("User ${i + 1}", "Career ${i + 1}"))
            }
            return users
        }
    }
}
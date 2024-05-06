package com.project.myproject.models

data class User(val id: Int, val photo: String, val name: String, val career: String, val address: String) {

    companion object {
        private var idCount = 0

        fun generateId() : Int {
            return ++idCount
        }
    }
}
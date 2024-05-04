package com.project.myproject.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.project.myproject.repository.MainRepository
import java.lang.IllegalArgumentException

//class UserViewModelFactory constructor(private val repository: MainRepository,
//    private val registrationCallbacks: RegistrationCallbacks): ViewModelProvider.Factory {
//
//    override fun <T: ViewModel> create(modelClass: Class<T>): T {
//        return if (modelClass.isAssignableFrom(UserViewModel::class.java)) {
//            UserViewModel(this.repository, registrationCallbacks) as T
//        } else {
//            throw IllegalArgumentException("ViewModel not found!")
//        }
//    }
//}
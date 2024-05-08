package com.project.myproject.ui.viewmodels

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
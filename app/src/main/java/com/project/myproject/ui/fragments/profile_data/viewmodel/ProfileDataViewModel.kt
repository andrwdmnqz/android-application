package com.project.myproject.ui.fragments.profile_data.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.myproject.data.requests.EditUserRequest
import com.project.myproject.data.room.entities.User
import com.project.myproject.domain.Repository
import com.project.myproject.utils.NetworkUtil
import com.project.myproject.utils.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ProfileDataViewModel @Inject constructor(
    private val networkUtil: NetworkUtil,
    private val repository: Repository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _userEdited = MutableStateFlow(false)
    val userEdited = _userEdited.asStateFlow()

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser = _currentUser.asStateFlow()

    fun editUserNameAndPhone(userName: String, phoneNumber: String) {
        if (networkUtil.isInternetAvailable()) {
            viewModelScope.launch(Dispatchers.IO) {

                val result = repository.editUser(sessionManager.getId(),
                    EditUserRequest(userName, phoneNumber)
                )

                withContext(Dispatchers.Main) {
                    if (result != null) {
                        _currentUser.value = result.data.user
                        _userEdited.value = true
                    }
                }
            }
        }
    }

    fun resetUserEdited() {
        _userEdited.value = false
    }
}
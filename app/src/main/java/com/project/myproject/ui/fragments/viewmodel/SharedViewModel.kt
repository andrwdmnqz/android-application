package com.project.myproject.ui.fragments.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.myproject.data.room.entities.Contact
import com.project.myproject.data.room.entities.User
import com.project.myproject.ui.fragments.register.viewmodel.RegistrationState
import com.project.myproject.utils.SessionManager
import com.project.myproject.utils.SettingPreference
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SharedViewModel @Inject constructor(
    private val sessionManager: SessionManager,
    private val settingPreference: SettingPreference
) : ViewModel() {

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser = _currentUser.asStateFlow()

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState = _loginState.asStateFlow()

    private val _registrationState = MutableStateFlow<RegistrationState>(RegistrationState.Idle)
    val registrationState = _registrationState.asStateFlow()

    private val _contacts = MutableStateFlow<List<Contact>>(emptyList())
    val contacts = _contacts.asStateFlow()

    private val _contactsId = MutableStateFlow<List<Int>>(emptyList())
    val contactsId = _contactsId.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading = _loading.asStateFlow()

    fun setCurrentUser(user: User?) {
        _currentUser.value = user
    }

    fun setLoginState(state: LoginState) {
        _loginState.value = state
    }

    fun setRegistrationState(state: RegistrationState) {
        _registrationState.value = state
    }

    fun setContactsId(contactId: List<Int>) {
        Log.d("DEBUG_TAG", "setContactsId: ${contactsId.value}")
        _contactsId.value = contactId
    }

    fun setContacts(contacts: List<Contact>) {
        _contacts.value = contacts
    }

    fun setLoadingState(loading: Boolean) {
        _loading.value = loading
    }

    fun clearCurrentUser() {
        _currentUser.value = null
    }

    suspend fun logoutUser() {
        viewModelScope.launch {
            sessionManager.resetData()
            settingPreference.clearData()
            _currentUser.value = null
            _loginState.value = LoginState.Idle
            _registrationState.value = RegistrationState.Idle
        }
    }
}

sealed class LoginState {
    data object Idle : LoginState()
    data object Success : LoginState()
    data object InvalidLoginData : LoginState()
}
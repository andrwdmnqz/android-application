package com.project.myproject.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.myproject.data.mappers.UserToContactMapper
import com.project.myproject.data.models.Contact
import com.project.myproject.data.models.User
import com.project.myproject.data.repository.ContactRepository
import com.project.myproject.data.requests.CreateRequest
import com.project.myproject.data.requests.EditUserRequest
import com.project.myproject.data.requests.LoginRequest
import com.project.myproject.data.repository.Repository
import com.project.myproject.data.repository.UserRepository
import com.project.myproject.data.requests.AddContactRequest
import com.project.myproject.utils.SessionManager
import com.project.myproject.utils.SettingPreference
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val repository: Repository,
    private val contactRepository: ContactRepository,
    private val userRepository: UserRepository,
    private val sessionManager: SessionManager,
    private val settingPreference: SettingPreference
) : ViewModel() {

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState = _loginState.asStateFlow()

    private val _registrationState = MutableStateFlow<RegistrationState>(RegistrationState.Idle)
    val registrationState = _registrationState.asStateFlow()

    private val _contacts = MutableStateFlow<List<Contact>>(emptyList())
    val contacts = _contacts.asStateFlow()

    private val _contactsId = MutableStateFlow<List<Int>>(emptyList())
    val contactsId = _contactsId.asStateFlow()

    private val _users = MutableStateFlow<List<User>>(emptyList())
    val users = _users.asStateFlow()

    private val errorMessage = MutableStateFlow("")

    private val _loading = MutableStateFlow(false)
    val loading = _loading.asStateFlow()

    private val _contactAdded = MutableStateFlow(false)
    val contactAdded = _contactAdded.asStateFlow()

    private val _userEdited = MutableStateFlow(false)
    val userEdited = _userEdited.asStateFlow()

    private var currentUser: User? = null

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        onError("Exception handled: ${throwable.localizedMessage}")
    }

    fun registerUser(email: String, password: String, isUserRemembered: Boolean) {

        viewModelScope.launch(Dispatchers.IO + exceptionHandler) {

            val result = repository.createUser(CreateRequest(email, password))

            withContext(Dispatchers.Main) {
                if (result != null) {
                    val responseBodyData = result.data

                    handleUserResponse(responseBodyData.user, responseBodyData.accessToken, responseBodyData.refreshToken, isUserRemembered)

                    _registrationState.value = RegistrationState.Success
                } else {
                    _registrationState.value = RegistrationState.InvalidRegisterData
                }

                _registrationState.value = RegistrationState.Idle
            }
        }
    }

    fun loginUser(email: String, password: String, isUserRemembered: Boolean) {

        viewModelScope.launch(Dispatchers.IO + exceptionHandler) {

            val result = repository.loginUser(LoginRequest(email, password))

            withContext(Dispatchers.Main) {
                if (result != null) {
                    val responseBodyData = result.data

                    handleUserResponse(responseBodyData.user, responseBodyData.accessToken, responseBodyData.refreshToken, isUserRemembered)

                    _loginState.value = LoginState.Success
                } else {
                    _loginState.value = LoginState.InvalidLoginData
                }
            }
        }
    }

    suspend fun logoutUser() {
        viewModelScope.launch {
            sessionManager.resetData()
            settingPreference.clearData()
            currentUser = null
            _loginState.value = LoginState.Idle
            _registrationState.value = RegistrationState.Idle
        }
    }

    fun editUserNameAndPhone(userName: String, phoneNumber: String) {

        viewModelScope.launch(Dispatchers.IO + exceptionHandler) {

            val result = repository.editUser(sessionManager.getId(),
                EditUserRequest(userName, phoneNumber)
            )

            withContext(Dispatchers.Main) {
                if (result != null) {
                    currentUser = result.data.user
                    _userEdited.value = true
                }
            }
        }
    }

    fun resetUserEdited() {
        _userEdited.value = false
    }

    fun getUser() {

        viewModelScope.launch(Dispatchers.IO + exceptionHandler) {

            val accessToken = settingPreference.getAccessToken().firstOrNull()
            val refreshToken = settingPreference.getRefreshToken().firstOrNull()
            val userId = settingPreference.getUserId().firstOrNull()

            if (!accessToken.isNullOrBlank() && !refreshToken.isNullOrBlank() && userId != null && userId != -1) {
                sessionManager.setupData(
                    userId,
                    settingPreference.getAccessToken().first(),
                    settingPreference.getRefreshToken().first(),
                    true
                )

                val result = repository.getUser(userId)

                withContext(Dispatchers.Main) {
                    if (result != null) {
                        currentUser = result.data.user

                        _registrationState.value = RegistrationState.RememberedUser
                        _loginState.value = LoginState.Success
                    }
                }
            }
        }
    }

    fun fetchContacts() {

        viewModelScope.launch(Dispatchers.IO + exceptionHandler) {
            _loading.value = true

            val result = repository.getUserContacts(sessionManager.getId())

            withContext(Dispatchers.Main) {
                _loading.value = false
                if (result != null) {
                    _contacts.value = UserToContactMapper.map(result.data.contacts)
                    _contactsId.value = result.data.contacts.map { it.id }
                    contactRepository.replaceContacts(_contacts.value)
                }
            }
        }
    }

    fun fetchUsers() {

        viewModelScope.launch(Dispatchers.IO + exceptionHandler) {
            _loading.value = true

            val result = repository.getAllUsers()

            withContext(Dispatchers.Main) {
                _loading.value = false
                if (result != null) {
                    _users.value = result.data.users.filter { it.id != sessionManager.getId() }
                    userRepository.replaceUsers(_users.value)
                }
            }
        }
    }

    fun deleteContact(contactId: Int) {

        viewModelScope.launch(Dispatchers.IO + exceptionHandler) {

            val result = repository.deleteUserContact(sessionManager.getId(), contactId)

            withContext(Dispatchers.Main) {
                if (result != null) {
                    _contacts.value = UserToContactMapper.map(result.data.contacts)
                    _contactsId.value = result.data.contacts.map { it.id }
                }
            }
        }
    }

    fun addContact(contactId: Int) {

        viewModelScope.launch(Dispatchers.IO + exceptionHandler) {
            _loading.value = true

            val result = repository.addContact(sessionManager.getId(), AddContactRequest(contactId))

            withContext(Dispatchers.Main) {
                _loading.value = false
                if (result != null) {
                    _contacts.value = UserToContactMapper.map(result.data.contacts)
                    _contactsId.value = result.data.contacts.map { it.id }
                    _contactAdded.value = true
                }
            }
        }
    }

    fun resetContactAdded() {
        _contactAdded.value = false
    }

    private fun onError(message: String) {
        errorMessage.value = message
        _loading.value = false
    }

    fun getCurrentUser() = currentUser

    private suspend fun handleUserResponse(
        user: User,
        accessToken: String,
        refreshToken: String,
        isUserRemembered: Boolean
    ) {
        currentUser = user

        if (isUserRemembered) {
            settingPreference.setupData(user.id, accessToken, refreshToken)
        }
        sessionManager.setupData(user.id, accessToken, refreshToken, isUserRemembered)
    }
}

sealed class LoginState {
    data object Idle : LoginState()
    data object Success : LoginState()
    data object InvalidLoginData : LoginState()
}

sealed class RegistrationState {
    data object Idle : RegistrationState()
    data object Success : RegistrationState()
    data object InvalidRegisterData : RegistrationState()
    data object RememberedUser : RegistrationState()
}
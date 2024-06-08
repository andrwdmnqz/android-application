package com.project.myproject.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.myproject.data.mappers.UserToContactMapper
import com.project.myproject.data.models.Contact
import com.project.myproject.data.models.User
import com.project.myproject.data.requests.CreateRequest
import com.project.myproject.data.requests.EditUserRequest
import com.project.myproject.data.requests.LoginRequest
import com.project.myproject.data.repository.MainRepository
import com.project.myproject.data.requests.AddContactRequest
import com.project.myproject.utils.SessionManager
import com.project.myproject.utils.SettingPreference
import com.project.myproject.utils.callbacks.AddContactCallbacks
import com.project.myproject.utils.callbacks.EditCallbacks
import com.project.myproject.utils.callbacks.LoginCallbacks
import com.project.myproject.utils.callbacks.RegistrationCallbacks
import com.project.myproject.utils.callbacks.TokenCallbacks
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
    private val mainRepository: MainRepository,
    private val sessionManager: SessionManager,
    private val settingPreference: SettingPreference,
    private var registrationCallbacks: RegistrationCallbacks? = null,
    private var loginCallbacks: LoginCallbacks? = null,
    private var overallCallbacks: TokenCallbacks? = null,
    private var editCallbacks: EditCallbacks? = null,
    private var addContactCallbacks: AddContactCallbacks? = null
) : ViewModel() {

    private val _contacts = MutableStateFlow<List<Contact>>(emptyList())
    val contacts = _contacts.asStateFlow()

    private val _contactsId = MutableStateFlow<List<Int>>(emptyList())
    val contactsId = _contactsId.asStateFlow()

    private val _users = MutableStateFlow<List<User>>(emptyList())
    val users = _users.asStateFlow()

    private val errorMessage = MutableStateFlow("")
    private val _loading = MutableStateFlow(false)
    val loading = _loading.asStateFlow()

    private var currentUser: User? = null

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        onError("Exception handled: ${throwable.localizedMessage}")
    }

    fun registerUser(email: String, password: String, isUserRemembered: Boolean) {

        viewModelScope.launch(Dispatchers.IO + exceptionHandler) {

            val result = mainRepository.createUser(CreateRequest(email, password))

            withContext(Dispatchers.Main) {
                if (result != null) {
                    val responseBodyData = result.data

                    val id = responseBodyData.user.id
                    val accessToken = responseBodyData.accessToken
                    val refreshToken = responseBodyData.refreshToken

                    currentUser = responseBodyData.user

                    if (isUserRemembered) {
                        settingPreference.setupData(id, accessToken, refreshToken)
                    }
                    sessionManager.setupData(id, accessToken, refreshToken, isUserRemembered)

                    registrationCallbacks?.onSuccess(responseBodyData.accessToken, responseBodyData.refreshToken, currentUser!!.id)
                } else {
                    registrationCallbacks?.onEmailTakenError()
                }
            }
        }
    }

    fun loginUser(email: String, password: String, isUserRemembered: Boolean) {

        viewModelScope.launch(Dispatchers.IO + exceptionHandler) {

            val result = mainRepository.loginUser(LoginRequest(email, password))

            withContext(Dispatchers.Main) {
                if (result != null) {
                    val responseBodyData = result.data

                    val id = responseBodyData.user.id
                    val accessToken = responseBodyData.accessToken
                    val refreshToken = responseBodyData.refreshToken

                    currentUser = responseBodyData.user

                    if (isUserRemembered) {
                        settingPreference.setupData(id, accessToken, refreshToken)
                    }
                    sessionManager.setupData(id, accessToken, refreshToken, isUserRemembered)

                    currentUser = responseBodyData.user
                    loginCallbacks?.onSuccess()
                } else {
                    loginCallbacks?.onInvalidLoginData()
                }
            }
        }
    }

    fun editUserNameAndPhone(userId: Int, userName: String, phoneNumber: String) {

        viewModelScope.launch(Dispatchers.IO + exceptionHandler) {

            val result = mainRepository.editUser(userId,
                EditUserRequest(userName, phoneNumber)
            )

            withContext(Dispatchers.Main) {
                if (result != null) {
                    currentUser = result.data.user
                    editCallbacks?.onUserEdited()
                }
            }
        }
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

                val result = mainRepository.getUser(userId)

                withContext(Dispatchers.Main) {
                    if (result != null) {
                        currentUser = result.data.user

                        registrationCallbacks?.onUserIsRemembered()
                    }
                }
            }
        }
    }

    fun fetchContacts(userId: Int) {

        viewModelScope.launch(Dispatchers.IO + exceptionHandler) {
            _loading.value = true

            val result = mainRepository.getUserContacts(userId)

            withContext(Dispatchers.Main) {
                _loading.value = false
                if (result != null) {
                    _contacts.value = UserToContactMapper.map(result.data.contacts)
                    _contactsId.value = result.data.contacts.map { it.id }
                }
            }
        }
    }

    fun fetchUsers() {

        viewModelScope.launch(Dispatchers.IO + exceptionHandler) {
            _loading.value = true

            val result = mainRepository.getAllUsers()

            withContext(Dispatchers.Main) {
                _loading.value = false
                if (result != null) {
                    _users.value = result.data.users
                }
            }
        }
    }

    fun deleteContact(userId: Int, contactId: Int) {

        viewModelScope.launch(Dispatchers.IO + exceptionHandler) {

            val result = mainRepository.deleteUserContact(userId, contactId)

            withContext(Dispatchers.Main) {
                if (result != null) {
                    _contacts.value = UserToContactMapper.map(result.data.contacts)
                    _contactsId.value = result.data.contacts.map { it.id }
                }
            }
        }
    }

    fun addContact(userId: Int, contactId: Int) {

        viewModelScope.launch(Dispatchers.IO + exceptionHandler) {
            _loading.value = true

            val result = mainRepository.addContact(userId, AddContactRequest(contactId))

            withContext(Dispatchers.Main) {
                _loading.value = false
                if (result != null) {
                    _contacts.value = UserToContactMapper.map(result.data.contacts)
                    _contactsId.value = result.data.contacts.map { it.id }
                    addContactCallbacks?.onContactAdded()
                }
            }
        }
    }

    private fun onError(message: String) {
        errorMessage.value = message
        _loading.value = false
    }

    fun getCurrentUser() = currentUser

    fun setRegistrationCallbacks(registrationCallbacks: RegistrationCallbacks) {
        this.registrationCallbacks = registrationCallbacks
    }

    fun setLoginCallbacks(loginCallbacks: LoginCallbacks) {
        this.loginCallbacks = loginCallbacks
    }

    fun setOverallCallbacks(overallCallbacks: TokenCallbacks?) {
        this.overallCallbacks = overallCallbacks
    }

    fun setEditCallbacks(editCallbacks: EditCallbacks?) {
        this.editCallbacks = editCallbacks
    }

    fun setAddContactsCallbacks(addContactCallbacks: AddContactCallbacks?) {
        this.addContactCallbacks = addContactCallbacks
    }
}
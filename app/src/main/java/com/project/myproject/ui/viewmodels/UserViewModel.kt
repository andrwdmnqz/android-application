package com.project.myproject.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.project.myproject.data.models.Contact
import com.project.myproject.utils.Constants
import com.project.myproject.data.models.User
import com.project.myproject.data.requests.CreateRequest
import com.project.myproject.data.requests.EditUserRequest
import com.project.myproject.data.requests.LoginRequest
import com.project.myproject.data.repository.MainRepository
import com.project.myproject.data.requests.AddContactRequest
import com.project.myproject.utils.callbacks.AddContactCallbacks
import com.project.myproject.utils.callbacks.EditCallbacks
import com.project.myproject.utils.callbacks.LoginCallbacks
import com.project.myproject.utils.callbacks.RegistrationCallbacks
import com.project.myproject.utils.callbacks.TokenCallbacks
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val mainRepository: MainRepository,
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

    private var job: Job? = null
    private val errorMessage = MutableStateFlow("")
    private val _loading = MutableStateFlow(false)
    val loading = _loading.asStateFlow()

    private var currentUser: User? = null

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        onError("Exception handled: ${throwable.localizedMessage}")
    }

    fun registerUser(email: String, password: String) {

        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {

            val response = mainRepository.createUser(CreateRequest(email, password))

            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    val responseBodyData = response.body()!!.data

                    currentUser = responseBodyData.user
                    registrationCallbacks?.onSuccess(responseBodyData.accessToken, responseBodyData.refreshToken, currentUser!!.id)
                } else {
                    registrationCallbacks?.onEmailTakenError()
                }
            }
        }
    }

    fun loginUser(email: String, password: String) {

        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {

            val response = mainRepository.loginUser(LoginRequest(email, password))

            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    val responseBodyData = response.body()!!.data

                    currentUser = responseBodyData.user
                    loginCallbacks?.onSuccess(
                        responseBodyData.accessToken,
                        responseBodyData.refreshToken,
                        currentUser!!.id
                    )
                } else {
                    loginCallbacks?.onInvalidLoginData()
                }
            }
        }
    }

    fun editUserNameAndPhone(userId: Int, accessToken: String, userName: String, phoneNumber: String) {

        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {

            val response = mainRepository.editUser(userId,
                Constants.BEARER_TOKEN_START + accessToken,
                EditUserRequest(userName, phoneNumber)
            )

            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    currentUser = response.body()?.data?.user
                    editCallbacks?.onUserEdited()
                }
            }
        }
    }

    fun getUser(userId: Int, accessToken: String) {

        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {

            val response = mainRepository.getUser(userId, Constants.BEARER_TOKEN_START + accessToken)

            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    currentUser = response.body()?.data?.user
                    registrationCallbacks?.onUserIsRemembered()
                }
            }
        }
    }

    fun fetchContacts(userId: Int, accessToken: String) {

        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            _loading.value = true

            val response = mainRepository.getUserContacts(userId, Constants.BEARER_TOKEN_START + accessToken)

            withContext(Dispatchers.Main) {
                _loading.value = false
                if (response.isSuccessful) {
                    _contacts.value = convertUsersToContacts(response.body()?.data?.contacts!!)
                    _contactsId.value = response.body()!!.data.contacts.map { it.id }
                }
            }
        }
    }

    fun fetchUsers(accessToken: String) {

        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            _loading.value = true

            val response = mainRepository.getAllUsers(Constants.BEARER_TOKEN_START + accessToken)

            withContext(Dispatchers.Main) {
                _loading.value = false
                if (response.isSuccessful) {
                    _users.value = response.body()?.data?.users!!
                }
            }
        }
    }

    fun deleteContact(userId: Int, contactId: Int, accessToken: String) {

        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {

            val response = mainRepository.deleteUserContact(userId, contactId, Constants.BEARER_TOKEN_START + accessToken)

            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    _contacts.value = convertUsersToContacts(response.body()?.data?.contacts!!)
                    _contactsId.value = response.body()!!.data.contacts.map { it.id }
                }
            }
        }
    }

    fun addContact(userId: Int, contactId: Int, accessToken: String) {

        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            _loading.value = true

            val response = mainRepository.addContact(userId, Constants.BEARER_TOKEN_START + accessToken, AddContactRequest(contactId))

            withContext(Dispatchers.Main) {
                _loading.value = false
                if (response.isSuccessful) {
                    _contacts.value = convertUsersToContacts(response.body()?.data?.contacts!!)
                    _contactsId.value = response.body()!!.data.contacts.map { it.id }
                    addContactCallbacks?.onContactAdded()
                }
            }
        }
    }

    private fun convertUsersToContacts(users: List<User>): List<Contact> {
        return users.map { user ->
            Contact(
                id = user.id,
                name = user.name,
                email = user.email,
                phone = user.phone,
                career = user.career,
                address = user.address,
                birthday = user.birthday,
                facebook = user.facebook,
                instagram = user.instagram,
                twitter = user.twitter,
                linkedin = user.linkedin,
                image = user.image,
                createdAt = user.createdAt,
                updatedAt = user.updatedAt,
                isSelected = false
            )
        }
    }

    private fun onError(message: String) {
        errorMessage.value = message
        _loading.value = false
    }

    override fun onCleared() {
        super.onCleared()
        job?.cancel()
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
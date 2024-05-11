package com.project.myproject.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import com.project.myproject.utils.Constants
import com.project.myproject.data.models.User
import com.project.myproject.data.requests.CreateRequest
import com.project.myproject.data.requests.EditUserRequest
import com.project.myproject.data.requests.LoginRequest
import com.project.myproject.data.responces.UserResponse
import com.project.myproject.data.repository.MainRepository
import com.project.myproject.data.repository.UserRepository
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
class UserViewModel @Inject constructor(private val mainRepository: MainRepository,
                                        private var registrationCallbacks: RegistrationCallbacks? = null,
                                        private var loginCallbacks: LoginCallbacks? = null,
                                        private var overallCallbacks: TokenCallbacks? = null,
                                        private var editCallbacks: EditCallbacks? = null): ViewModel() {

    private val userRepository = UserRepository()

    private val _users = MutableStateFlow<List<User>>(emptyList())
    val users = _users.asStateFlow()

    private var job: Job? = null
    private val errorMessage = MutableStateFlow("")
    private val loading = MutableStateFlow(false)

    private var currentUser: UserResponse? = null

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        onError("Exception handled: ${throwable.localizedMessage}")
    }

    fun registerUser(email: String, password: String) {

        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            Log.d("DEBUG", "register user $email, $password")

            val response = mainRepository.createUser(CreateRequest(email, password))
            Log.d("DEBUG", "$response")
            Log.d("DEBUG", "body - ${response.body()}")
            Log.d("DEBUG", "message - ${response.message()}")
            Log.d("DEBUG", "code - ${response.code()}")
            Log.d("DEBUG", "error body - ${response.errorBody()}")
            Log.d("DEBUG", "headers - ${response.headers()}")
            Log.d("DEBUG", "raw - ${response.raw()}")
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
            Log.d("DEBUG", "in job $email, $password")

            val response = mainRepository.loginUser(LoginRequest(email, password))
            Log.d("DEBUG", "$response")
            Log.d("DEBUG", "body - ${response.body()}")
            Log.d("DEBUG", "message - ${response.message()}")
            Log.d("DEBUG", "code - ${response.code()}")
            Log.d("DEBUG", "error body - ${response.errorBody()}")
            Log.d("DEBUG", "headers - ${response.headers()}")
            Log.d("DEBUG", "raw - ${response.raw()}")
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    val responseBodyData = response.body()!!.data

                    currentUser = responseBodyData.user
                    loginCallbacks?.onSuccess(responseBodyData.accessToken, responseBodyData.refreshToken, currentUser!!.id)
                } else {
                    loginCallbacks?.onInvalidLoginData()
                }
            }
        }
    }

    fun getUser(userId: Int, accessToken: String) {

        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            Log.d("DEBUG", "get user $userId, $accessToken")

            val response = mainRepository.getUser(userId, Constants.BEARER_TOKEN_START + accessToken)
            Log.d("DEBUG", "$response")
            Log.d("DEBUG", "body - ${response.body()}")
            Log.d("DEBUG", "message - ${response.message()}")
            Log.d("DEBUG", "code - ${response.code()}")
            Log.d("DEBUG", "error body - ${response.errorBody()}")
            Log.d("DEBUG", "headers - ${response.headers()}")
            Log.d("DEBUG", "raw - ${response.raw()}")
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    currentUser = response.body()?.data?.user
                    registrationCallbacks?.onUserIsRemembered()
                }
            }
        }
    }

    fun editUserNameAndPhone(userId: Int, accessToken: String, userName: String, phoneNumber: String) {

        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            Log.d("DEBUG", "edit user $userId, $accessToken")
            Log.d("DEBUG", "edit user $userName, $phoneNumber")

            val zxc = EditUserRequest(userName, phoneNumber)
            val token = Constants.BEARER_TOKEN_START + accessToken
            Log.d("DEBUG", "edit user $zxc $token")

            val response = mainRepository.editUser(userId,
                Constants.BEARER_TOKEN_START + accessToken,
                EditUserRequest(userName, phoneNumber)
            )

            Log.d("DEBUG", "$response")
            Log.d("DEBUG", "body - ${response.body()}")
            Log.d("DEBUG", "message - ${response.message()}")
            Log.d("DEBUG", "code - ${response.code()}")
            Log.d("DEBUG", "error body - ${response.errorBody()}")
            Log.d("DEBUG", "headers - ${response.headers()}")
            Log.d("DEBUG", "raw - ${response.raw()}")
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    currentUser = response.body()?.data?.user
                    editCallbacks?.onUserEdited()
                }
            }
        }
    }

    fun init() {
        fetchUsers()
    }

    private fun fetchUsers() {
        CoroutineScope(Dispatchers.Main).launch {
            _users.value = userRepository.addUsersToList()
        }
    }

    fun deleteUser(id: Int) {
        CoroutineScope(Dispatchers.Main).launch {
            userRepository.deleteUser(id)
        }
        _users.value = _users.value.filter { it.id != id }
    }

    fun addUser(position: Int, user: User) {
        CoroutineScope(Dispatchers.Main).launch {
            userRepository.addUser(position, user)

            val currentList = _users.value.toMutableList()

            currentList.add(position, user)

            _users.value = currentList
        }
    }

    private fun onError(message: String) {
        errorMessage.value = message
        loading.value = false
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
}
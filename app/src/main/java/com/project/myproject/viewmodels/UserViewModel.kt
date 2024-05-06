package com.project.myproject.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import com.project.myproject.Constants
import com.project.myproject.models.User
import com.project.myproject.network.retrofit.models.CreateRequest
import com.project.myproject.network.retrofit.models.LoginRequest
import com.project.myproject.network.retrofit.response.UserResponse
import com.project.myproject.repository.MainRepository
import com.project.myproject.repository.UserRepository
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

interface RegistrationCallbacks {
    fun onSuccess(accessToken: String, refreshToken: String, userId: Int)
    fun onEmailTakenError()
    fun onUserIsRemembered()
    fun onTokensRefreshed(newAuthToken: String, newRefreshToken: String)
}

interface LoginCallbacks {
    fun onSuccess(accessToken: String, refreshToken: String, userId: Int)
    fun onInvalidLoginData()
}

@HiltViewModel
class UserViewModel @Inject constructor(private val mainRepository: MainRepository,
    private var registrationCallbacks: RegistrationCallbacks? = null,
    private var loginCallbacks: LoginCallbacks? = null): ViewModel() {

    private val userRepository = UserRepository()

    private val _users = MutableStateFlow<List<User>>(emptyList())
    val users = _users.asStateFlow()

    private var job: Job? = null
    private val errorMessage = MutableStateFlow<String>("")
    private val loading = MutableStateFlow<Boolean>(false)

    private var currentUser: UserResponse? = null

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        onError("Exception handled: ${throwable.localizedMessage}")
    }

    fun registerUser(email: String, password: String) {

        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            Log.d("DEBUG", "in job $email, $password")

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

    fun refreshTokens(refreshToken: String) {

        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            Log.d("DEBUG", "Token refreshing $refreshToken")

            val response = mainRepository.refreshTokens(refreshToken)
            Log.d("DEBUG", "$response")
            Log.d("DEBUG", "body - ${response.body()}")
            Log.d("DEBUG", "message - ${response.message()}")
            Log.d("DEBUG", "code - ${response.code()}")
            Log.d("DEBUG", "error body - ${response.errorBody()}")
            Log.d("DEBUG", "headers - ${response.headers()}")
            Log.d("DEBUG", "raw - ${response.raw()}")
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    registrationCallbacks?.onTokensRefreshed(response.body()!!.accessToken, response.body()!!.accessToken)
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
}
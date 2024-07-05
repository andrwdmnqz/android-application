package com.project.myproject.ui.fragments.register.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.myproject.data.repository.UserRepository
import com.project.myproject.data.requests.CreateRequest
import com.project.myproject.data.room.entities.User
import com.project.myproject.domain.Repository
import com.project.myproject.ui.fragments.viewmodel.LoginState
import com.project.myproject.utils.NetworkUtil
import com.project.myproject.utils.SessionManager
import com.project.myproject.utils.SettingPreference
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val repository: Repository,
    private val userRepository: UserRepository,
    private val networkUtil: NetworkUtil,
    private val sessionManager: SessionManager,
    private val settingPreference: SettingPreference
) : ViewModel() {

    private val _registrationState = MutableStateFlow<RegistrationState>(RegistrationState.Idle)
    val registrationState = _registrationState.asStateFlow()

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser = _currentUser.asStateFlow()

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState = _loginState.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading = _loading.asStateFlow()

    fun registerUser(email: String, password: String, isUserRemembered: Boolean) {
        if (networkUtil.isInternetAvailable()) {
            viewModelScope.launch(Dispatchers.IO) {

                val result = repository.createUser(CreateRequest(email, password))

                withContext(Dispatchers.Main) {
                    if (result != null) {
                        val responseBodyData = result.data

                        handleUserResponse(
                            responseBodyData.user,
                            responseBodyData.accessToken,
                            responseBodyData.refreshToken,
                            isUserRemembered
                        )

                        _registrationState.value = RegistrationState.Success
                    } else {
                        _registrationState.value = RegistrationState.InvalidRegisterData
                    }

                    _registrationState.value = RegistrationState.Idle
                }
            }
        }
    }

    fun getUser() {

        viewModelScope.launch(Dispatchers.IO) {

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

                if (networkUtil.isInternetAvailable()) {
                    val result = repository.getUser(userId)

                    withContext(Dispatchers.Main) {
                        if (result != null) {
                            _currentUser.value = result.data.user

                            _registrationState.value = RegistrationState.RememberedUser
                            _loginState.value = LoginState.Success
                        } else {
                            sessionManager.resetData()
                            settingPreference.clearData()
                        }
                    }
                } else {
                    _currentUser.value = userRepository.getUser(userId)

                    _registrationState.value = RegistrationState.RememberedUser
                    _loginState.value = LoginState.Success
                }
            }
        }
    }

    private suspend fun handleUserResponse(
        user: User,
        accessToken: String,
        refreshToken: String,
        isUserRemembered: Boolean
    ) {
        _currentUser.value = user

        if (isUserRemembered) {
            settingPreference.setupData(user.id, accessToken, refreshToken)
        }
        sessionManager.setupData(user.id, accessToken, refreshToken, isUserRemembered)
    }
}

sealed class RegistrationState {
    data object Idle : RegistrationState()
    data object Success : RegistrationState()
    data object InvalidRegisterData : RegistrationState()
    data object RememberedUser : RegistrationState()
}
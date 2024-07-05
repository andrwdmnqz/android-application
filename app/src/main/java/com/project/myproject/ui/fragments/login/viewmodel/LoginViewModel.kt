package com.project.myproject.ui.fragments.login.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.myproject.data.requests.LoginRequest
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
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: Repository,
    private val networkUtil: NetworkUtil,
    private val sessionManager: SessionManager,
    private val settingPreference: SettingPreference
) : ViewModel() {

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser = _currentUser.asStateFlow()

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState = _loginState.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading = _loading.asStateFlow()

    fun loginUser(email: String, password: String, isUserRemembered: Boolean) {
        if (networkUtil.isInternetAvailable()) {
            viewModelScope.launch(Dispatchers.IO) {
                val result = repository.loginUser(LoginRequest(email, password))

                withContext(Dispatchers.Main) {
                    if (result != null) {
                        val responseBodyData = result.data

                        handleUserResponse(
                            responseBodyData.user,
                            responseBodyData.accessToken,
                            responseBodyData.refreshToken,
                            isUserRemembered
                        )

                        _loginState.value = LoginState.Success
                    } else {
                         _loginState.value = LoginState.InvalidLoginData
                    }
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
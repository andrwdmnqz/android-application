package com.project.myproject.utils

import com.project.myproject.data.repository.MainRepository
import com.project.myproject.utils.callbacks.TokenCallbacks
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Provider

class SessionManager @Inject constructor(
    private val settingPreference: SettingPreference,
    private val mainRepositoryProvider: Provider<MainRepository>,
    private val tokenCallbacks: TokenCallbacks,
) {
    private var userId: Int = -1
    private var accessToken: String = ""
    private var refreshToken: String = ""
    private var isUserRemembered: Boolean = true

    fun getId() = userId

    fun getAccessToken() = accessToken

    fun getRefreshToken() = refreshToken

    private fun getUserRememberState() = isUserRemembered

    fun setId(id: Int) {
        userId = id
    }

    fun setAccessToken(token: String) {
        accessToken = token
    }

    fun setRefreshToken(token: String) {
        refreshToken = token
    }

    fun setUserRememberState(state: Boolean) {
        isUserRemembered = state
    }

    fun resetData() {
        userId = -1
        accessToken = ""
        refreshToken = ""
        isUserRemembered = true
    }

    suspend fun refreshAccessToken(): String? {
        return try {
            val response = withContext(Dispatchers.IO) {
                mainRepositoryProvider.get().refreshTokens()
            }
            if (response.isSuccessful) {

                val tokenResponse = response.body()

                setAccessToken(tokenResponse?.data?.accessToken!!)
                setRefreshToken(tokenResponse.data.refreshToken)

                if (getUserRememberState()) {
                    settingPreference.saveAccessToken(tokenResponse.data.accessToken)
                    settingPreference.saveRefreshToken(tokenResponse.data.refreshToken)
                }

                tokenResponse.data.accessToken
            } else {
                tokenCallbacks.onTokensRefreshFailure()
                null
            }
        } catch (e: Exception) {
            null
        }
    }
}
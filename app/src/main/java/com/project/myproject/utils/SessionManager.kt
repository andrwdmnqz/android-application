package com.project.myproject.utils

class SessionManager {
    private var userId: Int = -1
    private var accessToken: String = ""
    private var refreshToken: String = ""
    private var isUserRemembered: Boolean = true

    fun getId() = userId

    fun getAccessToken() = accessToken

    fun getRefreshToken() = refreshToken

    fun getUserRememberState() = isUserRemembered

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
}
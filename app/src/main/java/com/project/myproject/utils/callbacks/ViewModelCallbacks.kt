package com.project.myproject.utils.callbacks

interface RegistrationCallbacks {
    fun onSuccess(accessToken: String, refreshToken: String, userId: Int)
    fun onEmailTakenError()
    fun onUserIsRemembered()
}

interface LoginCallbacks {
    fun onSuccess(accessToken: String, refreshToken: String, userId: Int)
    fun onInvalidLoginData()
}

interface TokenCallbacks {
    fun onTokensRefreshFailure()
}

interface EditCallbacks {
    fun onUserEdited()
}
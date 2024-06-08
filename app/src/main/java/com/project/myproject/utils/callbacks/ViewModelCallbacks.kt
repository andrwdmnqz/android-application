package com.project.myproject.utils.callbacks

interface RegistrationCallbacks {
    fun onSuccess(accessToken: String, refreshToken: String, userId: Int)
    fun onEmailTakenError()
    fun onUserIsRemembered()
    fun onUserIsNotRemembered()
}

interface LoginCallbacks {
    fun onSuccess()
    fun onInvalidLoginData()
}

interface TokenCallbacks {
    fun onTokensRefreshFailure()
}

interface EditCallbacks {
    fun onUserEdited()
}

interface AddContactCallbacks {
    fun onContactAdded()
}
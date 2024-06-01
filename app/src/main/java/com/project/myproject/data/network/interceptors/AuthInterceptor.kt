package com.project.myproject.data.network.interceptors

import com.project.myproject.data.repository.MainRepository
import com.project.myproject.utils.Constants
import com.project.myproject.utils.SessionManager
import com.project.myproject.utils.SettingPreference
import com.project.myproject.utils.callbacks.TokenCallbacks
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Provider

class AuthInterceptor @Inject constructor(
    private val mainRepositoryProvider: Provider<MainRepository>,
    private val sessionManager: SessionManager,
    private val tokenCallbacks: TokenCallbacks,
    private val settingPreference: SettingPreference
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        val isTokenRefreshRequest = originalRequest.url.encodedPath.endsWith("/refresh")

        val headersRequestBuilder = newRequestBuilder(originalRequest, isTokenRefreshRequest,
                                                    sessionManager.getAccessToken())

        val response = chain.proceed(headersRequestBuilder.build())

        if (response.code == Constants.UNAUTHORIZED_CODE) {
            val newToken = runBlocking {
                refreshAccessToken()
            }

            if (newToken != null) {
                val retryRequest = newRequestBuilder(originalRequest, isTokenRefreshRequest, newToken)

                return chain.proceed(retryRequest.build())
            }
        }

        return response
    }

    private fun newRequestBuilder(
        originalRequest: Request,
        isTokenRefreshRequest: Boolean,
        accessToken: String?
    ): Request.Builder {
        val retryRequest = originalRequest.newBuilder()

        if (isTokenRefreshRequest) {
            retryRequest.addHeader("RefreshToken", sessionManager.getRefreshToken())
        } else {
            retryRequest.addHeader("Authorization", Constants.BEARER_TOKEN_START + accessToken)
        }

        if (originalRequest.body != null) {
            retryRequest.addHeader("Content-type", Constants.APPLICATION_JSON_TYPE)
        }
        return retryRequest
    }

    private suspend fun refreshAccessToken(): String? {
        val refreshToken = sessionManager.getRefreshToken()
        return try {
            val response = withContext(Dispatchers.IO) {
                mainRepositoryProvider.get().refreshTokens()
            }
            if (response.isSuccessful) {

                val tokenResponse = response.body()

                sessionManager.setAccessToken(tokenResponse?.data?.accessToken!!)
                sessionManager.setRefreshToken(tokenResponse.data.refreshToken)

                if (sessionManager.getUserRememberState()) {
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
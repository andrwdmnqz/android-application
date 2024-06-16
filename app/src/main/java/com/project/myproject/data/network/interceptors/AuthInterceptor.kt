package com.project.myproject.data.network.interceptors

import com.project.myproject.utils.SessionManager
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val sessionManager: SessionManager
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        val isTokenRefreshRequest = originalRequest.url.encodedPath.endsWith(REFRESH_TOKEN_PATH)

        val headersRequestBuilder = newRequestBuilder(originalRequest, isTokenRefreshRequest,
                                                    sessionManager.getAccessToken())

        val response = chain.proceed(headersRequestBuilder.build())

        if (response.code == UNAUTHORIZED_CODE) {
            val newToken = runBlocking {
                sessionManager.refreshAccessToken()
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
            retryRequest.addHeader(REFRESH_TOKEN, sessionManager.getRefreshToken())
        } else {
            retryRequest.addHeader(AUTHORIZATION, BEARER_TOKEN_START + accessToken)
        }

        if (originalRequest.body != null) {
            retryRequest.addHeader(CONTENT_TYPE, APPLICATION_JSON_TYPE)
        }
        return retryRequest
    }

    companion object {
        private const val REFRESH_TOKEN_PATH = "/refresh"
        private const val REFRESH_TOKEN = "RefreshToken"
        private const val AUTHORIZATION = "Authorization"
        private const val CONTENT_TYPE = "Content-type"
        private const val BEARER_TOKEN_START = "Bearer "
        private const val APPLICATION_JSON_TYPE = "application/json"
        private const val UNAUTHORIZED_CODE = 401
    }
}
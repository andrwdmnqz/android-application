package com.project.myproject.data.network.interceptors

import com.project.myproject.data.repository.MainRepository
import com.project.myproject.utils.SessionManager
import com.project.myproject.utils.callbacks.TokenCallbacks
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val mainRepository: MainRepository,
    private val sessionManager: SessionManager,
    private val tokenCallbacks: TokenCallbacks
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = chain.proceed(request)

        if (response.code == 401) {
            val newToken = runBlocking {
                refreshAccessToken()
            }

            if (newToken != null) {
                val newRequest = request.newBuilder()
                    .header("Authorization", "Bearer $newToken")
                    .build()

                return chain.proceed(newRequest)
            }
        }

        return response
    }

    private suspend fun refreshAccessToken(): String? {
        val refreshToken = sessionManager.getRefreshToken()
        return try {
            val response = withContext(Dispatchers.IO) {
                mainRepository.refreshTokens(refreshToken)
            }
            if (response.isSuccessful) {

                val tokenResponse = response.body()

                sessionManager.setAccessToken(tokenResponse?.data?.accessToken!!)
                sessionManager.setRefreshToken(tokenResponse.data.refreshToken)

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
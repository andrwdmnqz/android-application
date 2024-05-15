package com.project.myproject.utils

import android.util.Log
import com.project.myproject.data.repository.MainRepository
import com.project.myproject.utils.callbacks.TokenCallbacks
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject

class TokenAuthenticator @Inject constructor(
    private val mainRepository: MainRepository,
    private val preference: SettingPreference,
    private val coroutineScope: CoroutineScope,
    private val tokenCallbacks: TokenCallbacks,
    private val sessionManager: SessionManager
): Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        val responseCount = responseCount(response)
        Log.d("DEBUG", "In authenticator route $route")
        Log.d("DEBUG", "In authenticator response $response")
        Log.d("DEBUG", "In authenticator response count $responseCount")
        if (responseCount >= Constants.RETRY_REQUEST_TIMES) {
            return null
        }

        coroutineScope.launch {
            val refreshTokenResponse = mainRepository.refreshTokens(sessionManager.getRefreshToken())

            if (refreshTokenResponse.isSuccessful) {
                val newAccessToken = refreshTokenResponse.body()!!.data.accessToken
                val newRefreshToken = refreshTokenResponse.body()!!.data.refreshToken

                sessionManager.setAccessToken(newAccessToken)
                sessionManager.setRefreshToken(newRefreshToken)

                if (sessionManager.getUserRememberState()) {
                    preference.saveAccessToken(newAccessToken)
                    preference.saveRefreshToken(newRefreshToken)
                }

                continueRequestWithNewAccessToken(response, newAccessToken)
            } else {
                preference.clearData()
                tokenCallbacks.onTokensRefreshFailure()
            }
        }

        return null
    }

    private fun continueRequestWithNewAccessToken(
        response: Response,
        accessToken: String
    ): Request {

        return response.request.newBuilder()
            .header("Authorization", Constants.BEARER_TOKEN_START + accessToken)
            .build()
    }

    private fun responseCount(response: Response): Int {
        var result = 1
        var currentResponse = response.priorResponse

        while (currentResponse != null) {
            result++
            currentResponse = currentResponse.priorResponse
        }
        return result
    }
}
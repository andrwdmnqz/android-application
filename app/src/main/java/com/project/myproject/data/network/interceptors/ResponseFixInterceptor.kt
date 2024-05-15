package com.project.myproject.data.network.interceptors

import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody

class ResponseFixInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalResponse = chain.proceed(chain.request())
        val responseBody = originalResponse.body

        if (responseBody != null) {
            val originalBodyString = responseBody.string()
            val modifiedBodyString = fixIncompleteJson(originalBodyString)

            return originalResponse.newBuilder()
                .body(modifiedBodyString.toResponseBody(responseBody.contentType()))
                .build()
        }

        return originalResponse
    }

    private fun fixIncompleteJson(json: String): String {

        val openingBraceCount = json.count { it == '{' }
        val closingBraceCount = json.count { it == '}' }

        val fixedJson = StringBuilder(json)

        repeat(openingBraceCount - closingBraceCount) {
            fixedJson.append("}")
        }

        return fixedJson.toString()
    }
}
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
            val modifiedBodyString = fixJson(originalBodyString)

            return originalResponse.newBuilder()
                .body(modifiedBodyString.toResponseBody(responseBody.contentType()))
                .build()
        }

        return originalResponse
    }

    private fun fixJson(json: String): String {

        val openingBraceCount = json.count { it == '{' }
        val closingBraceCount = json.count { it == '}' }
        json.replace("\"id:", "\"id\":")
            .replace("\"name:", "\"name\":")
            .replace("\"email:", "\"email\":")
            .replace("\"phone:", "\"phone\":")
            .replace("\"career:", "\"career\":")
            .replace("\"address:", "\"address\":")
            .replace("\"birthday:", "\"birthday\":")
            .replace("\"facebook:", "\"facebook\":")
            .replace("\"instagram:", "\"instagram\":")
            .replace("\"twitter:", "\"twitter\":")
            .replace("\"linkedin:", "\"linkedin\":")
            .replace("\"image:", "\"image\":")
            .replace("\"created_at:", "\"created_at\":")
            .replace("\"updated_at:", "\"updated_at\":")

        val fixedJson = StringBuilder(json)

        repeat(openingBraceCount - closingBraceCount) {
            fixedJson.append("}")
        }

        return fixedJson.toString()
    }
}
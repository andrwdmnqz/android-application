package com.project.myproject.repository

import android.util.Log
import com.project.myproject.network.retrofit.RetrofitService
import com.project.myproject.network.retrofit.models.CreateRequest
import com.project.myproject.network.retrofit.response.CreateResponse
import retrofit2.Response

class MainRepository constructor(private val retrofitService: RetrofitService) {

    suspend fun createUser(createRequest: CreateRequest): Response<CreateResponse> {
        try {
            Log.d("DEBUG", "in repository $createRequest")
            val responseResult = retrofitService.createUser(createRequest)
            Log.d("DEBUG", "in repository just anything after response")

            Log.d("DEBUG", "in repository $responseResult")
            return responseResult
        } catch (e: Exception) {
            Log.d("DEBUG", "Exception occurred: ${e.message}", e)
            throw e // Rethrow the exception or handle it according to your requirements
        }
    }
}


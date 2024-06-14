package com.project.myproject.utils.hilt

import com.project.myproject.data.network.interceptors.AuthInterceptor
import com.project.myproject.data.network.interceptors.ResponseFixInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DebugBuildTypeModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(
        authInterceptor: AuthInterceptor,
        responseFixInterceptor: ResponseFixInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .addInterceptor(authInterceptor)
            .addInterceptor(responseFixInterceptor)
            .build()
    }
}
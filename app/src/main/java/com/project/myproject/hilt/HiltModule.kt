package com.project.myproject.hilt

import com.project.myproject.Constants
import com.project.myproject.fragments.LoginFragment
import com.project.myproject.fragments.RegisterFragment
import com.project.myproject.network.retrofit.RetrofitService
import com.project.myproject.repository.MainRepository
import com.project.myproject.viewmodels.LoginCallbacks
import com.project.myproject.viewmodels.RegistrationCallbacks
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object HiltModule {

    @Provides
    fun providesBaseUrl(): String = Constants.BASE_URL

    @Provides
    @Singleton
    fun provideRetrofit(baseUrl: String): Retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(baseUrl)
        .build()

    @Provides
    @Singleton
    fun provideRetrofitService(retrofit : Retrofit) : RetrofitService = retrofit.create(RetrofitService::class.java)

    @Provides
    @Singleton
    fun provideMainRepository(retrofitService: RetrofitService): MainRepository = MainRepository(retrofitService)

    @Provides
    @Singleton
    fun provideRegisterFragment(): RegistrationCallbacks = RegisterFragment()

    @Provides
    @Singleton
    fun provideLoginFragment(): LoginCallbacks = LoginFragment()
}
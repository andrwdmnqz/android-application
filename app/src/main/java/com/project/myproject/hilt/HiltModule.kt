package com.project.myproject.hilt

import android.content.Context
import com.project.myproject.Constants
import com.project.myproject.SettingPreference
import com.project.myproject.fragments.LoginFragment
import com.project.myproject.fragments.ProfileDataFragment
import com.project.myproject.fragments.RegisterFragment
import com.project.myproject.network.retrofit.RetrofitService
import com.project.myproject.network.retrofit.TokenAuthenticator
import com.project.myproject.repository.MainRepository
import com.project.myproject.viewmodels.EditCallbacks
import com.project.myproject.viewmodels.LoginCallbacks
import com.project.myproject.viewmodels.TokenCallbacks
import com.project.myproject.viewmodels.RegistrationCallbacks
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import okhttp3.OkHttpClient
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
    fun provideOkHttpClient(tokenAuthenticator: TokenAuthenticator): OkHttpClient =
        OkHttpClient.Builder()
            .authenticator(tokenAuthenticator)
            .build()

    @Provides
    @Singleton
    fun provideRetrofit(baseUrl: String): Retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(baseUrl)
        .client(OkHttpClient())
        .build()

    @Provides
    @Singleton
    fun provideRetrofitService(retrofit : Retrofit) : RetrofitService = retrofit.create(RetrofitService::class.java)

    @Provides
    @Singleton
    fun provideMainRepository(retrofitService: RetrofitService): MainRepository = MainRepository(retrofitService)

    @Provides
    @Singleton
    fun provideSettingPreferences(@ApplicationContext context: Context): SettingPreference = SettingPreference(context)

    @Provides
    @Singleton
    fun provideCoroutineScope(): CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    @Provides
    @Singleton
    fun provideRegisterFragment(): RegistrationCallbacks = RegisterFragment()

    @Provides
    @Singleton
    fun provideLoginFragment(): LoginCallbacks = LoginFragment()

    @Provides
    @Singleton
    fun provideOverallCallbacks(): TokenCallbacks = RegisterFragment()

    @Provides
    @Singleton
    fun provideEditCallbacks(): EditCallbacks = ProfileDataFragment()
}
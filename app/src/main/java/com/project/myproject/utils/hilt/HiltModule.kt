package com.project.myproject.utils.hilt

import android.content.Context
import com.project.myproject.utils.Constants
import com.project.myproject.utils.SettingPreference
import com.project.myproject.ui.fragments.LoginFragment
import com.project.myproject.ui.fragments.ProfileDataFragment
import com.project.myproject.ui.fragments.RegisterFragment
import com.project.myproject.data.network.RetrofitService
import com.project.myproject.data.network.interceptors.AuthInterceptor
import com.project.myproject.data.network.interceptors.ResponseFixInterceptor
import com.project.myproject.data.repository.MainRepository
import com.project.myproject.ui.fragments.AddContactsFragment
import com.project.myproject.utils.SessionManager
import com.project.myproject.utils.callbacks.AddContactCallbacks
import com.project.myproject.utils.callbacks.EditCallbacks
import com.project.myproject.utils.callbacks.LoginCallbacks
import com.project.myproject.utils.callbacks.RegistrationCallbacks
import com.project.myproject.utils.callbacks.TokenCallbacks
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
import javax.inject.Provider
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object HiltModule {

    @Provides
    fun providesBaseUrl(): String = Constants.BASE_URL

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(
        baseUrl: String,
        okHttpClient: OkHttpClient,
        authInterceptor: AuthInterceptor,
        responseFixInterceptor: ResponseFixInterceptor
    ): Retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(baseUrl)
        .client(okHttpClient.newBuilder()
            .addInterceptor(authInterceptor)
            .addInterceptor(responseFixInterceptor)
            .build())
        .build()


    @Provides
    @Singleton
    fun provideAuthInterceptor(
        mainRepositoryProvider: Provider<MainRepository>,
        sessionManager: SessionManager,
        tokenCallbacks: TokenCallbacks
    ): AuthInterceptor =
        AuthInterceptor(mainRepositoryProvider, sessionManager, tokenCallbacks)

    @Provides
    @Singleton
    fun provideResponseFixInterceptor(): ResponseFixInterceptor = ResponseFixInterceptor()


    @Provides
    @Singleton
    fun provideRetrofitService(retrofit : Retrofit) : RetrofitService = retrofit.create(
        RetrofitService::class.java)

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
    fun provideEditCallbacks(): EditCallbacks = ProfileDataFragment()

    @Provides
    @Singleton
    fun provideAddContactCallbacks(): AddContactCallbacks = AddContactsFragment()

    @Provides
    @Singleton
    fun provideTokenCallbacks(): TokenCallbacks = RegisterFragment()

    @Provides
    @Singleton
    fun provideSessionManager(): SessionManager = SessionManager()
}
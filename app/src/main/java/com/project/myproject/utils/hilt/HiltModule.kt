package com.project.myproject.utils.hilt

import android.content.Context
import androidx.room.Room
import com.project.myproject.BuildConfig
import com.project.myproject.data.dao.ContactDao
import com.project.myproject.data.dao.UserDao
import com.project.myproject.data.database.AppDatabase
import com.project.myproject.data.network.RetrofitService
import com.project.myproject.data.network.interceptors.AuthInterceptor
import com.project.myproject.data.network.interceptors.ResponseFixInterceptor
import com.project.myproject.data.repository.MainRepository
import com.project.myproject.data.repository.Repository
import com.project.myproject.ui.fragments.RegisterFragment
import com.project.myproject.utils.SessionManager
import com.project.myproject.utils.SettingPreference
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
    fun providesBaseUrl(): String = BuildConfig.BASE_URL

    @Provides
    @Singleton
    fun provideRetrofit(
        baseUrl: String,
        okHttpClient: OkHttpClient,
    ): Retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(baseUrl)
        .client(okHttpClient)
        .build()

    @Provides
    @Singleton
    fun provideAuthInterceptor(
        sessionManager: SessionManager,
    ): AuthInterceptor =
        AuthInterceptor(sessionManager)

    @Provides
    @Singleton
    fun provideResponseFixInterceptor(): ResponseFixInterceptor = ResponseFixInterceptor()

    @Provides
    @Singleton
    fun provideRetrofitService(retrofit: Retrofit): RetrofitService = retrofit.create(
        RetrofitService::class.java
    )

    @Provides
    @Singleton
    fun provideMainRepository(retrofitService: RetrofitService): Repository =
        MainRepository(retrofitService)

    @Provides
    @Singleton
    fun provideSettingPreferences(@ApplicationContext context: Context): SettingPreference =
        SettingPreference(context)

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "app_database"
        ).build()
    }

    @Provides
    fun provideUserDao(appDatabase: AppDatabase): UserDao {
        return appDatabase.userDao()
    }

    @Provides
    fun provideContactDao(appDatabase: AppDatabase): ContactDao {
        return appDatabase.contactDao()
    }

    @Provides
    @Singleton
    fun provideCoroutineScope(): CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    @Provides
    @Singleton
    fun provideTokenCallbacks(): TokenCallbacks = RegisterFragment()

    @Provides
    @Singleton
    fun provideSessionManager(
        settingPreference: SettingPreference,
        mainRepositoryProvider: Provider<MainRepository>,
        tokenCallbacks: TokenCallbacks
    ): SessionManager = SessionManager(settingPreference, mainRepositoryProvider, tokenCallbacks)
}
package com.project.myproject.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SettingPreference @Inject constructor (private val context: Context) {

    fun getAccessToken(): Flow<String> {
        return context.dataStore.data.map { it[ACCESS_TOKEN_KEY] ?: ""}
    }

    fun getRefreshToken(): Flow<String> {
        return context.dataStore.data.map { it[REFRESH_TOKEN_KEY] ?: ""}
    }

    fun getUserId(): Flow<Int> {
        return context.dataStore.data.map { it[USER_ID_KEY] ?: -1}
    }

    suspend fun saveAccessToken(accessToken: String) {
        context.dataStore.edit { it[ACCESS_TOKEN_KEY] = accessToken }
    }

    suspend fun saveRefreshToken(refreshToken: String) {
        context.dataStore.edit { it[REFRESH_TOKEN_KEY] = refreshToken }
    }

    suspend fun saveUserId(userId: Int) {
        context.dataStore.edit { it[USER_ID_KEY] = userId }
    }

    suspend fun clearData() {
        context.dataStore.edit { it.clear() }
    }

    companion object {
        private const val ACCESS_TOKEN_KEY_NAME = "accessToken"
        private const val REFRESH_TOKEN_KEY_NAME = "refreshToken"
        private const val USER_ID_KEY_NAME = "userId"
        private const val DATA_STORE_NAME = "MyPreferences"

        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = DATA_STORE_NAME)
        private val ACCESS_TOKEN_KEY = stringPreferencesKey(ACCESS_TOKEN_KEY_NAME)
        private val REFRESH_TOKEN_KEY = stringPreferencesKey(REFRESH_TOKEN_KEY_NAME)
        private val USER_ID_KEY = intPreferencesKey(USER_ID_KEY_NAME)
    }
}
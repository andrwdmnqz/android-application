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

class SettingPreference(private val context: Context) {

    companion object {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = Constants.DATA_STORE_NAME)
        private val ACCESS_TOKEN_KEY = stringPreferencesKey(Constants.ACCESS_TOKEN_KEY)
        private val REFRESH_TOKEN_KEY = stringPreferencesKey(Constants.REFRESH_TOKEN_KEY)
        private val USER_ID_KEY = intPreferencesKey(Constants.USER_ID_KEY)
    }

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
}
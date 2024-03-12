package com.project.myproject

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SettingPreference(private val context: Context) {

    companion object {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
        private val EMAIL_KEY = stringPreferencesKey("EMAIL_KEY")
        private val PASSWORD_KEY = stringPreferencesKey("PASSWORD_KEY")
    }

    fun getEmail(): Flow<String> {
        return context.dataStore.data.map { it[EMAIL_KEY] ?: ""}
    }

    fun getPassword(): Flow<String> {
        return context.dataStore.data.map { it[PASSWORD_KEY] ?: ""}
    }

    suspend fun saveEmail(email: String) {
        context.dataStore.edit { it[EMAIL_KEY] = email }
    }

    suspend fun savePassword(password: String) {
        context.dataStore.edit { it[PASSWORD_KEY] = password }
    }

    suspend fun clearData() {
        context.dataStore.edit { it.clear() }
    }
}
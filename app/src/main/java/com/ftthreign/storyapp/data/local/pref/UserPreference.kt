package com.ftthreign.storyapp.data.local.pref

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


val Context.dataStore : DataStore<Preferences> by preferencesDataStore(name = "session_token")

class UserPreference private constructor(private val dataStore: DataStore<Preferences>) {
    suspend fun saveSession(userModel: UserModel) {
        dataStore.edit { pref ->
            pref[EMAIL_KEY] = userModel.email
            pref[TOKEN_KEY] = userModel.token
            pref[IS_LOGIN] = userModel.isLoading
        }
    }

    fun getSession() : Flow<UserModel> {
        return dataStore.data.map { pref ->
            UserModel(
                pref[EMAIL_KEY] ?: "",
                pref[TOKEN_KEY] ?: "",
                pref[IS_LOGIN] ?: false
            )
        }
    }

    suspend fun logout() {
        dataStore.edit { pref ->
            pref.clear()
        }
    }

    companion object {
        @Volatile
        private var INSTANCE : UserPreference? = null

        private val EMAIL_KEY = stringPreferencesKey("email")
        private val TOKEN_KEY = stringPreferencesKey("token_key")
        private val IS_LOGIN = booleanPreferencesKey("is_login")

        fun getInstance(
            dataStore: DataStore<Preferences>
        ) : UserPreference {
            return INSTANCE ?: synchronized(this) {
                val instance = UserPreference(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }
}
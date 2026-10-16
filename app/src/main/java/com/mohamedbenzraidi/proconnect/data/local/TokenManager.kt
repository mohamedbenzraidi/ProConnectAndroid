package com.mohamedbenzraidi.proconnect.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "proconnect_prefs")

class TokenManager(private val context: Context) {

    companion object {
        private val JWT_TOKEN_KEY = stringPreferencesKey("jwt_token")
        private val USER_ID_KEY = stringPreferencesKey("user_id")
    }

    val token: Flow<String?> = context.dataStore.data.map { prefs ->
        prefs[JWT_TOKEN_KEY]
    }

    val userId: Flow<String?> = context.dataStore.data.map { prefs ->
        prefs[USER_ID_KEY]
    }

    suspend fun saveAuthData(token: String, userId: String) {
        context.dataStore.edit { prefs ->
            prefs[JWT_TOKEN_KEY] = token
            prefs[USER_ID_KEY] = userId
        }
    }

    suspend fun clearSession() {
        context.dataStore.edit { prefs ->
            prefs.clear()
        }
    }
}
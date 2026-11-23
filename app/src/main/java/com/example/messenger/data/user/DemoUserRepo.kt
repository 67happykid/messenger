package com.example.messenger.data.user

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

private val Context.userProfileDataStore by preferencesDataStore(
    name = "demo_user_profile"
)

data class UserProfile(
    val username: String,
    val email: String?,
    val phone: String?
)

class DemoUserRepo(
    context: Context
) {
    private val dataStore =
        context.applicationContext.userProfileDataStore

    private object Keys {
        val username = stringPreferencesKey("username")
        val email = stringPreferencesKey("email")
        val phone = stringPreferencesKey("phone")
    }

    val profile: Flow<UserProfile> = dataStore.data
        .map { preferences ->
            UserProfile(
                username = preferences[Keys.username] ?: "user_001",
                email = preferences[Keys.email] ?: "user@example.com",
                phone = preferences[Keys.phone]
            )
        }
        .distinctUntilChanged()

    suspend fun updateUsername(username: String) {
        dataStore.edit { preferences ->
            preferences[Keys.username] = username
        }
    }

    suspend fun updateEmail(email: String) {
        dataStore.edit { preferences ->
            preferences[Keys.email] = email
        }
    }

    suspend fun updatePhone(phone: String) {
        dataStore.edit { preferences ->
            preferences[Keys.phone] = phone
        }
    }
}
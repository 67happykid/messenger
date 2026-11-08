package com.example.messenger.data.settings

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.messenger.ui.theme.ThemeMod
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import java.io.IOException

private val Context.settingsDataStore by preferencesDataStore(
    name = "app_settings"
)

class SettingsRepo(
    context: Context
) {
    private val dataStore = context.applicationContext.settingsDataStore

    private object Keys {
        val themeMod = stringPreferencesKey("theme_mod")
        val dynamicColor = booleanPreferencesKey("dynamic_color")
    }

    val settings: Flow<AppSettings> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            val savedTheme = preferences[Keys.themeMod]

            val themeMod = ThemeMod.entries.firstOrNull() { mode ->
                mode.name == savedTheme
            } ?: ThemeMod.SYSTEM

            AppSettings(
                themeMod = themeMod,
                dynamicColor = preferences[Keys.dynamicColor] ?: true
            )
        }
        .distinctUntilChanged()

    suspend fun setThemeMod(themeMod: ThemeMod) {
        dataStore.edit { preferences ->
            preferences[Keys.themeMod] = themeMod.name
        }
    }

    suspend fun setDynamicColor(enabled: Boolean){
        dataStore.edit { preferences ->
            preferences[Keys.dynamicColor] = enabled
        }
    }
}
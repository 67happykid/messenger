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
import androidx.datastore.preferences.core.intPreferencesKey

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
        val showOnlineStatus = booleanPreferencesKey("show_online_status")
        val allowFriendInvites = booleanPreferencesKey("allow_friend_invites")
        val inputDevice = stringPreferencesKey("audio_input_device")
        val outputDevice = stringPreferencesKey("audio_output_device")
        val inputVolume = intPreferencesKey("audio_input_volume")
        val outputVolume = intPreferencesKey("audio_output_volume")
        val notifyMessages = booleanPreferencesKey("notify_messages")
        val notifyMentions = booleanPreferencesKey("notify_mentions")
        val notifyCalls = booleanPreferencesKey("notify_calls")
        val showNotificationPreview = booleanPreferencesKey("show_notification_preview")
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
                dynamicColor = preferences[Keys.dynamicColor] ?: true,
                privacy = PrivacySettings(
                    showOnlineStatus = preferences[Keys.showOnlineStatus] ?: true,
                    allowFriendInvites = preferences[Keys.allowFriendInvites] ?: true
                ),
                sound = SoundSettings(
                    inputDeviceKey = preferences[Keys.inputDevice],
                    outputDeviceKey = preferences[Keys.outputDevice],
                    inputVolume = (preferences[Keys.inputVolume] ?: 100).coerceIn(0, 100),
                    outputVolume = (preferences[Keys.outputVolume] ?: 100).coerceIn(0, 100)
                ),
                notifications = NotificationSettings(
                    messages = preferences[Keys.notifyMessages] ?: true,
                    mentions = preferences[Keys.notifyMentions] ?: true,
                    calls = preferences[Keys.notifyCalls] ?: true,
                    showPreview = preferences[Keys.showNotificationPreview] ?: false
                )
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

    suspend fun setShowOnlineStatus(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[Keys.showOnlineStatus] = enabled
        }
    }

    suspend fun setAllowFriendInvites(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[Keys.allowFriendInvites] = enabled
        }
    }

    suspend fun updateSound(change: SoundChange) {
        dataStore.edit { preferences ->
            when (change) {
                is SoundChange.InputDevice -> {
                    val key = change.key
                    if (key == null) {
                        preferences.remove(Keys.inputDevice)
                    } else {
                        preferences[Keys.inputDevice] = key
                    }
                }

                is SoundChange.OutputDevice -> {
                    val key = change.key
                    if (key == null) {
                        preferences.remove(Keys.outputDevice)
                    } else {
                        preferences[Keys.outputDevice] = key
                    }
                }

                is SoundChange.InputVolume -> {
                    preferences[Keys.inputVolume] = change.percent.coerceIn(0, 100)
                }

                is SoundChange.OutputVolume -> {
                    preferences[Keys.outputVolume] = change.percent.coerceIn(0, 100)
                }
            }
        }
    }

    suspend fun setNotificationOption(
        option: NotificationOption,
        enabled: Boolean
    ) {
        val key = when (option) {
            NotificationOption.MESSAGES -> Keys.notifyMessages
            NotificationOption.MENTIONS -> Keys.notifyMentions
            NotificationOption.CALLS -> Keys.notifyCalls
            NotificationOption.SHOW_PREVIEW -> Keys.showNotificationPreview
        }

        dataStore.edit { preferences ->
            preferences[key] = enabled
        }
    }
}
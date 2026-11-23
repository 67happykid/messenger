package com.example.messenger.data.settings

import com.example.messenger.ui.theme.ThemeMod

data class PrivacySettings(
    val showOnlineStatus: Boolean = true,
    val allowFriendInvites: Boolean = true
)

data class SoundSettings(
    val inputDeviceKey: String? = null,
    val outputDeviceKey: String? = null,
    val inputVolume: Int = 100,
    val outputVolume: Int = 100
)

sealed interface SoundChange {
    data class InputDevice(val key: String?) : SoundChange
    data class OutputDevice(val key: String?) : SoundChange
    data class InputVolume(val percent: Int) : SoundChange
    data class OutputVolume(val percent: Int) : SoundChange
}

data class NotificationSettings(
    val messages: Boolean = true,
    val mentions: Boolean = true,
    val calls: Boolean = true,
    val showPreview: Boolean = false
)

enum class NotificationOption {
    MESSAGES,
    MENTIONS,
    CALLS,
    SHOW_PREVIEW
}

data class AppSettings(
    val themeMod: ThemeMod = ThemeMod.SYSTEM,
    val dynamicColor: Boolean = true,
    val privacy: PrivacySettings = PrivacySettings(),
    val sound: SoundSettings = SoundSettings(),
    val notifications: NotificationSettings = NotificationSettings(),
)
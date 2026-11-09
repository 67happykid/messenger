package com.example.messenger.ui.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data object MainRoute : NavKey

@Serializable
data object SettingsRoute : NavKey

@Serializable
data object AccountRoute : NavKey

@Serializable
data object AppearanceRoute : NavKey

@Serializable
data object PrivacyRoute : NavKey

@Serializable
data object SoundRoute : NavKey

@Serializable
data object NotificationsRoute : NavKey
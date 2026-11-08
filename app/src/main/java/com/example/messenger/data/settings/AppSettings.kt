package com.example.messenger.data.settings

import com.example.messenger.ui.theme.ThemeMod

data class AppSettings(
    val themeMod: ThemeMod = ThemeMod.SYSTEM,
    val dynamicColor: Boolean = true
)
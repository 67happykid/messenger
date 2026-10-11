package com.example.messenger.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

// Привязываем ваши цвета из Color.kt к цветовой схеме приложения
private val DarkColorScheme = darkColorScheme(
    primary = MainBackground,
    secondary = LayoutBackground,
    tertiary = SearchField,
    background = LayoutBackground,
    surface = MainBackground
)

private val LightColorScheme = lightColorScheme(
    primary = MainBackground,
    secondary = LayoutBackground,
    tertiary = SearchField,
    background = LayoutBackground,
    surface = MainBackground
)

@Composable
fun MessengerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Отключаем динамические цвета Android 12+, чтобы использовалась только ваша палитра
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> DarkColorScheme // По умолчанию используем темную тему
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
package com.example.messenger.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.example.messenger.ui.theme.MessageBackgroundDark

data class AppColors(
    val mainBackground: Color,
    val layoutBackground: Color,
    val chatBackground: Color,
    val searchField: Color,
    val strokeColor: Color,
    val textColor: Color,
    val messageBackground: Color,
    val iconBackground: Color
)

val DarkAppColors = AppColors(
    mainBackground = MainBackgroundDark,
    layoutBackground = LayoutBackgroundDark,
    chatBackground = ChatBackgroundDark,
    searchField = SearchFieldDark,
    strokeColor = StrokeColorDark,
    textColor = TextColorDark,
    messageBackground = MessageBackgroundDark,
    iconBackground = IconBackgroundDark
)

val LightAppColors = AppColors(
    mainBackground = MainBackgroundLight,
    layoutBackground = LayoutBackgroundLight,
    chatBackground = ChatBackgroundLight,
    searchField = SearchFieldLight,
    strokeColor = StrokeColorLight,
    textColor = TextColorLight,
    messageBackground = MessageBackgroundLight,
    iconBackground = IconBackgroundLight

)

data class AppColors(
    val mainBackground: Color,
    val layoutBackground: Color,
    val chatBackground: Color,
    val searchField: Color,
    val strokeColor: Color,
    val textColor: Color
)

val DarkAppColors = AppColors(
    mainBackground = MainBackgroundDark,
    layoutBackground = LayoutBackgroundDark,
    chatBackground = ChatBackgroundDark,
    searchField = SearchFieldDark,
    strokeColor = StrokeColorDark,
    textColor = TextColorDark
)

val LightAppColors = AppColors(
    mainBackground = MainBackgroundLight,
    layoutBackground = LayoutBackgroundLight,
    chatBackground = ChatBackgroundLight,
    searchField = SearchFieldLight,
    strokeColor = StrokeColorLight,
    textColor = TextColorLight
)

private val DarkColorScheme = darkColorScheme(
    background = LayoutBackgroundDark,
    surface = MainBackgroundDark,
    onSurface = TextColorDark,
    outline = StrokeColorDark
)

private val LightColorScheme = lightColorScheme(
    background = LayoutBackgroundLight,
    surface = MainBackgroundLight,
    onSurface = TextColorLight,
    outline = StrokeColorLight
)

val LocalAppColors = staticCompositionLocalOf { LightAppColors }

object AppTheme {
    val colors: AppColors
        @Composable
        get() = LocalAppColors.current
}

val ColorScheme.searchField: Color
    @Composable
    get() = if (isSystemInDarkTheme()) SearchFieldDark else SearchFieldLight

val ColorScheme.chatBackground: Color
    @Composable
    get() = if (isSystemInDarkTheme()) ChatBackgroundDark else ChatBackgroundLight

@Composable
fun MessengerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    //Цвета, не зависящие от dynamic color
    val appColors = if (darkTheme) DarkAppColors else LightAppColors

    CompositionLocalProvider(LocalAppColors provides appColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
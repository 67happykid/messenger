package com.example.messenger.ui.navigation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.example.messenger.ui.screens.PrivateMessagesScreen
import com.example.messenger.ui.screens.CallScreen

enum class Screen {
    MessageScreen,
    VoiceScreen
}

@Composable
fun PrivateChatNavigation(
    inviteToken: String?
) {
    val startScreen = if (inviteToken != null && inviteToken.take(4) == "call")
        Screen.VoiceScreen
    else Screen.MessageScreen
    var currentScreen by rememberSaveable { mutableStateOf(startScreen) }

    AnimatedContent(
        targetState = currentScreen,
        transitionSpec = {
//            (slideInHorizontally { width -> width } + fadeIn()).togetherWith(
//                slideOutHorizontally { width -> -width } + fadeOut()
//            )

            (slideInVertically { height -> height } + fadeIn()).togetherWith(
                slideOutVertically { height -> height } + fadeOut()
            )
        },
        label = "ScreenSwitchAnimation"
    ) { targetScreen ->
        when (targetScreen) {
            Screen.MessageScreen -> PrivateMessagesScreen(
                onCallClick = { currentScreen = Screen.VoiceScreen },
                onVideocallClick = { currentScreen = Screen.VoiceScreen }
            )
            Screen.VoiceScreen -> CallScreen(
                onBackClick = { currentScreen = Screen.MessageScreen },
                onCallOffClick = { currentScreen = Screen.MessageScreen }
            )
        }
    }
}
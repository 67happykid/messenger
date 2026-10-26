package com.example.messenger

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.messenger.ui.screens.MainScreen
import com.example.messenger.ui.theme.MessengerTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import com.example.messenger.ui.screens.ErrorScreen
import com.example.messenger.ui.screens.LoadingScreen
import kotlin.random.Random

class MainActivity : ComponentActivity() {

    private var inviteToken by mutableStateOf<String?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        handleIntent(intent)

        setContent {
            MessengerTheme {
                /*val screenNumber = Random.nextInt(3)
                when (screenNumber) {
                    0 -> MainScreen(inviteToken)
                    1 -> LoadingScreen()
                    2 -> ErrorScreen()
                }*/
                MainScreen(inviteToken = inviteToken)
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        if (intent?.action == Intent.ACTION_VIEW) {
            val data = intent.data
            if (data != null && data.scheme == "messenger" && data.host == "invite") {
                inviteToken = data.getQueryParameter("token")
            }
        }
    }
}
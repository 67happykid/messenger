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
import android.widget.Toast
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.saveable.rememberSaveable
import com.example.messenger.ui.navigation.AppNavigation
import com.example.messenger.ui.theme.ThemeMod
import kotlinx.coroutines.selects.select
import com.example.messenger.data.settings.SettingsRepo
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.messenger.ui.settings.AppSettingsViewModel

class MainActivity : ComponentActivity() {

    private var inviteToken by mutableStateOf<String?>(null)

    private val settingsRepo by lazy {
        SettingsRepo(applicationContext)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        handleIntent(intent)

        setContent {
            val settingsViewModel: AppSettingsViewModel = viewModel(
                factory = AppSettingsViewModel.factory(settingsRepo)
            )

            val uiState by settingsViewModel.uiState.collectAsStateWithLifecycle()

            LaunchedEffect(uiState.errorMessage) {
                val message = uiState.errorMessage

                if (message != null) {
                    Toast.makeText(
                        this@MainActivity,
                        message,
                        Toast.LENGTH_SHORT
                    ).show()

                    settingsViewModel.onErrorShown()
                }
            }

            val settings = uiState.settings

            if (settings == null) {
                MessengerTheme {
                    LoadingScreen()
                }

                return@setContent
            }

            val systemDarkTheme = isSystemInDarkTheme()

            val useDarkTheme = when (settings.themeMod) {
                ThemeMod.SYSTEM -> systemDarkTheme
                ThemeMod.LIGHT -> false
                ThemeMod.DARK -> true
            }

            MessengerTheme(
                darkTheme = useDarkTheme,
                dynamicColor = settings.dynamicColor
            ) {
                AppNavigation(
                    inviteToken = inviteToken,

                    themeMod = settings.themeMod,
                    onThemeModChange = settingsViewModel::setThemeMod,

                    dynamicColor = settings.dynamicColor,
                    onDynamicColorChange = settingsViewModel::setDynamicColor,

                    onSettingsSectionClick = { section ->
                        Toast.makeText(
                            this@MainActivity,
                            "Раздел «${section.title}» пока не подключён",
                            Toast.LENGTH_SHORT
                        ).show()
                    },

                    onLogoutClick = {
                        Toast.makeText(
                            this@MainActivity,
                            "Выход из аккаунта пока не подключён",
                            Toast.LENGTH_SHORT
                        ).show()
                    },

                    onAccountAction = { action ->
                        Toast.makeText(
                            this@MainActivity,
                            "${action.title} пока не подключено",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                )
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
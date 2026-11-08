package com.example.messenger.ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import com.example.messenger.ui.screens.SettingsSection
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.example.messenger.ui.screens.MainScreen
import com.example.messenger.ui.screens.SettingsScreen
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import com.example.messenger.ui.screens.AccountAction
import com.example.messenger.ui.screens.AccountScreen
import com.example.messenger.ui.screens.AccountUIState
import com.example.messenger.ui.theme.ThemeMod
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import com.example.messenger.ui.screens.AppearanceScreen

@Composable
fun AppNavigation(
    inviteToken: String?,
    onSettingsSectionClick: (SettingsSection) -> Unit,
    onLogoutClick: () -> Unit,
    onAccountAction: (AccountAction) -> Unit,
    themeMod: ThemeMod,
    onThemeModChange: (ThemeMod) -> Unit,
    dynamicColor: Boolean,
    onDynamicColorChange: (Boolean) -> Unit
) {
    val backStack = rememberNavBackStack(MainRoute)

    val goBack: () -> Unit = {
        if (backStack.size > 1){
            backStack.removeAt(backStack.lastIndex)
        }
    }

    NavDisplay(
        modifier = Modifier
            .fillMaxSize(),
        backStack = backStack,
        onBack = goBack,
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),

        transitionSpec = {
            slideInHorizontally(
                initialOffsetX = { width -> width },
                animationSpec = tween(
                    durationMillis = 350,
                    easing = FastOutSlowInEasing
                )
            ) togetherWith slideOutHorizontally(
                targetOffsetX = { width -> -width / 4 },
                animationSpec = tween(
                    durationMillis = 350,
                    easing = FastOutSlowInEasing
                )
            )
        },

        popTransitionSpec = {
            slideInHorizontally(
                initialOffsetX = { width -> -width / 4 },
                animationSpec = tween(
                    durationMillis = 350,
                    easing = FastOutSlowInEasing
                )
            ) togetherWith slideOutHorizontally(
                targetOffsetX = { width -> width },
                animationSpec = tween(
                    durationMillis = 350,
                    easing = FastOutSlowInEasing
                )
            )
        },

        predictivePopTransitionSpec = { _ ->
            slideInHorizontally(
                initialOffsetX = { width -> -width / 4 },
                animationSpec = tween(durationMillis = 350)
            ) togetherWith slideOutHorizontally(
                targetOffsetX = { width -> width },
                animationSpec = tween(durationMillis = 350)
            )
        },

        entryProvider = entryProvider {
            entry <MainRoute> {
                MainScreen(
                    inviteToken = inviteToken,
                    onSettingsClick = {
                        if (backStack.lastOrNull() != SettingsRoute) {
                            backStack.add(SettingsRoute)
                        }
                    }
                )
            }

            entry<SettingsRoute> {
                SettingsScreen(
                    onBackClick = goBack,
                    onSectionClick = { section ->
                        when (section) {
                            SettingsSection.ACCOUNT -> {
                                if (backStack.lastOrNull() != AccountRoute) {
                                    backStack.add(AccountRoute)
                                }
                            }

                            SettingsSection.APPEARANCE -> {
                                if (backStack.lastOrNull() != AppearanceRoute) {
                                    backStack.add(AppearanceRoute)
                                }
                            }

                            else -> {
                                onSettingsSectionClick(section)
                            }
                        }
                    },
                    onLogoutClick = onLogoutClick
                )
            }

            entry<AccountRoute> {
                AccountScreen(
                    //Временные демонстрационные данные
                    state = AccountUIState(
                        username = "user_001",
                        email = "user@example.com",
                        phone = null
                    ),
                    onBackClick = goBack,
                    onAction = onAccountAction
                )
            }

            entry<AppearanceRoute> {
                AppearanceScreen(
                    themeMod = themeMod,
                    dynamicColor = dynamicColor,
                    onThemeModChange = onThemeModChange,
                    onDynamicColorChange = onDynamicColorChange,
                    onBackClick = goBack
                )
            }
        }
    )
}
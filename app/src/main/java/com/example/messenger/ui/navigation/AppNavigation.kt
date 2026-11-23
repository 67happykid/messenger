package com.example.messenger.ui.navigation

import android.widget.Toast
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import com.example.messenger.ui.screens.ErrorScreen
import com.example.messenger.ui.screens.LoadingScreen
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
import com.example.messenger.ui.theme.ThemeMod
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import com.example.messenger.ui.screens.AppearanceScreen
import com.example.messenger.data.user.DemoUserRepo
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.messenger.ui.settings.AccountViewModel
import androidx.compose.ui.platform.LocalContext
import com.example.messenger.ui.screens.EmptyScreen
import com.example.messenger.data.settings.PrivacySettings
import com.example.messenger.ui.screens.PrivacyScreen
import com.example.messenger.data.settings.SoundChange
import com.example.messenger.data.settings.SoundSettings
import com.example.messenger.ui.screens.SoundScreen
import com.example.messenger.data.settings.NotificationOption
import com.example.messenger.data.settings.NotificationSettings
import com.example.messenger.ui.screens.NotificationsScreen

@Composable
fun AppNavigation(
    inviteToken: String?,
    onSettingsSectionClick: (SettingsSection) -> Unit,
    onLogoutClick: () -> Unit,
    onAccountAction: (AccountAction) -> Unit,
    themeMod: ThemeMod,
    onThemeModChange: (ThemeMod) -> Unit,
    dynamicColor: Boolean,
    onDynamicColorChange: (Boolean) -> Unit,
    userRepo: DemoUserRepo,
    privacy: PrivacySettings,
    onShowOnlineStatusChange: (Boolean) -> Unit,
    onAllowFriendInvitesChange: (Boolean) -> Unit,
    sound: SoundSettings,
    onSoundChange: (SoundChange) -> Unit,
    notifications: NotificationSettings,
    onNotificationOptionChange: (NotificationOption, Boolean) -> Unit
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
                /*MainScreen(
                    inviteToken = inviteToken,
                    onSettingsClick = {
                        if (backStack.lastOrNull() != SettingsRoute) {
                            backStack.add(SettingsRoute)
                        }
                    }
                )*/
                val context = LocalContext.current

                EmptyScreen(
                    inviteToken = inviteToken,

                    onSettingsClick = {
                        if (backStack.lastOrNull() != SettingsRoute) {
                            backStack.add(SettingsRoute)
                        }
                    },

                    onCreateChatClick = {
                        Toast.makeText(
                            context,
                            "Создание чата пока не подключено",
                            Toast.LENGTH_SHORT
                        ).show()
                    },

                    onCreateChannelClick = {
                        Toast.makeText(
                            context,
                            "Создание канала пока не подключено",
                            Toast.LENGTH_SHORT
                        ).show()
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

                            SettingsSection.PRIVACY -> {
                                if (backStack.lastOrNull() != PrivacyRoute) {
                                    backStack.add(PrivacyRoute)
                                }
                            }

                            SettingsSection.SOUND -> {
                                if (backStack.lastOrNull() != SoundRoute) {
                                    backStack.add(SoundRoute)
                                }
                            }

                            SettingsSection.NOTIFICATIONS -> {
                                if (backStack.lastOrNull() != NotificationsRoute) {
                                    backStack.add(NotificationsRoute)
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
                val accountViewModel: AccountViewModel = viewModel(
                    factory = AccountViewModel.factory(userRepo)
                )

                val accountState by accountViewModel.uiState
                    .collectAsStateWithLifecycle()

                when {
                    accountState.isLoading -> {
                        LoadingScreen()
                    }

                    accountState.loadError != null -> {
                        ErrorScreen(
                            onRetryClick = accountViewModel::loadProfile
                        )
                    }

                    else -> {
                        AccountScreen(
                            state = accountState,
                            onBackClick = goBack,

                            onAction = { action ->
                                when (action) {
                                    AccountAction.CHANGE_USERNAME -> {
                                        accountViewModel.openUsernameEditor()
                                    }

                                    AccountAction.CHANGE_EMAIL -> {
                                        accountViewModel.openEmailEditor()
                                    }

                                    AccountAction.CHANGE_PHONE -> {
                                        accountViewModel.openPhoneEditor()
                                    }

                                    else -> {
                                        onAccountAction(action)
                                    }
                                }
                            },

                            onUsernameDraftChange =
                                accountViewModel::changeUsernameDraft,
                            onUsernameSave =
                                accountViewModel::saveUsername,
                            onUsernameDismiss =
                                accountViewModel::dismissUsernameEditor,

                            onEmailDraftChange =
                                accountViewModel::changeEmailDraft,
                            onEmailSave =
                                accountViewModel::saveEmail,
                            onEmailDismiss =
                                accountViewModel::dismissEmailEditor,

                            onPhoneDraftChange =
                                accountViewModel::changePhoneDraft,
                            onPhoneSave =
                                accountViewModel::savePhone,
                            onPhoneDismiss =
                                accountViewModel::dismissPhoneEditor
                        )
                    }
                }
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

            entry<PrivacyRoute> {
                PrivacyScreen(
                    settings = privacy,
                    onShowOnlineStatusChange = onShowOnlineStatusChange,
                    onAllowFriendInvitesChange = onAllowFriendInvitesChange,
                    onBackClick = goBack
                )
            }

            entry<SoundRoute> {
                SoundScreen(
                    settings = sound,
                    onSoundChange = onSoundChange,
                    onBackClick = goBack
                )
            }

            entry<NotificationsRoute> {
                NotificationsScreen(
                    settings = notifications,
                    onOptionChange = onNotificationOptionChange,
                    onBackClick = goBack
                )
            }
        }
    )
}
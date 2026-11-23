package com.example.messenger.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.example.messenger.ui.screens.ServerScreen
import com.example.messenger.ui.screens.ServerSettingsScreen
import com.example.messenger.ui.server.ServerAction
import com.example.messenger.ui.server.ServerViewModel
import kotlinx.serialization.Serializable

@Serializable
data object ServersRoute : NavKey

@Serializable
data class ServerSettingsRoute(val serverId: Int) : NavKey

@Composable
fun ServerNavigation(onProfileClick: () -> Unit = {}) {
    val viewModel: ServerViewModel = viewModel()
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val backStack = rememberNavBackStack(ServersRoute)
    val snackbarHostState = remember { SnackbarHostState() }

    fun goBack() {
        if (backStack.size > 1) {
            backStack.removeLastOrNull()
        }
    }

    LaunchedEffect(state.notice) {
        val message = state.notice ?: return@LaunchedEffect
        snackbarHostState.showSnackbar(message)
        viewModel.onAction(ServerAction.NoticeShown)
    }

    Box {
        NavDisplay(
            backStack = backStack,
            onBack = ::goBack,
            entryProvider = entryProvider {
                entry<ServersRoute> {
                    ServerScreen(
                        state = state,
                        onAction = viewModel::onAction,
                        onSettingsClick = { serverId ->
                            backStack.add(ServerSettingsRoute(serverId))
                        },
                        onProfileClick = onProfileClick
                    )
                }

                entry<ServerSettingsRoute> { route ->
                    val server = state.servers.firstOrNull {
                        it.id == route.serverId
                    }

                    ServerSettingsScreen(
                        server = server,
                        onAction = viewModel::onAction,
                        onBack = ::goBack
                    )
                }
            }
        )

        SnackbarHost(hostState = snackbarHostState)
    }
}

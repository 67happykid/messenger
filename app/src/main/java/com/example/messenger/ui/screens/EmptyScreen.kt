package com.example.messenger.ui.screens

import android.content.res.Configuration
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.messenger.ui.theme.MessengerTheme

@Composable
fun EmptyScreen(
    onCreateChatClick: () -> Unit,
    onCreateChannelClick: () -> Unit,
    inviteToken: String? = null,
    onSettingsClick: () -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    MainScreen(
        inviteToken = inviteToken,
        onSettingsClick = onSettingsClick,
        onProfileClick = onProfileClick,
        isChatListEmpty = true,
        isChannelListEmpty = true,
        onCreateChatClick = onCreateChatClick,
        onCreateChannelClick = onCreateChannelClick
    )
}

@Composable
fun EmptyListAction(
    actionText: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        if (maxWidth >= 160.dp) {
            FilledTonalButton(
                onClick = onClick,
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(
                    horizontal = 12.dp,
                    vertical = 12.dp
                )
            ) {
                Text(
                    text = actionText,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            FilledTonalIconButton(
                onClick = onClick,
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = actionText
                )
            }
        }
    }
}

@Preview(
    name = "Главный пустой - светлый",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    widthDp = 360,
    heightDp = 800
)
@Preview(
    name = "Главный пустой - тёмный",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    widthDp = 360,
    heightDp = 800
)
@Composable
private fun EmptyScreenPreview() {
    MessengerTheme(dynamicColor = false) {
        EmptyScreen(
            onCreateChatClick = {},
            onCreateChannelClick = {}
        )
    }
}
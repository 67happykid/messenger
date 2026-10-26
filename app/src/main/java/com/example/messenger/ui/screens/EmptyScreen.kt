package com.example.messenger.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
    onActionClick: () -> Unit,
    modifier: Modifier = Modifier,
    title: String = "Здесь пока пусто",
    message: String = "Добавьте друга, чтобы начать общение.",
    actionText: String = "Добавить друга"
) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
        contentColor = MaterialTheme.colorScheme.onBackground
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = 420.dp)
                    .verticalScroll(rememberScrollState())
                    .padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Chat,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(64.dp)
                )

                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                Button(onClick = onActionClick) {
                    Text(
                        text = actionText,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Preview(
    name = "Пустой экран - светлый",
    showBackground = true
)
@Composable
private fun EmptyScreenLightPreview() {
    MessengerTheme(
        darkTheme = false,
        dynamicColor = false
    ) {
        EmptyScreen(onActionClick = {})
    }
}

@Preview(
    name = "Пустой экран - тёмный",
    showBackground = true
)
@Composable
private fun EmptyScreenDarkPreview() {
    MessengerTheme(
        darkTheme = true,
        dynamicColor = false
    ) {
        EmptyScreen(onActionClick = {})
    }
}
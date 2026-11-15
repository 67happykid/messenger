package com.example.messenger.ui.screens

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.messenger.data.settings.NotificationOption
import com.example.messenger.data.settings.NotificationSettings
import com.example.messenger.ui.theme.MessengerTheme

@Composable
fun NotificationsScreen(
    settings: NotificationSettings,
    onOptionChange: (NotificationOption, Boolean) -> Unit,
    onBackClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
        contentColor = MaterialTheme.colorScheme.onBackground
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Назад"
                    )
                }

                Text(
                    text = "Уведомления",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.semantics { heading() }
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SettingsSwitchRow(
                    title = "Личные сообщения",
                    description = "Уведомлять о новых сообщениях в личных чатах.",
                    checked = settings.messages,
                    onCheckedChange = {
                        onOptionChange(NotificationOption.MESSAGES, it)
                    }
                )

                SettingsSwitchRow(
                    title = "Упоминания",
                    description = "Уведомлять, когда вас упоминают в канале.",
                    checked = settings.mentions,
                    onCheckedChange = {
                        onOptionChange(NotificationOption.MENTIONS, it)
                    }
                )

                SettingsSwitchRow(
                    title = "Входящие звонки",
                    description = "Показывать уведомления о входящих звонках.",
                    checked = settings.calls,
                    onCheckedChange = {
                        onOptionChange(NotificationOption.CALLS, it)
                    }
                )

                SettingsSwitchRow(
                    title = "Предпросмотр сообщений",
                    description = "Показывать текст сообщения в уведомлении.",
                    checked = settings.showPreview,
                    onCheckedChange = {
                        onOptionChange(NotificationOption.SHOW_PREVIEW, it)
                    }
                )
            }
        }
    }
}

@Preview(name = "Уведомления - светлая тема", showBackground = true)
@Preview(
    name = "Уведомления - тёмная тема",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun NotificationsScreenPreview() {
    MessengerTheme(dynamicColor = false) {
        NotificationsScreen(
            settings = NotificationSettings(),
            onOptionChange = { _, _ -> },
            onBackClick = {}
        )
    }
}
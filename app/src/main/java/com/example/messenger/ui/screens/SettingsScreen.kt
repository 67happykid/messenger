package com.example.messenger.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.messenger.ui.theme.MessengerTheme

//Разделы
enum class SettingsSection (
    val title: String,
    val description: String,
    val icon: ImageVector
) {
    ACCOUNT(
        title = "Аккаунт",
        description = "Данные аккаунта и безопасность входа",
        icon = Icons.Default.AccountCircle
    ),
    PRIVACY(
        title = "Конфиденциальность",
        description = "Активность, приглашения и блокировки",
        icon = Icons.Default.Security
    ),
    SOUND(
        title = "Звук",
        description = "Микрофон и параметры голосового общения",
        icon = Icons.Default.VolumeUp
    ),
    VIDEO(
        title = "Видео",
        description = "Камера и параметры видеозвонков",
        icon = Icons.Default.Videocam
    ),
    APPEARANCE(
        title = "Оформление",
        description = "Тема, системные цвета и размер текста",
        icon = Icons.Default.Palette
    ),
    NOTIFICATIONS(
        title = "Уведомления",
        description = "Сообщения, упоминания и звонки",
        icon = Icons.Default.Notifications
    ),
    ABOUT(
        title = "О приложении",
        description = "Версия приложения и команда разработки",
        icon = Icons.Default.Info
    )
}

//Общие размеры элементов экрана
private object SettingsDimensions {
    val screenPadding = 16.dp
    val itemSpacing = 8.dp
    val contentSpacing = 12.dp
    val textSpacing = 4.dp
    val sectionSpacing = 16.dp
    val cornerRadius = 16.dp
    val iconSize = 24.dp
    val touchTarget = 48.dp
    val minimumRowHeight = 72.dp
}

private data class SettingsGroup(
    val id: String,
    val title: String,
    val sections: List<SettingsSection>
)

private val settingsGroups = listOf(
    SettingsGroup(
        id = "user",
        title = "Пользователь",
        sections = listOf(
            SettingsSection.ACCOUNT,
            SettingsSection.PRIVACY
        )
    ),

    SettingsGroup(
        id = "app",
        title = "Приложение",
        sections = listOf(
            SettingsSection.SOUND,
            SettingsSection.VIDEO,
            SettingsSection.APPEARANCE,
            SettingsSection.NOTIFICATIONS
        )
    ),

    SettingsGroup(
        id = "info",
        title = "Информация",
        sections = listOf(
            SettingsSection.ABOUT
        )
    )
)

@Composable
fun SettingsScreen (
    onBackClick: () -> Unit,
    onSectionClick: (SettingsSection) -> Unit,
    onLogoutClick: () -> Unit,
) {
    var showLogoutDialog by rememberSaveable { mutableStateOf(false) }

    Surface(
        modifier = Modifier
            .fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
        contentColor = MaterialTheme.colorScheme.onBackground
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .safeDrawingPadding()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = SettingsDimensions.itemSpacing,
                        vertical = SettingsDimensions.itemSpacing
                    ),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(
                    SettingsDimensions.itemSpacing
                )
            ) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier
                        .size(SettingsDimensions.touchTarget)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Назад"
                    )
                }

                Text(
                    text = "Настройки",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier
                        .weight(1f)
                        .semantics { heading() }
                )
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(
                    horizontal = SettingsDimensions.screenPadding,
                    vertical = SettingsDimensions.itemSpacing
                ),
                verticalArrangement = Arrangement.spacedBy(
                    SettingsDimensions.itemSpacing
                )
            ) {
                for (group in settingsGroups) {
                    item(key = "group_${group.id}") {
                        SettingsGroupHeading(
                            text = group.title
                        )
                    }

                    items(
                        items = group.sections,
                        key = { section -> section.name }
                    ) { section ->
                        SettingsSectionCard(
                            section = section,
                            onClick = {
                                onSectionClick(section)
                            }
                        )
                    }
                }

                item(key = "logout") {
                    TextButton(
                        onClick = {
                            showLogoutDialog = true
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                top = SettingsDimensions.sectionSpacing
                            )
                            .heightIn(
                                min = SettingsDimensions.touchTarget
                            ),
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text(
                            text = "Выйти из аккаунта",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            }
        }
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = {
                showLogoutDialog = false
            },
            title = {
                Text("Выйти из аккаунта")
            },
            text = {
                Text("Для продолжения общения потребуется снова войти")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showLogoutDialog = false
                        onLogoutClick()
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Выйти")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showLogoutDialog = false
                    }
                ) {
                    Text("Отмена")
                }
            }
        )
    }
}

@Composable
private fun SettingsGroupHeading(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = modifier
            .padding(
                top = SettingsDimensions.sectionSpacing,
                bottom = SettingsDimensions.itemSpacing
            )
            .semantics { heading() }
    )
}

@Composable
private fun SettingsSectionCard(
    section: SettingsSection,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(SettingsDimensions.cornerRadius),
        color = MaterialTheme.colorScheme.surfaceContainer,
        contentColor = MaterialTheme.colorScheme.onSurface
    ) {
        Row(
            modifier = Modifier
                .heightIn(
                    min = SettingsDimensions.minimumRowHeight
                )
                .padding(SettingsDimensions.screenPadding),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(
                SettingsDimensions.contentSpacing
            )
        ) {
            Icon(
                imageVector = section.icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .size(SettingsDimensions.iconSize)
            )

            Column(
                modifier = Modifier
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(SettingsDimensions.textSpacing)
            ) {
                Text(
                    text = section.title,
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    text = section.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Icon(
                imageVector =
                    Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(
                    SettingsDimensions.iconSize
                )
            )
        }
    }
}

@Preview(
    name = "Настройки — светлая тема",
    showBackground = true
)
@Composable
private fun SettingsScreenLightPreview() {
    MessengerTheme(
        darkTheme = false,
        dynamicColor = false
    ) {
        SettingsScreen(
            onBackClick = {},
            onSectionClick = {},
            onLogoutClick = {}
        )
    }
}

@Preview(
    name = "Настройки — тёмная тема",
    showBackground = true
)
@Composable
private fun SettingsScreenDarkPreview() {
    MessengerTheme(
        darkTheme = true,
        dynamicColor = false
    ) {
        SettingsScreen(
            onBackClick = {},
            onSectionClick = {},
            onLogoutClick = {}
        )
    }
}
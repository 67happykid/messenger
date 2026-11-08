package com.example.messenger.ui.screens

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.pm.ShortcutInfoCompat
import com.example.messenger.ui.theme.MessengerTheme

data class AccountUIState(
    val username: String,
    val email: String? = null,
    val phone: String? = null
)

enum class AccountAction(val title: String) {
    CHANGE_USERNAME("Изменение имени пользователя"),
    CHANGE_EMAIL("Изменение электронной почты"),
    CHANGE_PHONE("Изменение телефона"),
    CHANGE_PASSWORD("Изменение пароля")
}

private object AccountDimensions {
    val screenPadding = 16.dp
    val itemSpacing = 12.dp
    val textSpacing = 4.dp
    val sectionSpacing = 8.dp
    val cornerRadius = 16.dp
    val iconSize = 24.dp
    val touchTarget = 48.dp
    val minimumRowHeight = 72.dp
}

@Composable
fun AccountScreen(
    state: AccountUIState,
    onBackClick: () -> Unit,
    onAction: (AccountAction) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = Modifier
            .fillMaxSize(),
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
                    .padding(AccountDimensions.sectionSpacing),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(
                    AccountDimensions.sectionSpacing
                )
            ) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier.size(
                        AccountDimensions.touchTarget
                    )
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Назад"
                    )
                }

                Text(
                    text = "Аккаунт",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier
                        .weight(1f)
                        .semantics { heading() }
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(AccountDimensions.screenPadding),
                verticalArrangement = Arrangement.spacedBy(
                    AccountDimensions.itemSpacing
                )
            ) {
                AccountSectionHeading(
                    text = "Данные учётной записи"
                )

                AccountSettingCard(
                    title = "Имя пользователя",
                    value = state.username.ifBlank {
                        "Не указано"
                    },
                    icon = Icons.Default.Person,
                    onClick = {
                        onAction(AccountAction.CHANGE_USERNAME)
                    }
                )

                AccountSettingCard(
                    title = "Электронная почта",
                    value = state.email
                        ?.takeIf { it.isNotBlank() }
                        ?: "Не привязана",
                    icon = Icons.Default.Email,
                    onClick = {
                        onAction(AccountAction.CHANGE_EMAIL)
                    }
                )

                AccountSettingCard(
                    title = "Телефон",
                    value = state.phone
                        ?.takeIf { it.isNotBlank() }
                        ?: "Не привязан",
                    icon = Icons.Default.Phone,
                    onClick = {
                        onAction(AccountAction.CHANGE_PHONE)
                    }
                )

                AccountSectionHeading(
                    text = "Безопасность"
                )

                AccountSettingCard(
                    title = "Изменить пароль",
                    value = "Обновить пароль для входа",
                    icon = Icons.Default.Lock,
                    onClick = {
                        onAction(AccountAction.CHANGE_PASSWORD)
                    }
                )
            }
        }
    }
}

@Composable
private fun AccountSectionHeading(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = modifier
            .padding(top = AccountDimensions.sectionSpacing)
            .semantics { heading() }
    )
}

@Composable
private fun AccountSettingCard(
    title: String,
    value: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(
            AccountDimensions.cornerRadius
        ),
        color = MaterialTheme.colorScheme.surfaceContainer,
        contentColor = MaterialTheme.colorScheme.onSurface
    ) {
        Row(
            modifier = Modifier
                .heightIn(
                    min = AccountDimensions.minimumRowHeight
                )
                .padding(AccountDimensions.screenPadding),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(
                AccountDimensions.itemSpacing
            )
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(
                    AccountDimensions.iconSize
                )
            )

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(
                    AccountDimensions.textSpacing
                )
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    text = value,
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
                    AccountDimensions.iconSize
                )
            )
        }
    }
}

@Preview(
    name = "Аккаунт - светлая тема",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    widthDp = 360,
    heightDp = 800
)
@Preview(
    name = "Аккаунт - тёмная тема",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    widthDp = 360,
    heightDp = 800
)
@Composable
private fun AccountScreenPreview() {
    MessengerTheme(dynamicColor = false) {
        AccountScreen(
            state = AccountUIState(
                username = "user_001",
                email = "user@example.com",
                phone = null
            ),
            onBackClick = {},
            onAction = {}
        )
    }
}
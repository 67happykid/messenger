package com.example.messenger.ui.screens

import android.os.Build
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.messenger.ui.theme.MessengerTheme
import com.example.messenger.ui.theme.ThemeMod

@Composable
fun AppearanceScreen(
    themeMod: ThemeMod,
    dynamicColor: Boolean,
    onThemeModChange: (ThemeMod) -> Unit,
    onDynamicColorChange: (Boolean) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val supportsDynamicColor =
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.S

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
        contentColor = MaterialTheme.colorScheme.onBackground
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
        ) {
            //Заголовок и возврат
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBackClick
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Назад"
                    )
                }

                Text(
                    text = "Оформление",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier
                        .weight(1f)
                        .semantics { heading() }
                )
            }

            //Прокручиваемое содержимое
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Тема приложения",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.semantics { heading() }
                )

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surfaceContainer,
                    contentColor = MaterialTheme.colorScheme.onSurface
                ) {
                    Column(
                        modifier = Modifier.selectableGroup()
                    ) {
                        for (mode in ThemeMod.entries) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(min = 56.dp)
                                    .selectable(
                                        selected = themeMod == mode,
                                        role = Role.RadioButton,
                                        onClick = {
                                            onThemeModChange(mode)
                                        }
                                    )
                                    .padding(
                                        horizontal = 16.dp,
                                        vertical = 12.dp
                                    ),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement =
                                    Arrangement.spacedBy(12.dp)
                            ) {
                                RadioButton(
                                    selected = themeMod == mode,
                                    onClick = null
                                )

                                Text(
                                    text = mode.title,
                                    style = MaterialTheme.typography.bodyLarge,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }

                Text(
                    text = "При выборе темы системы приложение следует " +
                            "настройкам телефона.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Text(
                    text = "Цвета",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.semantics { heading() }
                )

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surfaceContainer,
                    contentColor = MaterialTheme.colorScheme.onSurface
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 72.dp)
                            .toggleable(
                                value = supportsDynamicColor && dynamicColor,
                                enabled = supportsDynamicColor,
                                role = Role.Switch,
                                onValueChange = onDynamicColorChange
                            )
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "Динамические цвета",
                                style = MaterialTheme.typography.titleMedium
                            )

                            Text(
                                text = if (supportsDynamicColor) {
                                    "Использовать палитру из обоев телефона"
                                } else {
                                    "Доступно начиная с Android 12"
                                },
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Switch(
                            checked = supportsDynamicColor && dynamicColor,
                            onCheckedChange = null,
                            enabled = supportsDynamicColor
                        )
                    }
                }

                Text(
                    text = "Изменения применяются автоматически " +
                            "Кнопка сохранения не требуется",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Preview(
    name = "Оформление - светлая тема",
    showBackground = true,
    widthDp = 360,
    heightDp = 800
)
@Composable
private fun AppearanceScreenLightPreview() {
    MessengerTheme(
        darkTheme = false,
        dynamicColor = false
    ) {
        AppearanceScreen(
            themeMod = ThemeMod.LIGHT,
            dynamicColor = false,
            onThemeModChange = {},
            onDynamicColorChange = {},
            onBackClick = {}
        )
    }
}

@Preview(
    name = "Оформление - тёмная тема",
    showBackground = true,
    widthDp = 360,
    heightDp = 800
)
@Composable
private fun AppearanceScreenDarkPreview() {
    MessengerTheme(
        darkTheme = true,
        dynamicColor = false
    ) {
        AppearanceScreen(
            themeMod = ThemeMod.DARK,
            dynamicColor = false,
            onThemeModChange = {},
            onDynamicColorChange = {},
            onBackClick = {}
        )
    }
}
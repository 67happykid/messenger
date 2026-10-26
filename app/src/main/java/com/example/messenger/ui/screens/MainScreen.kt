package com.example.messenger.ui.screens

import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.core.animateDpAsState
import com.example.messenger.util.rememberNetworkStatus
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.material.icons.filled.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import com.example.messenger.R
import com.example.messenger.ui.theme.AppTheme
import com.example.messenger.ui.theme.chatBackground

@Composable
fun MainScreen(
    inviteToken: String?,
    onSettingsClick: () -> Unit = {},
    onProfileClick: () -> Unit = {}
){
    if (inviteToken != null) {
        //Обрабатываем переход по ссылке-приглашению в друзья
    }

    //Текст в поле поиска чатов
    var searchQuery by rememberSaveable { mutableStateOf("") }

    //Состояние, расширена ли область серверов (по умолчанию false)
    var isServersExpanded by rememberSaveable { mutableStateOf(false) }

    //Состояние, открыта ли область друзей
    var isFriendsOpen by rememberSaveable { mutableStateOf(false) }

    //Статус подключения к интернету
    val isConnected = rememberNetworkStatus()

    //Задний фон
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppTheme.colors.mainBackground)
    ) {
        //Все области
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .padding(horizontal = 16.dp)
                .padding(bottom = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            //Верхняя область
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(AppTheme.colors.layoutBackground)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                //Поле поиска
                Row(
                    modifier = Modifier
                        .width(160.dp)
                        .height(36.dp)
                        .clip(RoundedCornerShape(32.dp))
                        .background(AppTheme.colors.searchField)
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    //Иконка поиска
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = AppTheme.colors.textColor,
                        modifier = Modifier
                            .size(16.dp)
                    )

                    //Поле поиска чатов
                    BasicTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        singleLine = true,
                        textStyle = TextStyle(color = AppTheme.colors.textColor, fontSize = 13.sp),
                        modifier = Modifier
                            .fillMaxWidth(),
                        decorationBox = { innerTextField ->
                            Box(contentAlignment = Alignment.CenterStart) {
                                if (searchQuery.isEmpty()) {
                                    Text(
                                        text = "Search",
                                        color = AppTheme.colors.textColor,
                                        style = MaterialTheme.typography.bodyMedium,
                                        maxLines = 1
                                    )
                                }
                                innerTextField()
                            }
                        }
                    )
                }

                //Распорка, чтобы прижать вправо
                Spacer(modifier = Modifier.weight(1f))

                //Кнопка друзей
                Box {
                    IconButton(
                        onClick = { isFriendsOpen = true },
                        modifier = Modifier
                            .size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.People,
                            contentDescription = "Friends",
                            tint = AppTheme.colors.textColor,
                            modifier = Modifier
                                .size(24.dp)
                        )
                    }

                    //Выпадающее окно
                    DropdownMenu(
                        expanded = isFriendsOpen,
                        onDismissRequest = { isFriendsOpen = false },
                        modifier = Modifier
                            .width(280.dp)
                            .shadow(30.dp)
                            .background(AppTheme.colors.layoutBackground)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "Friends",
                                color = AppTheme.colors.textColor,
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            )

                            //Прокручиваемый список друзей
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(260.dp)
                                    .verticalScroll(rememberScrollState()),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                repeat(12) { index ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(46.dp)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(MaterialTheme.colorScheme.chatBackground)
                                            .padding(horizontal = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(32.dp)
                                                .clip(CircleShape)
                                                .background(Color(0xFF5865F2)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = "F${index + 1}",
                                                color = AppTheme.colors.textColor,
                                                style = MaterialTheme.typography.titleMedium
                                            )
                                        }
                                        Column(
                                            modifier = Modifier.weight(1f),
                                            verticalArrangement = Arrangement.Center
                                        ) {
                                            Text(
                                                text = "Friend Name ${index + 1}",
                                                color = AppTheme.colors.textColor,
                                                style = MaterialTheme.typography.bodyMedium,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            Text(
                                                text = if (index % 2 == 0) "В сети" else "Был недавно",
                                                color = if (index % 2 == 0) Color(0xFF57F287)
                                                    else MaterialTheme.colorScheme.onSurface.copy(
                                                    alpha = 0.5f
                                                ),
                                                style = MaterialTheme.typography.labelSmall
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(2.dp))

                            //Кнопка добавления друзей
                            val context = LocalContext.current
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(42.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color(0xFF5865F2))
                                    .clickable {
                                        isFriendsOpen = false
                                        val inviteLink =
                                            "messenger://messenger.app/invite?token=${System.currentTimeMillis()}&expires=1h"
                                        val sendIntent = Intent().apply {
                                            action = Intent.ACTION_SEND
                                            putExtra(
                                                Intent.EXTRA_TEXT,
                                                "Привет! Добавь меня в друзья. Ссылка действительна 1 час:\n$inviteLink"
                                            )
                                            type = "text/plain"
                                        }
                                        val shareIntent = Intent.createChooser(
                                            sendIntent,
                                            "Пригласить друга через"
                                        )
                                        context.startActivity(shareIntent)
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.PersonAdd,
                                        contentDescription = "Add Friend",
                                        tint = AppTheme.colors.textColor,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = stringResource(R.string.add_friend),
                                        color = AppTheme.colors.textColor,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                        }
                    }
                }

                //Иконка сети
                /*IconButton(
                    onClick = { isConnected = !isConnected},
                    modifier = Modifier
                        .size(24.dp)
                ) {
                    Icon(
                        imageVector = if (isConnected) Icons.Default.Wifi
                            else Icons.Default.WifiOff,
                        contentDescription = "Network Status",
                        tint = if (isConnected) DarkThemeTextColor
                            else LayoutBackground,
                        modifier = Modifier
                            .size(16.dp)
                    )
                }*/

                val ctx = LocalContext.current
                //Кнопка настроек
                IconButton(
                    onClick = { Toast.makeText(ctx, "Вас могнули... ?:)",
                        Toast.LENGTH_SHORT).show() },
                    modifier = Modifier
                        .size(36.dp)
                ) {
                    //Иконка настроек
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Settings",
                        tint = AppTheme.colors.textColor,
                        modifier = Modifier
                            .size(24.dp)
                    )
                }

                //Кнопка профиля
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFFFFFF))
                        .clickable { Toast.makeText(ctx, "Вас могнули... ?:)",
                            Toast.LENGTH_SHORT).show() }
                )
            }

            //Область панелей с фиксированием свайпа пальцем
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .pointerInput(Unit) {
                        detectHorizontalDragGestures { change, dragAmount ->
                            change.consume()
                            if (dragAmount > 20) {
                                isServersExpanded = true
                            } else if (dragAmount < -20) {
                                isServersExpanded = false
                            }
                        }
                    }
            ) {
                //Ширина свёрнутой панели
                val collapsedWidth = 72.dp

                //Ширина зазора
                val gap = 8.dp

                //Ширина развёрнутой панели
                val expandedWidth = maxWidth - collapsedWidth - gap

                // Анимация ширины левой панели (Серверы)
                val leftWidth by animateDpAsState(
                    targetValue = if (isServersExpanded) expandedWidth else collapsedWidth,
                    label = "leftPanelAnimation"
                )

                // Анимация ширины правой панели (Чаты)
                val rightWidth by animateDpAsState(
                    targetValue = if (isServersExpanded) collapsedWidth else expandedWidth,
                    label = "rightPanelAnimation"
                )

                //Две панели (Чатов и серверов)
                Row(
                    modifier = Modifier
                        .fillMaxSize(),
                    horizontalArrangement = Arrangement.spacedBy(gap)
                ) {
                    //Левая панель (Серверы)
                    Box(
                        modifier = Modifier
                            .width(leftWidth)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(16.dp))
                            .background(AppTheme.colors.layoutBackground)
                    ) {
                        //Вертикально прокручиваемый список серверов
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(vertical = 12.dp, horizontal = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            //Временная заглушка для проверки скрола на 15 серверах
                            items(15) { index ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(52.dp)
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(MaterialTheme.colorScheme.chatBackground)
                                        .padding(horizontal = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    // Иконка сервера (всегда фиксирована у левого края, центрирована при 72dp)
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(CircleShape)
                                            .background(
                                                if (index % 2 == 0) Color(0xFF5865F2) else Color(
                                                    0xFF57F287
                                                )
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "S${index + 1}",
                                            color = AppTheme.colors.textColor,
                                            style = MaterialTheme.typography.titleMedium
                                        )
                                    }

                                    //Название сервера и статус (видны при расширении)
                                    if (isServersExpanded) {
                                        Column(
                                            modifier = Modifier.weight(1f),
                                            verticalArrangement = Arrangement.Center
                                        ) {
                                            Text(
                                                text = "Server Community ${index + 1}",
                                                color = AppTheme.colors.textColor,
                                                style = MaterialTheme.typography.titleMedium,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            Text(
                                                text = "${(index + 1) * 14} members • online",
                                                color = AppTheme.colors.textColor.copy(alpha = 0.6f),
                                                style = MaterialTheme.typography.labelSmall,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    //Правая панель (Чаты)
                    Box(
                        modifier = Modifier
                            .width(rightWidth)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(16.dp))
                            .background(AppTheme.colors.layoutBackground)
                    ) {
                        //Вертикально прокручиваемый список чатов
                        LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(vertical = 12.dp, horizontal = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            //Временные заглушки для проверки скролла 20 чатов
                            items(20) { index ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(52.dp)
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(MaterialTheme.colorScheme.chatBackground)
                                        .padding(horizontal = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    // Аватар чата
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFFEB459E))
                                    )

                                    // Имя юзера и последнее сообщение (видны при расширении чатов)
                                    if (!isServersExpanded) {
                                        Column(
                                            modifier = Modifier.weight(1f),
                                            verticalArrangement = Arrangement.Center
                                        ) {
                                            Text(
                                                text = "User Name ${index + 1}",
                                                color = AppTheme.colors.textColor,
                                                style = MaterialTheme.typography.titleMedium,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            Text(
                                                text = "Last message text goes here...",
                                                color = AppTheme.colors.textColor.copy(alpha = 0.6f),
                                                style = MaterialTheme.typography.bodyMedium,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
package com.example.messenger.ui.screens

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.messenger.R
import com.example.messenger.ui.server.ChannelType
import com.example.messenger.ui.server.ServerAction
import com.example.messenger.ui.server.ServerChannel
import com.example.messenger.ui.server.ServerInfo
import com.example.messenger.ui.server.ServerUiState
import com.example.messenger.ui.theme.AppTheme
import com.example.messenger.ui.theme.MessengerTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.abs

@Composable
fun ServerScreen(
    state: ServerUiState,
    onAction: (ServerAction) -> Unit,
    onSettingsClick: (Int) -> Unit,
    onProfileClick: () -> Unit = {}
) {
    val selectedServer = state.selectedServer
    if (selectedServer == null) {
        EmptyServersScreen()
        return
    }

    val selectedChannel = state.selectedChannel
    val closeChannel = { onAction(ServerAction.CloseChannel) }
    val swipeThreshold = with(LocalDensity.current) { 48.dp.toPx() }

    BackHandler(enabled = selectedChannel != null) {
        closeChannel()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppTheme.colors.mainBackground)
            .systemBarsPadding()
            .imePadding()
            .padding(horizontal = 16.dp)
            .padding(bottom = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (selectedChannel == null) {
            ServerTopBar(
                server = selectedServer,
                searchQuery = state.searchQuery,
                membersOpen = state.isMembersOpen,
                onAction = onAction,
                onProfileClick = onProfileClick
            )

            ServerPanels(
                state = state,
                server = selectedServer,
                swipeThreshold = swipeThreshold,
                onAction = onAction,
                onSettingsClick = onSettingsClick,
                modifier = Modifier.weight(1f)
            )
        } else {
            TextChannelHeader(
                channelName = selectedChannel.name,
                onBack = closeChannel
            )

            TextChannelMessages(
                serverName = selectedServer.name,
                channelName = selectedChannel.name,
                modifier = Modifier
                    .weight(1f)
                    .pointerInput(swipeThreshold) {
                        var dragDistance = 0f
                        detectHorizontalDragGestures(
                            onDragStart = { dragDistance = 0f },
                            onDragEnd = {
                                if (dragDistance >= swipeThreshold) closeChannel()
                                dragDistance = 0f
                            },
                            onDragCancel = { dragDistance = 0f },
                            onHorizontalDrag = { change, amount ->
                                change.consume()
                                dragDistance += amount
                            }
                        )
                    }
            )

            TextChannelComposer(
                value = state.messageDraft,
                onValueChange = { onAction(ServerAction.DraftChanged(it)) }
            )
        }
    }
}

@Composable
private fun EmptyServersScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppTheme.colors.mainBackground),
        contentAlignment = Alignment.Center
    ) {
        Text("Нет доступных серверов", color = AppTheme.colors.textColor)
    }
}

@Composable
private fun ServerTopBar(
    server: ServerInfo,
    searchQuery: String,
    membersOpen: Boolean,
    onAction: (ServerAction) -> Unit,
    onProfileClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(AppTheme.colors.layoutBackground)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ChannelSearchField(
            value = searchQuery,
            onValueChange = { onAction(ServerAction.SearchChanged(it)) },
            modifier = Modifier.weight(1f)
        )

        MembersButton(
            server = server,
            expanded = membersOpen,
            onExpandedChange = { onAction(ServerAction.MembersOpen(it)) }
        )

        // Верхняя шестерёнка пока остаётся без действия.
        IconButton(onClick = {}, modifier = Modifier.size(36.dp)) {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = "Общие настройки (пока не реализованы)",
                tint = AppTheme.colors.textColor,
                modifier = Modifier.size(24.dp)
            )
        }

        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(Color.White)
                .clickable(onClick = onProfileClick)
        )
    }
}

@Composable
private fun ChannelSearchField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .widthIn(max = 160.dp)
            .height(36.dp)
            .clip(RoundedCornerShape(32.dp))
            .background(AppTheme.colors.searchField)
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = "Поиск каналов",
            tint = AppTheme.colors.textColor,
            modifier = Modifier.size(16.dp)
        )

        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            textStyle = TextStyle(
                color = AppTheme.colors.textColor,
                fontSize = 13.sp
            ),
            modifier = Modifier.weight(1f),
            decorationBox = { textField ->
                Box(contentAlignment = Alignment.CenterStart) {
                    if (value.isEmpty()) {
                        Text(
                            text = stringResource(R.string.search),
                            color = AppTheme.colors.textColor,
                            style = MaterialTheme.typography.bodyMedium,
                            maxLines = 1
                        )
                    }
                    textField()
                }
            }
        )
    }
}

@Composable
private fun MembersButton(
    server: ServerInfo,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit
) {
    val configuration = LocalConfiguration.current
    val menuWidth = minOf(280.dp, (configuration.screenWidthDp.dp - 32.dp).coerceAtLeast(160.dp))
    val listHeight = minOf(260.dp, (configuration.screenHeightDp.dp - 180.dp).coerceAtLeast(96.dp))

    Box {
        IconButton(
            onClick = { onExpandedChange(true) },
            modifier = Modifier.size(36.dp)
        ) {
            Icon(
                imageVector = Icons.Default.People,
                contentDescription = "Участники сервера",
                tint = AppTheme.colors.textColor,
                modifier = Modifier.size(24.dp)
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { onExpandedChange(false) },
            modifier = Modifier
                .width(menuWidth)
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
                    text = "Members · ${server.memberCount}",
                    color = AppTheme.colors.textColor,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = server.name,
                    color = AppTheme.colors.textColor.copy(alpha = 0.6f),
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.padding(horizontal = 8.dp),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = listHeight),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(server.members, key = { it.id }) { member ->
                        MemberPreviewRow(member.name, member.id)
                    }
                }
            }
        }
    }
}

@Composable
private fun MemberPreviewRow(name: String, memberId: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(46.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(AppTheme.colors.chatBackground)
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
                text = "M$memberId",
                color = AppTheme.colors.textColor,
                style = MaterialTheme.typography.labelSmall,
                maxLines = 1
            )
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = name,
                color = AppTheme.colors.textColor,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = if (memberId % 2 == 1) "В сети" else "Был недавно",
                color = if (memberId % 2 == 1) Color(0xFF57F287)
                else AppTheme.colors.textColor.copy(alpha = 0.5f),
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}

@Composable
private fun ServerPanels(
    state: ServerUiState,
    server: ServerInfo,
    swipeThreshold: Float,
    onAction: (ServerAction) -> Unit,
    onSettingsClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val filteredChannels = server.channels.filter { channel ->
        channel.name.contains(state.searchQuery.trim(), ignoreCase = true)
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .pointerInput(swipeThreshold) {
                var dragDistance = 0f
                detectHorizontalDragGestures(
                    onDragStart = { dragDistance = 0f },
                    onDragEnd = {
                        if (abs(dragDistance) >= swipeThreshold) {
                            onAction(ServerAction.ServersExpanded(dragDistance > 0f))
                        }
                        dragDistance = 0f
                    },
                    onDragCancel = { dragDistance = 0f },
                    onHorizontalDrag = { change, amount ->
                        change.consume()
                        dragDistance += amount
                    }
                )
            }
    ) {
        val gap = 8.dp
        val collapsedWidth = minOf(72.dp, ((maxWidth - gap) / 2).coerceAtLeast(0.dp))
        val expandedWidth = (maxWidth - collapsedWidth - gap).coerceAtLeast(0.dp)
        val serverPanelWidth = if (state.isServersExpanded) expandedWidth else collapsedWidth
        val channelPanelWidth = if (state.isServersExpanded) collapsedWidth else expandedWidth

        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(gap)
        ) {
            ServerList(
                servers = state.servers,
                expanded = state.isServersExpanded,
                onSelect = { onAction(ServerAction.SelectServer(it)) },
                modifier = Modifier.width(serverPanelWidth)
            )

            ChannelList(
                server = server,
                channels = filteredChannels,
                expanded = state.isServersExpanded,
                onSelectChannel = { onAction(ServerAction.SelectChannel(it)) },
                onSettingsClick = onSettingsClick,
                modifier = Modifier.width(channelPanelWidth)
            )
        }
    }
}

@Composable
private fun ServerList(
    servers: List<ServerInfo>,
    expanded: Boolean,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxHeight()
            .clip(RoundedCornerShape(16.dp))
            .background(AppTheme.colors.layoutBackground),
        contentPadding = PaddingValues(vertical = 12.dp, horizontal = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(servers, key = { it.id }) { server ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(AppTheme.colors.chatBackground)
                    .clickable { onSelect(server.id) }
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ServerListAvatar(server)

                if (expanded) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = server.name,
                            color = AppTheme.colors.textColor,
                            style = MaterialTheme.typography.titleMedium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "${server.memberCount} members • online",
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

@Composable
private fun ServerListAvatar(server: ServerInfo) {
    val context = LocalContext.current
    val image by produceState<Bitmap?>(initialValue = null, server.avatarUri) {
        value = withContext(Dispatchers.IO) {
            server.avatarUri?.let { address ->
                runCatching {
                    context.contentResolver.openInputStream(Uri.parse(address))?.use {
                        BitmapFactory.decodeStream(it)
                    }
                }.getOrNull()
            }
        }
    }

    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(
                if (server.id % 2 == 1) Color(0xFF5865F2) else Color(0xFF57F287)
            ),
        contentAlignment = Alignment.Center
    ) {
        if (image != null) {
            Image(
                bitmap = image!!.asImageBitmap(),
                contentDescription = "Аватар ${server.name}",
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Text(
                text = server.name.take(2).uppercase(),
                color = AppTheme.colors.textColor,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun ChannelList(
    server: ServerInfo,
    channels: List<ServerChannel>,
    expanded: Boolean,
    onSelectChannel: (Int) -> Unit,
    onSettingsClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxHeight()
            .clip(RoundedCornerShape(16.dp))
            .background(AppTheme.colors.layoutBackground)
    ) {
        if (!expanded) {
            ChannelListHeader(server, onSettingsClick)
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(vertical = 12.dp, horizontal = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (channels.isEmpty() && !expanded) {
                item {
                    Text(
                        text = "Каналы не найдены",
                        color = AppTheme.colors.textColor.copy(alpha = 0.6f),
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }

            channels.groupBy { it.category }.forEach { (category, group) ->
                if (!expanded) {
                    item(key = "section-$category") {
                        Text(
                            text = category,
                            color = AppTheme.colors.textColor.copy(alpha = 0.6f),
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(start = 8.dp, top = 4.dp, bottom = 2.dp),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                items(group, key = { it.id }) { channel ->
                    ChannelListRow(
                        channel = channel,
                        expanded = expanded,
                        onClick = { onSelectChannel(channel.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun ChannelListHeader(
    server: ServerInfo,
    onSettingsClick: (Int) -> Unit
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = server.name,
                color = AppTheme.colors.textColor,
                style = MaterialTheme.typography.titleLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )

            IconButton(
                onClick = {
                    val inviteLink = "messenger-demo://server/${server.id}"
                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, inviteLink)
                    }
                    context.startActivity(
                        Intent.createChooser(shareIntent, "Поделиться сервером")
                    )
                },
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = "Поделиться сервером",
                    tint = AppTheme.colors.textColor,
                    modifier = Modifier.size(24.dp)
                )
            }

            IconButton(
                onClick = { onSettingsClick(server.id) },
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Настройки сервера",
                    tint = AppTheme.colors.textColor,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        Text(
            text = "${server.memberCount} members",
            color = AppTheme.colors.textColor.copy(alpha = 0.6f),
            style = MaterialTheme.typography.labelSmall
        )
    }
}

@Composable
private fun ChannelListRow(
    channel: ServerChannel,
    expanded: Boolean,
    onClick: () -> Unit
) {
    val isVoice = channel.type == ChannelType.VOICE

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(AppTheme.colors.chatBackground)
            .clickable(enabled = !isVoice, onClick = onClick)
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(if (isVoice) Color(0xFF57F287) else Color(0xFF5865F2)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (isVoice) Icons.Default.VolumeUp else Icons.Default.Tag,
                contentDescription = if (isVoice) "Голосовой канал" else "Текстовый канал",
                tint = Color.White,
                modifier = Modifier.size(22.dp)
            )
        }

        if (!expanded) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = channel.name,
                    color = AppTheme.colors.textColor,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = if (isVoice) "Голосовой канал" else "Текстовый канал",
                    color = AppTheme.colors.textColor.copy(alpha = 0.6f),
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun TextChannelHeader(channelName: String, onBack: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(AppTheme.colors.layoutBackground)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        IconButton(onClick = onBack, modifier = Modifier.size(36.dp)) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Вернуться к каналам",
                tint = AppTheme.colors.textColor,
                modifier = Modifier.size(24.dp)
            )
        }
        Text(
            text = "# $channelName",
            color = AppTheme.colors.textColor,
            style = MaterialTheme.typography.titleMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )
        IconButton(onClick = {}, modifier = Modifier.size(36.dp)) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Поиск по сообщениям (макет)",
                tint = AppTheme.colors.textColor,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
private fun TextChannelMessages(
    serverName: String,
    channelName: String,
    modifier: Modifier = Modifier
) {
    val messages = sampleChannelMessages()

    LazyColumn(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(AppTheme.colors.layoutBackground),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item(key = "intro") {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Text(
                    text = "# $channelName",
                    color = AppTheme.colors.textColor,
                    style = MaterialTheme.typography.headlineSmall,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "Начало текстового канала на сервере $serverName",
                    color = AppTheme.colors.textColor.copy(alpha = 0.6f),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        items(messages, key = { it.id }) { message ->
            MessageRow(message)
        }
    }
}

@Composable
private fun MessageRow(message: SampleChatMessage) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(AppTheme.colors.chatBackground)
            .padding(10.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(message.avatarColor),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = message.author.take(1).uppercase(),
                color = Color.White,
                style = MaterialTheme.typography.titleMedium
            )
        }

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = message.author,
                    color = AppTheme.colors.textColor,
                    style = MaterialTheme.typography.titleSmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false)
                )
                Text(
                    text = message.time,
                    color = AppTheme.colors.textColor.copy(alpha = 0.55f),
                    style = MaterialTheme.typography.labelSmall
                )
            }
            Text(
                text = message.text,
                color = AppTheme.colors.textColor,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun TextChannelComposer(
    value: String,
    onValueChange: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(AppTheme.colors.layoutBackground)
            .padding(horizontal = 8.dp, vertical = 8.dp),
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        IconButton(onClick = {}, modifier = Modifier.size(40.dp)) {
            Icon(
                imageVector = Icons.Default.AttachFile,
                contentDescription = "Прикрепить фото или видео (макет)",
                tint = AppTheme.colors.textColor,
                modifier = Modifier.size(24.dp)
            )
        }

        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            textStyle = TextStyle(color = AppTheme.colors.textColor, fontSize = 14.sp),
            maxLines = 4,
            modifier = Modifier
                .weight(1f)
                .heightIn(min = 40.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(AppTheme.colors.searchField)
                .padding(horizontal = 12.dp, vertical = 10.dp),
            decorationBox = { textField ->
                Box(contentAlignment = Alignment.CenterStart) {
                    if (value.isEmpty()) {
                        Text(
                            text = "Написать сообщение...",
                            color = AppTheme.colors.textColor.copy(alpha = 0.6f),
                            style = MaterialTheme.typography.bodyMedium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    textField()
                }
            }
        )

        IconButton(
            onClick = {},
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color(0xFF5865F2))
        ) {
            Icon(
                imageVector = Icons.Default.Send,
                contentDescription = "Отправить сообщение (макет)",
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

private data class SampleChatMessage(
    val id: Int,
    val author: String,
    val time: String,
    val text: String,
    val avatarColor: Color
)

private fun sampleChannelMessages(): List<SampleChatMessage> = listOf(
    SampleChatMessage(
        id = 1,
        author = "Alexey",
        time = "12:50",
        text = "Заходит однажды в бар улитка и говорит:\n" +
            "— Можно виски с колой?\n" +
            "— Простите, но мы не обслуживаем улиток.\n" +
            "И бармен вышвырнул её за дверь.\n" +
            "Через неделю заходит опять эта улитка и спрашивает:\n" +
            "— Ну и нафига ты это сделал?!\n",
        avatarColor = Color(0xFFEB459E)
    ),
    SampleChatMessage(
        id = 2,
        author = "Ivan",
        time = "12:51",
        text = "Приемлемо",
        avatarColor = Color(0xFFEB459E)
    ),
    SampleChatMessage(
        id = 3,
        author = "Nikita",
        time = "20:41",
        text = "АХАХАХАХАХАХАХА",
        avatarColor = Color(0xFFEB459E)
    )
)

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun ServerScreenPreview() {
    MessengerTheme(dynamicColor = false) {
        ServerScreen(
            state = ServerUiState(),
            onAction = {},
            onSettingsClick = {}
        )
    }
}

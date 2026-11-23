package com.example.messenger.ui.screens

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.messenger.ui.server.ChannelType
import com.example.messenger.ui.server.ME
import com.example.messenger.ui.server.ServerAction
import com.example.messenger.ui.server.ServerChannel
import com.example.messenger.ui.server.ServerInfo
import com.example.messenger.ui.server.ServerMember
import com.example.messenger.ui.server.ServerRole
import com.example.messenger.ui.server.canManage
import com.example.messenger.ui.server.myRole
import com.example.messenger.ui.theme.AppTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun ServerSettingsScreen(
    server: ServerInfo?,
    onAction: (ServerAction) -> Unit,
    onBack: () -> Unit
) {
    if (server == null) {
        MissingServer(onBack = onBack)
        return
    }

    var activeDialog by remember(server.id) { mutableStateOf<SettingsDialog?>(null) }
    val myRole = server.myRole()

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { imageUri ->
        imageUri?.let {
            onAction(ServerAction.SetAvatar(server.id, it.toString()))
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppTheme.colors.mainBackground)
            .padding(16.dp)
    ) {
        SettingsHeader(onBack = onBack)

        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            item {
                ServerDetails(
                    server = server,
                    canEdit = myRole.canManage(),
                    onRename = {
                        activeDialog = SettingsDialog.Rename(server.name)
                    },
                    onChangeAvatar = {
                        imagePicker.launch("image/*")
                    }
                )
            }

            item {
                ChannelHeading(
                    canCreate = myRole.canManage(),
                    onCreate = {
                        activeDialog = SettingsDialog.ChannelEditor(channel = null)
                    }
                )
            }

            items(server.channels, key = { it.id }) { channel ->
                ChannelRow(
                    channel = channel,
                    canEdit = myRole.canManage(),
                    onEdit = {
                        activeDialog = SettingsDialog.ChannelEditor(channel)
                    }
                )
            }

            item {
                Text("Участники", color = AppTheme.colors.textColor)
            }

            items(server.members, key = { it.id }) { member ->
                MemberRow(
                    member = member,
                    myRole = myRole,
                    onChangeRole = {
                        activeDialog = SettingsDialog.ChooseRole(member)
                    },
                    onKick = {
                        activeDialog = SettingsDialog.Kick(member)
                    }
                )
            }

            item {
                ServerActions(
                    myRole = myRole,
                    onLeave = { activeDialog = SettingsDialog.Leave },
                    onDelete = { activeDialog = SettingsDialog.Delete }
                )
            }
        }
    }

    SettingsDialogs(
        server = server,
        dialog = activeDialog,
        onAction = onAction,
        onDismiss = { activeDialog = null },
        onShowDialog = { activeDialog = it },
        onBack = onBack
    )
}

@Composable
private fun MissingServer(onBack: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppTheme.colors.mainBackground),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Сервер недоступен", color = AppTheme.colors.textColor)
            TextButton(onClick = onBack) {
                Text("Назад")
            }
        }
    }
}

@Composable
private fun SettingsHeader(onBack: () -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        IconButton(onClick = onBack) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Назад",
                tint = AppTheme.colors.textColor
            )
        }
        Text("Настройки сервера", color = AppTheme.colors.textColor)
    }
}

@Composable
private fun ServerDetails(
    server: ServerInfo,
    canEdit: Boolean,
    onRename: () -> Unit,
    onChangeAvatar: () -> Unit
) {
    SettingsCard {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ServerSettingsAvatar(server)

            Column {
                Text(server.name, color = AppTheme.colors.textColor)
                Text(
                    text = "Ваша роль: ${server.myRole()?.name ?: "нет"}",
                    color = AppTheme.colors.textColor
                )
            }
        }

        if (canEdit) {
            TextButton(onClick = onRename) {
                Text("Изменить название")
            }
            TextButton(onClick = onChangeAvatar) {
                Text("Выбрать аватар")
            }
        }
    }
}

@Composable
private fun ServerSettingsAvatar(server: ServerInfo) {
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
            .size(56.dp)
            .clip(CircleShape)
            .background(AppTheme.colors.searchField),
        contentAlignment = Alignment.Center
    ) {
        if (image != null) {
            Image(
                bitmap = image!!.asImageBitmap(),
                contentDescription = "Аватар сервера",
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Text(
                text = server.name.take(2).uppercase(),
                color = AppTheme.colors.textColor
            )
        }
    }
}

@Composable
private fun ChannelHeading(canCreate: Boolean, onCreate: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text("Каналы", color = AppTheme.colors.textColor)

        if (canCreate) {
            TextButton(onClick = onCreate) {
                Text("Создать")
            }
        }
    }
}

@Composable
private fun ChannelRow(
    channel: ServerChannel,
    canEdit: Boolean,
    onEdit: () -> Unit
) {
    val channelIcon = if (channel.type == ChannelType.TEXT) "#" else "🔊"

    SettingsCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "$channelIcon ${channel.name}",
                modifier = Modifier.weight(1f),
                color = AppTheme.colors.textColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            if (canEdit) {
                TextButton(onClick = onEdit) {
                    Text("Изменить")
                }
            }
        }
    }
}

@Composable
private fun MemberRow(
    member: ServerMember,
    myRole: ServerRole?,
    onChangeRole: () -> Unit,
    onKick: () -> Unit
) {
    val isAnotherEditableMember = member.id != ME && member.role != ServerRole.OWNER
    val canChangeRole = isAnotherEditableMember && myRole == ServerRole.OWNER
    val canKick = isAnotherEditableMember && (
        myRole == ServerRole.OWNER ||
            (myRole == ServerRole.ADMIN && member.role == ServerRole.MEMBER)
        )

    SettingsCard {
        Text("${member.name} · ${member.role.name}", color = AppTheme.colors.textColor)

        if (canChangeRole) {
            TextButton(onClick = onChangeRole) {
                Text("Права")
            }
        }

        if (canKick) {
            TextButton(onClick = onKick) {
                Text("Исключить")
            }
        }
    }
}

@Composable
private fun ServerActions(
    myRole: ServerRole?,
    onLeave: () -> Unit,
    onDelete: () -> Unit
) {
    SettingsCard {
        if (myRole != ServerRole.OWNER) {
            TextButton(onClick = onLeave) {
                Text("Выйти с сервера")
            }
        }

        if (myRole == ServerRole.OWNER) {
            TextButton(onClick = onDelete) {
                Text("Удалить сервер")
            }
        }
    }
}

@Composable
private fun SettingsCard(content: @Composable () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(AppTheme.colors.layoutBackground)
                .padding(12.dp)
        ) {
            content()
        }
    }
}

@Composable
private fun SettingsDialogs(
    server: ServerInfo,
    dialog: SettingsDialog?,
    onAction: (ServerAction) -> Unit,
    onDismiss: () -> Unit,
    onShowDialog: (SettingsDialog) -> Unit,
    onBack: () -> Unit
) {
    when (dialog) {
        is SettingsDialog.Rename -> RenameServerDialog(
            serverId = server.id,
            initialName = dialog.initialName,
            onAction = onAction,
            onDismiss = onDismiss
        )

        is SettingsDialog.ChannelEditor -> ChannelEditorDialog(
            serverId = server.id,
            channel = dialog.channel,
            onAction = onAction,
            onDelete = { onShowDialog(SettingsDialog.DeleteChannel(it)) },
            onDismiss = onDismiss
        )

        is SettingsDialog.ChooseRole -> ChooseRoleDialog(
            member = dialog.member,
            onChoose = { role ->
                onShowDialog(SettingsDialog.ConfirmRole(dialog.member, role))
            },
            onDismiss = onDismiss
        )

        is SettingsDialog.ConfirmRole -> ConfirmationDialog(
            title = "Изменить роль ${dialog.member.name} на ${dialog.role.name}?",
            onConfirm = {
                onAction(ServerAction.ChangeRole(server.id, dialog.member.id, dialog.role))
                onDismiss()
            },
            onDismiss = onDismiss
        )

        is SettingsDialog.DeleteChannel -> ConfirmationDialog(
            title = "Удалить канал ${dialog.channel.name}?",
            onConfirm = {
                onAction(ServerAction.DeleteChannel(server.id, dialog.channel.id))
                onDismiss()
            },
            onDismiss = onDismiss
        )

        is SettingsDialog.Kick -> ConfirmationDialog(
            title = "Исключить ${dialog.member.name}?",
            onConfirm = {
                onAction(ServerAction.KickMember(server.id, dialog.member.id))
                onDismiss()
            },
            onDismiss = onDismiss
        )

        SettingsDialog.Leave -> ConfirmationDialog(
            title = "Выйти с сервера ${server.name}?",
            onConfirm = {
                onAction(ServerAction.LeaveServer(server.id))
                onDismiss()
                onBack()
            },
            onDismiss = onDismiss
        )

        SettingsDialog.Delete -> ConfirmationDialog(
            title = "Удалить сервер ${server.name}?",
            onConfirm = {
                onAction(ServerAction.DeleteServer(server.id))
                onDismiss()
                onBack()
            },
            onDismiss = onDismiss
        )

        null -> Unit
    }
}

@Composable
private fun RenameServerDialog(
    serverId: Int,
    initialName: String,
    onAction: (ServerAction) -> Unit,
    onDismiss: () -> Unit
) {
    var name by remember(initialName) { mutableStateOf(initialName) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Название сервера") },
        text = {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Название") },
                singleLine = true
            )
        },
        confirmButton = {
            TextButton(
                enabled = name.isNotBlank(),
                onClick = {
                    onAction(ServerAction.RenameServer(serverId, name))
                    onDismiss()
                }
            ) {
                Text("Сохранить")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Отмена")
            }
        }
    )
}

@Composable
private fun ChannelEditorDialog(
    serverId: Int,
    channel: ServerChannel?,
    onAction: (ServerAction) -> Unit,
    onDelete: (ServerChannel) -> Unit,
    onDismiss: () -> Unit
) {
    var name by remember(channel?.id) { mutableStateOf(channel?.name.orEmpty()) }
    var type by remember(channel?.id) {
        mutableStateOf(channel?.type ?: ChannelType.TEXT)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(if (channel == null) "Новый канал" else "Изменить канал")
        },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Название") },
                    singleLine = true
                )

                ChannelTypeOption(
                    label = "Текстовый",
                    selected = type == ChannelType.TEXT,
                    onClick = { type = ChannelType.TEXT }
                )
                ChannelTypeOption(
                    label = "Голосовой",
                    selected = type == ChannelType.VOICE,
                    onClick = { type = ChannelType.VOICE }
                )

                if (channel != null) {
                    TextButton(onClick = { onDelete(channel) }) {
                        Text("Удалить канал")
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                enabled = name.isNotBlank(),
                onClick = {
                    if (channel == null) {
                        onAction(ServerAction.CreateChannel(serverId, name, type))
                    } else {
                        onAction(ServerAction.EditChannel(serverId, channel.id, name, type))
                    }
                    onDismiss()
                }
            ) {
                Text("Сохранить")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Отмена")
            }
        }
    )
}

@Composable
private fun ChannelTypeOption(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        RadioButton(selected = selected, onClick = onClick)
        Text(text = label, modifier = Modifier.clickable(onClick = onClick))
    }
}

@Composable
private fun ChooseRoleDialog(
    member: ServerMember,
    onChoose: (ServerRole) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Права: ${member.name}") },
        text = {
            Column {
                Text("Выберите роль участника")
                TextButton(onClick = { onChoose(ServerRole.ADMIN) }) {
                    Text("Администратор")
                }
                TextButton(onClick = { onChoose(ServerRole.MEMBER) }) {
                    Text("Участник")
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Отмена")
            }
        }
    )
}

@Composable
private fun ConfirmationDialog(
    title: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = { Text("Продолжить?") },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Подтвердить")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Отмена")
            }
        }
    )
}

private sealed interface SettingsDialog {
    data class Rename(val initialName: String) : SettingsDialog
    data class ChannelEditor(val channel: ServerChannel?) : SettingsDialog
    data class ChooseRole(val member: ServerMember) : SettingsDialog
    data class ConfirmRole(val member: ServerMember, val role: ServerRole) : SettingsDialog
    data class DeleteChannel(val channel: ServerChannel) : SettingsDialog
    data class Kick(val member: ServerMember) : SettingsDialog
    data object Leave : SettingsDialog
    data object Delete : SettingsDialog
}

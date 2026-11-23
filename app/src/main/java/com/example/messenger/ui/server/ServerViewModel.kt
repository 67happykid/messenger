package com.example.messenger.ui.server

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

enum class ServerRole {
    OWNER,
    ADMIN,
    MEMBER
}

enum class ChannelType {
    TEXT,
    VOICE
}

data class ServerMember(
    val id: Int,
    val name: String,
    val role: ServerRole
)

data class ServerChannel(
    val id: Int,
    val name: String,
    val type: ChannelType,
    val category: String
)

data class ServerInfo(
    val id: Int,
    val name: String,
    val avatarUri: String? = null,
    val channels: List<ServerChannel>,
    val members: List<ServerMember>
) {
    val memberCount: Int
        get() = members.size
}

data class ServerUiState(
    val servers: List<ServerInfo> = createSampleServers(),
    val selectedServerId: Int? = 1,
    val selectedChannelId: Int? = null,
    val searchQuery: String = "",
    val messageDraft: String = "",
    val isServersExpanded: Boolean = false,
    val isMembersOpen: Boolean = false,
    val notice: String? = null
) {
    val selectedServer: ServerInfo?
        get() = servers.firstOrNull { it.id == selectedServerId }

    val selectedChannel: ServerChannel?
        get() = selectedServer?.channels?.firstOrNull { it.id == selectedChannelId }
}

sealed interface ServerAction {
    data class SelectServer(val id: Int) : ServerAction
    data class SelectChannel(val id: Int) : ServerAction
    data object CloseChannel : ServerAction

    data class SearchChanged(val value: String) : ServerAction
    data class DraftChanged(val value: String) : ServerAction
    data class  ServersExpanded(val value: Boolean) : ServerAction
    data class MembersOpen(val value: Boolean) : ServerAction

    data class RenameServer(val id: Int, val name: String) : ServerAction
    data class SetAvatar(val id: Int, val uri: String) : ServerAction

    data class CreateChannel(
        val serverId: Int,
        val name: String,
        val type: ChannelType
    ) : ServerAction

    data class EditChannel(
        val serverId: Int,
        val channelId: Int,
        val name: String,
        val type: ChannelType
    ) : ServerAction

    data class DeleteChannel(val serverId: Int, val channelId: Int) : ServerAction
    data class ChangeRole(val serverId: Int, val memberId: Int, val role: ServerRole) : ServerAction
    data class KickMember(val serverId: Int, val memberId: Int) : ServerAction
    data class LeaveServer(val serverId: Int) : ServerAction
    data class DeleteServer(val serverId: Int) : ServerAction
    data object NoticeShown : ServerAction
}

class ServerViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(ServerUiState())
    val uiState: StateFlow<ServerUiState> = _uiState.asStateFlow()

    fun onAction(action: ServerAction) {
        _uiState.update { currentState ->
            when (action) {
                is ServerAction.SelectServer -> currentState.selectServer(action.id)
                is ServerAction.SelectChannel -> currentState.selectChannel(action.id)
                ServerAction.CloseChannel -> currentState.closeChannel()

                is ServerAction.SearchChanged -> currentState.copy(searchQuery = action.value)
                is ServerAction.DraftChanged -> currentState.copy(messageDraft = action.value)
                is ServerAction.ServersExpanded -> currentState.copy(isServersExpanded = action.value)
                is ServerAction.MembersOpen -> currentState.copy(isMembersOpen = action.value)

                is ServerAction.RenameServer -> currentState.renameServer(action)
                is ServerAction.SetAvatar -> currentState.setAvatar(action)
                is ServerAction.CreateChannel -> currentState.createChannel(action)
                is ServerAction.EditChannel -> currentState.editChannel(action)
                is ServerAction.DeleteChannel -> currentState.deleteChannel(action)
                is ServerAction.ChangeRole -> currentState.changeRole(action)
                is ServerAction.KickMember -> currentState.kickMember(action)
                is ServerAction.LeaveServer -> currentState.leaveServer(action.serverId)
                is ServerAction.DeleteServer -> currentState.deleteServer(action.serverId)

                ServerAction.NoticeShown -> currentState.copy(notice = null)
            }
        }
    }
}

private fun ServerUiState.selectServer(serverId: Int): ServerUiState {
    if (servers.none { it.id == serverId }) return this

    return copy(
        selectedServerId = serverId,
        selectedChannelId = null,
        searchQuery = "",
        messageDraft = "",
        isServersExpanded = false,
        isMembersOpen = false
    )
}

private fun ServerUiState.selectChannel(channelId: Int): ServerUiState {
    val channel = selectedServer?.channels?.firstOrNull { it.id == channelId }
    if (channel?.type != ChannelType.TEXT) return this

    return copy(
        selectedChannelId = channelId,
        messageDraft = "",
        isServersExpanded = false
    )
}

private fun ServerUiState.closeChannel(): ServerUiState = copy(
    selectedChannelId = null,
    messageDraft = "",
    isServersExpanded = false
)

private fun ServerUiState.renameServer(action: ServerAction.RenameServer): ServerUiState {
    val name = action.name.trim()
    if (name.isEmpty()) return this

    return updateServer(action.id, minimumRole = ServerRole.ADMIN) { server ->
        server.copy(name = name.take(MAX_NAME_LENGTH))
    }
}

private fun ServerUiState.setAvatar(action: ServerAction.SetAvatar): ServerUiState {
    return updateServer(action.id, minimumRole = ServerRole.ADMIN) { server ->
        server.copy(avatarUri = action.uri)
    }
}

private fun ServerUiState.createChannel(action: ServerAction.CreateChannel): ServerUiState {
    val name = action.name.trim()
    if (name.isEmpty()) return this

    val nextId = maxOf(
        FIRST_NEW_CHANNEL_ID,
        (servers.flatMap { it.channels }.maxOfOrNull { it.id } ?: 0) + 1
    )
    val channel = ServerChannel(
        id = nextId,
        name = name.take(MAX_NAME_LENGTH),
        type = action.type,
        category = action.type.defaultCategory()
    )

    return updateServer(action.serverId, minimumRole = ServerRole.ADMIN) { server ->
        server.copy(channels = server.channels + channel)
    }
}

private fun ServerUiState.editChannel(action: ServerAction.EditChannel): ServerUiState {
    val name = action.name.trim()
    if (name.isEmpty()) return this

    val updatedState = updateServer(action.serverId, minimumRole = ServerRole.ADMIN) { server ->
        server.copy(
            channels = server.channels.map { channel ->
                if (channel.id == action.channelId) {
                    channel.copy(
                        name = name.take(MAX_NAME_LENGTH),
                        type = action.type,
                        category = action.type.defaultCategory()
                    )
                } else {
                    channel
                }
            }
        )
    }

    val changedToVoice = updatedState.selectedChannelId == action.channelId &&
        updatedState.selectedChannel?.type == ChannelType.VOICE

    return if (changedToVoice) updatedState.closeChannel() else updatedState
}

private fun ServerUiState.deleteChannel(action: ServerAction.DeleteChannel): ServerUiState {
    val updatedState = updateServer(action.serverId, minimumRole = ServerRole.ADMIN) { server ->
        server.copy(channels = server.channels.filterNot { it.id == action.channelId })
    }

    val deletedOpenChannel = updatedState.selectedChannelId == action.channelId &&
        updatedState.selectedChannel == null

    return if (deletedOpenChannel) updatedState.closeChannel() else updatedState
}

private fun ServerUiState.changeRole(action: ServerAction.ChangeRole): ServerUiState {
    return updateServer(action.serverId, minimumRole = ServerRole.OWNER) { server ->
        server.copy(
            members = server.members.map { member ->
                val canChangeThisMember = member.id == action.memberId &&
                    member.id != CURRENT_USER_ID &&
                    member.role != ServerRole.OWNER &&
                    action.role != ServerRole.OWNER

                if (canChangeThisMember) member.copy(role = action.role) else member
            }
        )
    }
}

private fun ServerUiState.kickMember(action: ServerAction.KickMember): ServerUiState {
    return updateServer(action.serverId, minimumRole = ServerRole.ADMIN) { server ->
        val myRole = server.myRole()
        val remainingMembers = server.members.filterNot { member ->
            val isTarget = member.id == action.memberId
            val isProtected = member.id == CURRENT_USER_ID || member.role == ServerRole.OWNER
            val canKickRole = myRole == ServerRole.OWNER || member.role == ServerRole.MEMBER

            isTarget && !isProtected && canKickRole
        }
        server.copy(members = remainingMembers)
    }
}

private fun ServerUiState.leaveServer(serverId: Int): ServerUiState {
    val server = servers.firstOrNull { it.id == serverId }
    if (server == null || server.myRole() == ServerRole.OWNER) {
        return copy(notice = "Владелец должен сначала передать сервер")
    }

    return removeServer(serverId)
}

private fun ServerUiState.deleteServer(serverId: Int): ServerUiState {
    val server = servers.firstOrNull { it.id == serverId }
    if (server?.myRole() != ServerRole.OWNER) {
        return copy(notice = "Удалить сервер может только владелец")
    }

    return removeServer(serverId)
}

private fun ServerUiState.updateServer(
    serverId: Int,
    minimumRole: ServerRole,
    change: (ServerInfo) -> ServerInfo
): ServerUiState {
    val server = servers.firstOrNull { it.id == serverId } ?: return this
    if (!server.myRole().hasPermission(minimumRole)) {
        return copy(notice = "Недостаточно прав")
    }

    return copy(
        servers = servers.map { currentServer ->
            if (currentServer.id == serverId) change(currentServer) else currentServer
        }
    )
}

private fun ServerUiState.removeServer(serverId: Int): ServerUiState {
    val remainingServers = servers.filterNot { it.id == serverId }

    return copy(
        servers = remainingServers,
        selectedServerId = remainingServers.firstOrNull()?.id,
        selectedChannelId = null,
        searchQuery = "",
        messageDraft = "",
        isMembersOpen = false
    )
}

private fun ChannelType.defaultCategory(): String = when (this) {
    ChannelType.TEXT -> "ТЕКСТОВЫЕ КАНАЛЫ"
    ChannelType.VOICE -> "ГОЛОСОВЫЕ КАНАЛЫ"
}

const val ME = 1
private const val CURRENT_USER_ID = ME
private const val MAX_NAME_LENGTH = 80
private const val FIRST_NEW_CHANNEL_ID = 10000

fun ServerInfo.myRole(): ServerRole? = members.firstOrNull { it.id == ME }?.role

fun ServerRole?.canManage(): Boolean = this == ServerRole.OWNER || this == ServerRole.ADMIN

private fun ServerRole?.hasPermission(minimumRole: ServerRole): Boolean = when (minimumRole) {
    ServerRole.OWNER -> this == ServerRole.OWNER
    ServerRole.ADMIN -> canManage()
    ServerRole.MEMBER -> this != null
}

private fun createSampleServers(): List<ServerInfo> = (1..15).map { serverId ->
    ServerInfo(
        id = serverId,
        name = "Server Community $serverId",
        members = createSampleMembers(serverId),
        channels = createSampleChannels(serverId)
    )
}

private fun createSampleMembers(serverId: Int): List<ServerMember> {
    return (1..serverId * 14).map { memberId ->
        val role = when {
            memberId == ME && serverId == 1 -> ServerRole.OWNER
            memberId == ME && serverId == 2 -> ServerRole.ADMIN
            memberId == ME -> ServerRole.MEMBER
            memberId == 2 -> ServerRole.OWNER
            else -> ServerRole.MEMBER
        }

        ServerMember(
            id = memberId,
            name = if (memberId == ME) "Вы" else "Member $memberId · S$serverId",
            role = role
        )
    }
}

private fun createSampleChannels(serverId: Int): List<ServerChannel> {
    val names = listOf(
        "announcements",
        "rules",
        "general",
        "introductions",
        "memes",
        "projects-$serverId",
        "General voice",
        "Gaming room"
    )

    return names.mapIndexed { index, name ->
        val type = if (index >= 6) ChannelType.VOICE else ChannelType.TEXT
        val category = when {
            index < 2 -> "ИНФОРМАЦИЯ"
            type == ChannelType.TEXT -> "ТЕКСТОВЫЕ КАНАЛЫ"
            else -> "ГОЛОСОВЫЕ КАНАЛЫ"
        }

        ServerChannel(
            id = serverId * 100 + index,
            name = name,
            type = type,
            category = category
        )
    }
}

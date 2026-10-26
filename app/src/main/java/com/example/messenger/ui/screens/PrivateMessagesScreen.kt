package com.example.messenger.ui.screens

import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.core.animateDpAsState
import com.example.messenger.util.rememberNetworkStatus
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.scrollable
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
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CallEnd
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import com.example.messenger.ui.navigation.Screen
import com.example.messenger.ui.theme.AppTheme


data class ChatMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val text: String,
    val isFromMe: Boolean = true,
    val timestamp: Long = System.currentTimeMillis()
)

@Composable
fun PrivateMessagesScreen(
    onSettingsClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onCallClick: () -> Unit = {},
    onVideocallClick: () -> Unit = {},
){
    var searchQuery by rememberSaveable { mutableStateOf("") }
    var messageQuery by rememberSaveable { mutableStateOf("") }
    val messages = rememberSaveable() { mutableStateListOf<ChatMessage>() }
    var isDrafted by remember { mutableStateOf(false) }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppTheme.colors.mainBackground)
            .imePadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .padding(horizontal = 16.dp)
                .padding(bottom = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .height(108.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(AppTheme.colors.layoutBackground),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp)
                ) {
                    Text(
                        text = "User Nickname",
                        color = AppTheme.colors.textColor,
                        fontSize = 22.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .padding(horizontal = 16.dp, vertical = 16.dp),
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    val ctx = LocalContext.current
                    Box(
                        modifier = Modifier
                            .padding(vertical = 8.dp)
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFD32F2F))
                            .clickable { Toast.makeText(ctx, "Вас могнули... ?:)",
                                Toast.LENGTH_SHORT).show() }
                    ) {
                        Icon(
                            imageVector = Icons.Default.CallEnd,
                            contentDescription = "Settings",
                            tint = AppTheme.colors.textColor,
                            modifier = Modifier
                                .padding(horizontal = 12.dp, vertical = 10.dp)
                                .size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier
                            .width(160.dp)
                            .height(36.dp)
                            .clip(RoundedCornerShape(32.dp))
                            .background(AppTheme.colors.searchField),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = AppTheme.colors.textColor,
                            modifier = Modifier
                                .size(16.dp)
                        )

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
                                            "Search",
                                            color = AppTheme.colors.textColor,
                                            fontSize = 13.sp,
                                            maxLines = 1
                                        )
                                    }
                                    innerTextField()
                                }
                            }
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(AppTheme.colors.layoutBackground)
                            .clickable {onCallClick()}
                    ) {
                        Icon(
                            imageVector = Icons.Default.Call,
                            contentDescription = "Settings",
                            tint = AppTheme.colors.textColor,
                            modifier = Modifier
                                .padding(horizontal = 6.dp, vertical = 6.dp)
                                .size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(4.dp))

                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(AppTheme.colors.layoutBackground)
                            .clickable {onVideocallClick()}
                    ) {
                        Icon(
                            imageVector = Icons.Default.Videocam,
                            contentDescription = "Settings",
                            tint = AppTheme.colors.textColor,
                            modifier = Modifier
                                .padding(horizontal = 6.dp, vertical = 6.dp)
                                .size(24.dp)
                        )
                    }
                    val ctx = LocalContext.current
                    IconButton(
                        onClick = { Toast.makeText(ctx, "Вас могнули... ?:)",
                            Toast.LENGTH_SHORT).show() },
                        modifier = Modifier
                            .size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = AppTheme.colors.textColor,
                            modifier = Modifier
                                .size(24.dp)
                        )
                    }
                }
            }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
                    .clip(RoundedCornerShape(16.dp))
                    .background(AppTheme.colors.layoutBackground)
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(vertical = 12.dp, horizontal = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(/*20*/messages.size) { index ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(36.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(AppTheme.colors.messageBackground)
                                .padding(horizontal = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                text = "${/*"Let's start chat $index".repeat(index + 1)*/messages[index].text}",
                                color = AppTheme.colors.textColor,
                                fontSize = 16.sp,
                                modifier = Modifier
                                    .padding(horizontal = 16.dp, vertical = 16.dp),
                            )
                        }
                    }
                }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(AppTheme.colors.layoutBackground)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            Row(
                modifier = Modifier
                    .weight(1f)
                    .heightIn(min = 36.dp, max = 120.dp)
                    .clip(RoundedCornerShape(32.dp))
                    .background(AppTheme.colors.searchField)
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Spacer(modifier = Modifier.width(4.dp))
                BasicTextField(
                    value = messageQuery,
                    onValueChange = { messageQuery = it },
                    textStyle = TextStyle(color = AppTheme.colors.textColor, fontSize = 13.sp),
                    modifier = Modifier
                        .fillMaxWidth(),
                    decorationBox = { innerTextField ->
                        Box(contentAlignment = Alignment.CenterStart) {
                            if (messageQuery.isEmpty()) {
                                Text(
                                    "Write message",
                                    color = AppTheme.colors.textColor,
                                    fontSize = 13.sp,
                                    maxLines = 6
                                )
                            }
                            innerTextField()
                        }
                    }
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            val ctx = LocalContext.current
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF9999FF))
                    .clickable { /*Toast.makeText(ctx, "Вас могнули... ?:)",
                        Toast.LENGTH_SHORT).show()*/
                        if (messageQuery.isNotBlank()) {
                            messages.add(
                                ChatMessage(
                                    text = messageQuery.trim(),
                                    isFromMe = true
                                )
                            )
                            messageQuery = ""
                        }}
            ) {
                Icon(
                    imageVector = Icons.Default.Send,
                    contentDescription = "Settings",
                    tint = AppTheme.colors.textColor,
                    modifier = Modifier
                        .padding(horizontal = 6.dp, vertical = 6.dp)
                        .size(24.dp)
                )
            }
        }
    }
    }
}
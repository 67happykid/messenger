package com.example.messenger.ui.screens

import kotlin.math.ceil
import kotlin.math.sqrt
import android.content.Intent
import android.content.res.Configuration
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
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.VideocamOff
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import com.example.messenger.ui.theme.AppTheme


@Composable
fun CallScreen(
    onBackClick: () -> Unit = {},
    onCallOffClick: () -> Unit = {},
){
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
            Row{
                Row(modifier = Modifier
                    .height(40.dp)
                    .weight(0.4f)
                    .clip(RoundedCornerShape(20.dp))
                    .background(AppTheme.colors.layoutBackground)
                    .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically){
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(AppTheme.colors.iconBackground)
                        .clickable {onBackClick()},
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = "Back",
                        tint = AppTheme.colors.textColor,
                        modifier = Modifier
                            .size(16.dp)
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "User Nickname",
                    color = AppTheme.colors.textColor,
                    fontSize = 15.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                )
            }
                Spacer(modifier = Modifier.weight(0.4f))
                }



            Column(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(AppTheme.colors.layoutBackground)
                            .fillMaxWidth()
                            .weight(1f)
                            .padding(vertical = 8.dp, horizontal = 8.dp),
                    ) {
                val configuration = LocalConfiguration.current
                val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
                val itemCount = 6
                val colCount = ceil((-1.0 + sqrt(1.0 + 4.0 * itemCount)) / 2.0).toInt().coerceAtLeast(1)
                val rowCount = ceil((-2.0 + sqrt(4.0 + 4.0 * itemCount)) / 2.0).toInt().coerceAtLeast(1)
                val columns = if (isLandscape) ceil(itemCount.toFloat() / rowCount).toInt() else colCount
                val rows = if (isLandscape) rowCount else ceil(itemCount.toFloat() / colCount).toInt()

                for (index in 0 until rows) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .padding(vertical = 8.dp),
                    ) {

                        for (colIndex in 0 until columns) {
                            val itemIndex = index * columns + colIndex
                            val hue = (itemIndex.toFloat() / itemCount) * 360f
                            if (itemIndex < itemCount) {
                                val dynamicColor = Color.hsl(
                                    hue = hue,
                                    saturation = 0.75f,
                                    lightness = 0.65f
                                )
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxHeight()
                                        .padding(horizontal = 8.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(AppTheme.colors.iconBackground),
                                    contentAlignment = Alignment.Center
                                ) {
                                    val size = 168 / columns
                                    Box(
                                        modifier = Modifier
                                            .size(size.dp)
                                            .clip(RoundedCornerShape(size.dp))
                                            .background(dynamicColor)
                                    )
                                }
                            } else {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }

            }



            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(AppTheme.colors.layoutBackground)
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.Center
            ) {
                Spacer(modifier = Modifier.weight(0.5f))
                val ctx = LocalContext.current
                Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(AppTheme.colors.iconBackground)
                            .clickable { Toast.makeText(ctx, "Вас могнули... ?:)",
                                Toast.LENGTH_SHORT).show() }
                    ) {
                        Icon(
                            imageVector = Icons.Default.VideocamOff,
                            contentDescription = "OffVideo",
                            tint = AppTheme.colors.textColor,
                            modifier = Modifier
                                .padding(horizontal = 8.dp, vertical = 8.dp)
                                .size(36.dp)
                        )
                    }
                Spacer(modifier = Modifier.weight(1f))
                Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(AppTheme.colors.iconBackground)
                            .clickable { Toast.makeText(ctx, "Вас могнули... ?:)",
                                Toast.LENGTH_SHORT).show() }
                    ) {
                        Icon(
                            imageVector = Icons.Default.MicOff,
                            contentDescription = "OffMicro",
                            tint = AppTheme.colors.textColor,
                            modifier = Modifier
                                .padding(horizontal = 8.dp, vertical = 8.dp)
                                .size(36.dp)
                        )
                    }
                Spacer(modifier = Modifier.weight(1f))
                Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(AppTheme.colors.iconBackground)
                            .clickable { Toast.makeText(ctx, "Вас могнули... ?:)",
                                Toast.LENGTH_SHORT).show() }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Message,
                            contentDescription = "LocalChat",
                            tint = AppTheme.colors.textColor,
                            modifier = Modifier
                                .padding(horizontal = 8.dp, vertical = 8.dp)
                                .size(36.dp)
                        )
                    }
                Spacer(modifier = Modifier.weight(1f))
                val context = LocalContext.current
                Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(AppTheme.colors.iconBackground)
                            .clickable {val CallLink = "messenger://messenger.app/invitecall?token=call${System.currentTimeMillis()}&expires=10m"
                                val SendIntent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(
                                        Intent.EXTRA_TEXT,
                                        "Привет! Присоединяйся к звонку. Ссылка действительна 10 минут:\n $CallLink"
                                    )
                                    type = "text/plain"
                                }
                                val ShareIntent = Intent.createChooser(
                                    SendIntent,
                                    "Пригласить в звонок через"
                                )
                                context.startActivity(ShareIntent)}
                    ) {
                        Icon(
                            imageVector = Icons.Default.PersonAdd,
                            contentDescription = "AddUser",
                            tint = AppTheme.colors.textColor,
                            modifier = Modifier
                                .padding(horizontal = 8.dp, vertical = 8.dp)
                                .size(36.dp)
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFBB2222))
                            .clickable {onCallOffClick()}
                    ) {
                        Icon(
                            imageVector = Icons.Default.CallEnd,
                            contentDescription = "Settings",
                            tint = AppTheme.colors.textColor,
                            modifier = Modifier
                                .padding(horizontal = 8.dp, vertical = 8.dp)
                                .size(36.dp)
                        )
                    }
                Spacer(modifier = Modifier.weight(0.5f))
            }
            }

    }
}
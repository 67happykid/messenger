package com.example.messenger.ui.screens

import android.media.AudioDeviceCallback
import android.media.AudioDeviceInfo
import android.media.AudioManager
import android.os.Handler
import android.os.Looper
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.messenger.data.settings.SoundChange
import com.example.messenger.data.settings.SoundSettings
import com.example.messenger.ui.theme.MessengerTheme
import kotlin.math.roundToInt
import androidx.compose.foundation.layout.width

private data class AudioDeviceOption(
    val key: String,
    val label: String
)

@Composable
fun SoundScreen(
    settings: SoundSettings,
    onSoundChange: (SoundChange) -> Unit,
    onBackClick: () -> Unit
) {
    val inputDevices = rememberAudioDeviceOptions(
        flags = AudioManager.GET_DEVICES_INPUTS
    )
    val outputDevices = rememberAudioDeviceOptions(
        flags = AudioManager.GET_DEVICES_OUTPUTS
    )

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
                    text = "Звук",
                    style = MaterialTheme.typography.titleLarge
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                AudioDevicePicker(
                    title = "Устройство ввода",
                    devices = inputDevices,
                    selectedKey = settings.inputDeviceKey,
                    onSelect = { onSoundChange(SoundChange.InputDevice(it)) }
                )

                AudioVolumeSlider(
                    title = "Уровень микрофона",
                    percent = settings.inputVolume,
                    onSave = { onSoundChange(SoundChange.InputVolume(it)) }
                )

                AudioDevicePicker(
                    title = "Устройство вывода",
                    devices = outputDevices,
                    selectedKey = settings.outputDeviceKey,
                    onSelect = { onSoundChange(SoundChange.OutputDevice(it)) }
                )

                AudioVolumeSlider(
                    title = "Уровень воспроизведения",
                    percent = settings.outputVolume,
                    onSave = { onSoundChange(SoundChange.OutputVolume(it)) }
                )

                Text(
                    text = "Параметры сохраняются для будущих звонков.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun AudioDevicePicker(
    title: String,
    devices: List<AudioDeviceOption>,
    selectedKey: String?,
    onSelect: (String?) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    val selectedLabel = if (selectedKey == null) {
        "По умолчанию"
    } else {
        devices.firstOrNull { it.key == selectedKey }?.label
            ?: "Устройство недоступно"
    }

    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(title, style = MaterialTheme.typography.titleMedium)

        Box {
            OutlinedButton(
                onClick = { expanded = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(selectedLabel)
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                DropdownMenuItem(
                    text = { Text("По умолчанию") },
                    onClick = {
                        expanded = false
                        onSelect(null)
                    }
                )

                devices.forEach { device ->
                    DropdownMenuItem(
                        text = { Text(device.label) },
                        onClick = {
                            expanded = false
                            onSelect(device.key)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun AudioVolumeSlider(
    title: String,
    percent: Int,
    onSave: (Int) -> Unit
) {
    var draft by remember(percent) {
        mutableFloatStateOf(percent.toFloat())
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Text(
            text = "$title: ${draft.roundToInt()}%",
            style = MaterialTheme.typography.titleMedium
        )

        Slider(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .width(240.dp),
            value = draft,
            onValueChange = { draft = it },
            onValueChangeFinished = { onSave(draft.roundToInt()) },
            valueRange = 0f..100f
        )
    }
}

@Composable
private fun rememberAudioDeviceOptions(
    flags: Int
): List<AudioDeviceOption> {
    if (LocalInspectionMode.current) return emptyList()

    val context = LocalContext.current
    val audioManager = remember(context) {
        context.getSystemService(AudioManager::class.java)
    }

    var devices by remember(audioManager, flags) {
        mutableStateOf(emptyList<AudioDeviceOption>())
    }

    DisposableEffect(audioManager, flags) {
        fun refresh() {
            devices = audioManager
                ?.getDevices(flags)
                .orEmpty()
                .mapNotNull { device ->
                    val label = when (device.type) {
                        AudioDeviceInfo.TYPE_BUILTIN_MIC -> "Встроенный микрофон"
                        AudioDeviceInfo.TYPE_BUILTIN_SPEAKER -> "Динамик телефона"
                        AudioDeviceInfo.TYPE_BUILTIN_EARPIECE -> "Разговорный динамик"
                        AudioDeviceInfo.TYPE_WIRED_HEADSET -> "Проводная гарнитура"
                        AudioDeviceInfo.TYPE_WIRED_HEADPHONES -> "Проводные наушники"
                        else -> return@mapNotNull null
                    }

                    AudioDeviceOption(
                        key = "${device.type}:${device.productName}",
                        label = label
                    )
                }
                .distinctBy { it.key }
                .sortedBy { it.label }
        }

        val callback = object : AudioDeviceCallback() {
            override fun onAudioDevicesAdded(
                addedDevices: Array<out AudioDeviceInfo>
            ) {
                refresh()
            }

            override fun onAudioDevicesRemoved(
                removedDevices: Array<out AudioDeviceInfo>
            ) {
                refresh()
            }
        }

        audioManager?.registerAudioDeviceCallback(
            callback,
            Handler(Looper.getMainLooper())
        )
        refresh()

        onDispose {
            audioManager?.unregisterAudioDeviceCallback(callback)
        }
    }

    return devices
}

@Preview(name = "Звук", showBackground = true, widthDp = 360)
@Composable
private fun SoundScreenPreview() {
    MessengerTheme(dynamicColor = false) {
        SoundScreen(
            settings = SoundSettings(inputVolume = 65, outputVolume = 80),
            onSoundChange = {},
            onBackClick = {}
        )
    }
}
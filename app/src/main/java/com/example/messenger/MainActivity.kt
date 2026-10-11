package com.example.messenger

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.messenger.ui.screens.MainScreen
import com.example.messenger.ui.theme.MessengerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Включаем отображение "от края до края" (без рамок вокруг статус-бара)
        enableEdgeToEdge()

        setContent {
            // Оборачиваем в вашу тему и вызываем собранный экран
            MessengerTheme {
                MainScreen()
            }
        }
    }
}
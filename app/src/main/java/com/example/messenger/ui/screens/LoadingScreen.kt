package com.example.messenger.ui.screens

import android.R
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.messenger.ui.theme.DarkThemeTextColor
import com.example.messenger.ui.theme.MainBackground

@Composable
fun LoadingScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MainBackground),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            //Кружок загрузки
            CircularProgressIndicator(
                color = DarkThemeTextColor,
                modifier = Modifier
                    .size(48.dp),
                strokeWidth = 4.dp
            )

            //Текст под символом загрузки
            Text(
                text = "Loading",
                color = DarkThemeTextColor,
                fontSize = 14.sp
            )
        }
    }
}
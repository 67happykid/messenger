package com.example.messenger.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.messenger.ui.theme.AppTheme
import com.example.messenger.ui.theme.chatBackground


@Composable
fun LoadingScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppTheme.colors.mainBackground),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            //Кружок загрузки
            CircularProgressIndicator(
                color = AppTheme.colors.textColor,
                modifier = Modifier
                    .size(48.dp),
                strokeWidth = 4.dp
            )

            //Текст под символом загрузки
            Text(
                text = "Loading",
                color = AppTheme.colors.textColor,
                style = MaterialTheme.typography.titleLarge
            )
        }
    }
}
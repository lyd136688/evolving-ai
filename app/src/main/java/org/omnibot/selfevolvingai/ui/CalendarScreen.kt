package org.omnibot.selfevolvingai.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen() {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("📅 日历闹钟", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))
        Text("管理日程和闹钟提醒功能", style = MaterialTheme.typography.bodyMedium)
    }
}

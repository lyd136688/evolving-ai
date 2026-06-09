package org.omnibot.selfevolvingai.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleScreen() {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("⏰ 定时任务", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))
        Text("创建自动化定时任务功能", style = MaterialTheme.typography.bodyMedium)
    }
}

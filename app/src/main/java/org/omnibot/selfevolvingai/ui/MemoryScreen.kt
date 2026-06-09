package org.omnibot.selfevolvingai.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MemoryScreen() {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(" 记忆系统", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))
        Text("长期记忆和短期记忆管理功能", style = MaterialTheme.typography.bodyMedium)
    }
}

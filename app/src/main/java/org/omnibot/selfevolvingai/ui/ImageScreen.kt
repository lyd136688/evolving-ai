package org.omnibot.selfevolvingai.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImageScreen() {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("🖼️ 图片生成", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))
        Text("AI 图片生成功能", style = MaterialTheme.typography.bodyMedium)
    }
}

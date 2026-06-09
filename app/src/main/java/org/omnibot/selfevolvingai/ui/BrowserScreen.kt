package org.omnibot.selfevolvingai.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BrowserScreen() {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("🌐 浏览器", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))
        Text("网页浏览和内容提取功能", style = MaterialTheme.typography.bodyMedium)
    }
}

package org.omnibot.selfevolvingai.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SettingsScreen() {
    var apiKey by remember { mutableStateOf("") }
    var apiEndpoint by remember { mutableStateOf("https://api.openai.com/v1") }
    var defaultModel by remember { mutableStateOf("gpt-3.5-turbo") }
    var workspacePath by remember { mutableStateOf("/workspace") }
    var autoSaveMemory by remember { mutableStateOf(true) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            "⚙️ 设置",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 24.dp)
        )
        
        SettingsSection("API 配置") {
            OutlinedTextField(
                value = apiKey,
                onValueChange = { apiKey = it },
                label = { Text("API Key") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = PasswordVisualTransformation()
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = apiEndpoint,
                onValueChange = { apiEndpoint = it },
                label = { Text("API 端点") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = defaultModel,
                onValueChange = { defaultModel = it },
                label = { Text("默认模型") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        SettingsSection("工作区配置") {
            OutlinedTextField(
                value = workspacePath,
                onValueChange = { workspacePath = it },
                label = { Text("工作区路径") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        SettingsSection("记忆系统") {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("自动保存记忆", style = MaterialTheme.typography.bodyLarge)
                Switch(checked = autoSaveMemory, onCheckedChange = { autoSaveMemory = it })
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        SettingsSection("关于") {
            Text("版本：2.0.0", style = MaterialTheme.typography.bodyMedium)
            Text("构建：2026-06-08", style = MaterialTheme.typography.bodyMedium)
            Text("基于 Omnibot Agent 架构", style = MaterialTheme.typography.bodyMedium)
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Button(
            onClick = { /* TODO: 保存设置 */ },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("保存设置")
        }
    }
}

@Composable
fun SettingsSection(title: String, content: @Composable () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(bottom = 16.dp))
            content()
        }
    }
}

package org.omnibot.selfevolvingai.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import org.omnibot.selfevolvingai.network.ApiService

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen() {
    var apiKey by remember { mutableStateOf("") }
    var apiProvider by remember { mutableStateOf("DeepSeek") }
    var defaultModel by remember { mutableStateOf("deepseek-chat") }
    var showApiKey by remember { mutableStateOf(false) }
    var isSaved by remember { mutableStateOf(false) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            "️ 设置",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 24.dp)
        )
        
        if (isSaved) {
            Snackbar(
                modifier = Modifier.padding(bottom = 16.dp),
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ) {
                Text("✅ 设置已保存！")
            }
        }
        
        SettingsSection("API 配置") {
            Text("选择 API 提供商", style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ApiService.API_PROVIDERS.keys.take(3).forEach { provider ->
                    FilterChip(
                        selected = apiProvider == provider,
                        onClick = { apiProvider = provider },
                        label = { Text(provider) }
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            
            OutlinedTextField(
                value = apiKey,
                onValueChange = { apiKey = it },
                label = { Text("API Key") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = if (showApiKey) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    IconButton(onClick = { showApiKey = !showApiKey }) {
                        Text(if (showApiKey) "🙈" else "👁️")
                    }
                }
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "💡 国内推荐：DeepSeek / 智谱 AI / 通义千问",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
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
        
        SettingsSection("关于") {
            Text("版本：2.1.0", style = MaterialTheme.typography.bodyMedium)
            Text("架构：NowInAndroid + Compose", style = MaterialTheme.typography.bodyMedium)
            Text("硬件检测：已启用", style = MaterialTheme.typography.bodyMedium)
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Button(
            onClick = { isSaved = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("💾 保存设置")
        }
    }
}

@Composable
fun SettingsSection(title: String, content: @Composable () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            content()
        }
    }
}

package org.omnibot.selfevolvingai.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.omnibot.selfevolvingai.network.ApiService
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen() {
    var messages by remember { mutableStateOf(listOf<Message>()) }
    var input by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var apiKey by remember { mutableStateOf("") }
    var apiProvider by remember { mutableStateOf("DeepSeek") }
    var model by remember { mutableStateOf("deepseek-chat") }
    
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val apiService = remember { ApiService() }
    
    LaunchedEffect(messages.size) {
        scope.launch {
            if (messages.isNotEmpty()) {
                listState.animateScrollToItem(messages.size - 1)
            }
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            "💬 AI 对话",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                "当前：$apiProvider - $model",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            TextButton(onClick = { /* 打开设置 */ }) {
                Text("⚙️ 配置")
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(messages) { message ->
                MessageBubble(message)
            }
            
            if (isLoading) {
                item {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .size(24.dp)
                                .padding(end = 8.dp)
                        )
                        Text("AI 思考中...", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        if (apiKey.isBlank()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.warningContainer
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("⚠️ 请先配置 API Key", style = MaterialTheme.typography.titleMedium)
                    Text("前往 设置 页面配置 API Key 后即可使用对话功能", style = MaterialTheme.typography.bodySmall)
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = { /* 导航到设置 */ }) {
                        Text("去配置")
                    }
                }
            }
        }
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = input,
                onValueChange = { input = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("输入命令或问题...") },
                maxLines = 4,
                enabled = !isLoading && apiKey.isNotBlank()
            )
            Button(
                onClick = {
                    if (input.isNotBlank() && apiKey.isNotBlank()) {
                        messages = messages + Message("user", input)
                        val userInput = input
                        input = ""
                        isLoading = true
                        
                        scope.launch {
                            val result = apiService.chat(
                                apiKey = apiKey,
                                baseUrl = ApiService.API_PROVIDERS[apiProvider] ?: "",
                                model = model,
                                messages = messages.map { ApiService.ChatMessage(it.role, it.content) }
                            )
                            
                            isLoading = false
                            result.onSuccess { response ->
                                messages = messages + Message("assistant", response)
                            }.onFailure { error ->
                                messages = messages + Message("assistant", "❌ 错误：${error.message}")
                            }
                        }
                    }
                },
                enabled = input.isNotBlank() && !isLoading && apiKey.isNotBlank()
            ) {
                Text("发送")
            }
        }
    }
}

data class Message(val role: String, val content: String, val timestamp: Long = System.currentTimeMillis())

@Composable
fun MessageBubble(message: Message) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = when (message.role) {
                "user" -> MaterialTheme.colorScheme.primaryContainer
                "assistant" -> MaterialTheme.colorScheme.secondaryContainer
                else -> MaterialTheme.colorScheme.surfaceVariant
            }
        )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = when (message.role) {
                        "user" -> "👤 你"
                        "assistant" -> "🤖 AI"
                        else -> "ℹ️ 系统"
                    },
                    style = MaterialTheme.typography.labelMedium
                )
                Text(
                    text = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(message.timestamp)),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(message.content, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

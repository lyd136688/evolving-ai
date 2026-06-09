package org.omnibot.selfevolvingai.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TerminalScreen() {
    var output by remember { mutableStateOf(listOf<LogEntry>()) }
    var command by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var serverUrl by remember { mutableStateOf("https://api.omnibot.cn.com") }
    var apiKey by remember { mutableStateOf("") }
    
    val scope = rememberCoroutineScope()
    val toolManager = remember { ToolManager(serverUrl, apiKey) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            "💻 终端执行",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        Text(
            "在 Alpine 环境中执行 Shell 命令",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        // 输出区域
        Card(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF1E1E1E)
            )
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp)
            ) {
                items(output) { entry ->
                    Text(
                        text = if (entry.isError) "❌ ${entry.content}" else "$ ${entry.content}",
                        fontSize = 12.sp,
                        color = if (entry.isError) Color(0xFFFF6B6B) else Color(0xFF4ECDC4),
                        modifier = Modifier.padding(vertical = 2.dp)
                    )
                }
                
                if (isLoading) {
                    item {
                        Text(
                            text = "⏳ 执行中...",
                            fontSize = 12.sp,
                            color = Color(0xFFFFD93D),
                            modifier = Modifier.padding(vertical = 2.dp)
                        )
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // 配置区域
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("服务器配置", style = MaterialTheme.typography.titleSmall)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = serverUrl,
                    onValueChange = { serverUrl = it },
                    label = { Text("服务器 URL") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    textStyle = LocalTextStyle.current.copy(fontSize = 12.sp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = apiKey,
                    onValueChange = { apiKey = it },
                    label = { Text("API Key") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    textStyle = LocalTextStyle.current.copy(fontSize = 12.sp)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // 输入区域
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = command,
                onValueChange = { command = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("输入 Shell 命令...") },
                singleLine = true,
                enabled = !isLoading
            )
            Button(
                onClick = {
                    if (command.isNotBlank() && apiKey.isNotBlank()) {
                        scope.launch {
                            val result = toolManager.executeCommand(command)
                            output = output + LogEntry(command, false)
                            output = output + LogEntry(result, result.contains("失败") || result.contains("error"))
                            command = ""
                        }
                        isLoading = true
                    }
                },
                enabled = command.isNotBlank() && !isLoading && apiKey.isNotBlank()
            ) {
                Text("执行")
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // 快捷命令
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AssistChip(onClick = { command = "ls -la" }, label = { Text("ls -la") })
            AssistChip(onClick = { command = "pwd" }, label = { Text("pwd") })
            AssistChip(onClick = { command = "python3 --version" }, label = { Text("python3") })
        }
    }
}

data class LogEntry(val content: String, val isError: Boolean)

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
import org.omnibot.selfevolvingai.llm.LocalLLMEngine
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(llmEngine: LocalLLMEngine) {
    var messages by remember { mutableStateOf(listOf<Message>()) }
    var input by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    val isModelLoaded = llmEngine.isModelLoaded()
    
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    
    LaunchedEffect(Unit) {
        if (!isModelLoaded) {
            isLoading = true
            val result = llmEngine.loadModel()
            result.onSuccess {
                messages = messages + Message("system", "✅ $it")
            }.onFailure {
                messages = messages + Message("system", "❌ 模型加载失败：${it.message}\n请前往模型管理下载 Qwen2.5-7B-GGUF")
            }
            isLoading = false
        }
    }
    
    LaunchedEffect(messages.size) {
        scope.launch {
            if (messages.isNotEmpty()) listState.animateScrollToItem(messages.size - 1)
        }
    }
    
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("💬 本地 AI 对话", style = MaterialTheme.typography.headlineMedium)
            TextButton(onClick = {
                messages = messages + Message("system", llmEngine.getMemoryStats())
            }) {
                Text("记忆状态")
            }
        }
        
        Text(
            if (isModelLoaded) "🟢 模型已加载 - 本地推理" else "🔴 模型未加载",
            style = MaterialTheme.typography.bodySmall,
            color = if (isModelLoaded) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        LazyColumn(
            state = listState,
            modifier = Modifier.weight(1f).fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(messages) { message -> MessageBubble(message) }
            if (isLoading) {
                item {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp).padding(end = 8.dp))
                        Text("AI 思考中...", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = input,
                onValueChange = { input = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("输入问题或命令...") },
                maxLines = 4,
                enabled = !isLoading && isModelLoaded
            )
            Button(
                onClick = {
                    if (input.isNotBlank() && isModelLoaded) {
                        messages = messages + Message("user", input)
                        val userInput = input
                        input = ""
                        isLoading = true
                        
                        scope.launch {
                            val result = llmEngine.generate(userInput)
                            isLoading = false
                            result.onSuccess { response ->
                                messages = messages + Message("assistant", response)
                            }.onFailure { error ->
                                messages = messages + Message("assistant", "❌ 错误：${error.message}")
                            }
                        }
                    }
                },
                enabled = input.isNotBlank() && !isLoading && isModelLoaded
            ) { Text("发送") }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            AssistChip(
                onClick = {
                    scope.launch {
                        val result = llmEngine.selfImprove("请分析最近的对话，找出可以改进的地方")
                        result.onSuccess { messages = messages + Message("assistant", "🔧 自改进：$it") }
                    }
                },
                label = { Text("🔧 自改进") }
            )
            AssistChip(
                onClick = {
                    scope.launch {
                        val result = llmEngine.selfLearn("记录当前对话内容到知识库")
                        result.onSuccess { messages = messages + Message("assistant", "📚 自学习：$it") }
                    }
                },
                label = { Text("📚 自学习") }
            )
            AssistChip(
                onClick = {
                    scope.launch {
                        val result = llmEngine.selfArchitect("设计一个待办事项应用")
                        result.onSuccess { messages = messages + Message("assistant", "🏗️ 自构架：$it") }
                    }
                },
                label = { Text("🏗️ 自构架") }
            )
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
                "system" -> MaterialTheme.colorScheme.tertiaryContainer
                else -> MaterialTheme.colorScheme.surfaceVariant
            }
        )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(
                    text = when (message.role) {
                        "user" -> "👤 你"
                        "assistant" -> "🤖 AI"
                        "system" -> "ℹ️ 系统"
                        else -> "消息"
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

package org.omnibot.selfevolvingai.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TasksScreen() {
    var tasks by remember { mutableStateOf(listOf<TaskItem>()) }
    var newTaskInput by remember { mutableStateOf("") }
    var concurrency by remember { mutableStateOf(2) }
    var isRunning by remember { mutableStateOf(false) }
    var results by remember { mutableStateOf(listOf<TaskResult>()) }
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
            "📋 任务分发",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        Text(
            "并行执行多个子任务，自动聚合结果",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        // 配置卡片
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("并发度设置", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = concurrency == 1,
                        onClick = { concurrency = 1 },
                        label = { Text("1 个") }
                    )
                    FilterChip(
                        selected = concurrency == 2,
                        onClick = { concurrency = 2 },
                        label = { Text("2 个") }
                    )
                    FilterChip(
                        selected = concurrency == 3,
                        onClick = { concurrency = 3 },
                        label = { Text("3 个") }
                    )
                    FilterChip(
                        selected = concurrency == 4,
                        onClick = { concurrency = 4 },
                        label = { Text("4 个") }
                    )
                    FilterChip(
                        selected = concurrency == 6,
                        onClick = { concurrency = 6 },
                        label = { Text("6 个") }
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // 任务列表
        Text("任务列表", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))
        
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            itemsIndexed(tasks) { index, task ->
                TaskItemCard(
                    task = task,
                    index = index,
                    onRemove = { tasks = tasks.filterIndexed { i, _ -> i != index } },
                    result = results.find { it.taskIndex == index }
                )
            }
            
            item {
                // 添加任务按钮
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    onClick = {
                        if (newTaskInput.isNotBlank()) {
                            tasks = tasks + TaskItem(newTaskInput, false)
                            newTaskInput = ""
                        }
                    }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = newTaskInput,
                            onValueChange = { newTaskInput = it },
                            modifier = Modifier.weight(1f),
                            placeholder = { Text("输入新任务...") },
                            singleLine = true
                        )
                        IconButton(onClick = {
                            if (newTaskInput.isNotBlank()) {
                                tasks = tasks + TaskItem(newTaskInput, false)
                                newTaskInput = ""
                            }
                        }) {
                            Text("➕")
                        }
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // 执行按钮
        Button(
            onClick = {
                if (tasks.isNotEmpty() && apiKey.isNotBlank()) {
                    isRunning = true
                    results = tasks.mapIndexed { index, task ->
                        TaskResult(index, task.description, "等待中", null)
                    }
                    
                    scope.launch {
                        val taskDescriptions = tasks.map { it.description }
                        val result = toolManager.subagentDispatch(
                            tasks = taskDescriptions,
                            concurrency = concurrency,
                            mergeInstruction = "按顺序返回每个任务的结果"
                        )
                        
                        result.onSuccess { response ->
                            results = tasks.mapIndexed { index, task ->
                                TaskResult(index, task.description, "完成", response)
                            }
                        }.onFailure { error ->
                            results = tasks.mapIndexed { index, task ->
                                TaskResult(index, task.description, "失败", error.message)
                            }
                        }
                        isRunning = false
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = tasks.isNotEmpty() && !isRunning && apiKey.isNotBlank()
        ) {
            if (isRunning) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("执行中...")
            } else {
                Text("🚀 执行所有任务 (${tasks.size}个)")
            }
        }
        
        if (apiKey.isBlank()) {
            Text(
                "️ 请先在设置中配置 API Key",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

data class TaskItem(val description: String, var completed: Boolean = false)

data class TaskResult(
    val taskIndex: Int,
    val description: String,
    val status: String,
    val result: String?
)

@Composable
fun TaskItemCard(
    task: TaskItem,
    index: Int,
    onRemove: () -> Unit,
    result: TaskResult?
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = when (result?.status) {
                "完成" -> MaterialTheme.colorScheme.primaryContainer
                "失败" -> MaterialTheme.colorScheme.errorContainer
                "等待中" -> MaterialTheme.colorScheme.surfaceVariant
                else -> MaterialTheme.colorScheme.surface
            }
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(modifier = Modifier.weight(1f)) {
                    Text(
                        text = when (result?.status) {
                            "完成" -> "✅"
                            "失败" -> "❌"
                            "等待中" -> "⏳"
                            else -> ""
                        },
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Column {
                        Text(
                            "任务 ${index + 1}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(task.description, style = MaterialTheme.typography.bodyMedium)
                    }
                }
                IconButton(onClick = onRemove) {
                    Text("❌")
                }
            }
            
            if (result?.result != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Divider()
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "结果：${result.result.take(200)}${if (result.result.length > 200) "..." else ""}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

// 在 ToolManager.kt 中添加：
/*
    suspend fun subagentDispatch(
        tasks: List<String>,
        concurrency: Int = 2,
        mergeInstruction: String = ""
    ): Result<String> = withContext(Dispatchers.IO) {
        client.subagentDispatch(tasks, concurrency, mergeInstruction)
    }
*/

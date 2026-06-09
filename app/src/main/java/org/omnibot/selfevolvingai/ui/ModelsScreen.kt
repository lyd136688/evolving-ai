package org.omnibot.selfevolvingai.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModelsScreen() {
    var models by remember { mutableStateOf(listOf<Model>()) }
    var selectedSource by remember { mutableStateOf("HuggingFace") }
    
    LaunchedEffect(Unit) {
        models = listOf(
            Model("Qwen2.5-7B-Instruct-GGUF", "7B", "4.2 GB", "等待下载"),
            Model("Qwen2.5-14B-Instruct-GGUF", "14B", "8.5 GB", "等待下载"),
            Model("Llama-3-8B-Instruct-GGUF", "8B", "4.8 GB", "等待下载"),
            Model("Gemma-2-9B-Instruct-GGUF", "9B", "5.2 GB", "等待下载")
        )
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            " 模型管理",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = selectedSource == "HuggingFace",
                onClick = { selectedSource = "HuggingFace" },
                label = { Text("HuggingFace") }
            )
            FilterChip(
                selected = selectedSource == "ModelScope",
                onClick = { selectedSource = "ModelScope" },
                label = { Text("ModelScope") }
            )
            FilterChip(
                selected = selectedSource == "本地",
                onClick = { selectedSource = "本地" },
                label = { Text("本地") }
            )
        }
        
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(models) { model ->
                ModelCard(model)
            }
        }
    }
}

data class Model(
    val name: String,
    val size: String,
    val fileSize: String,
    val status: String
)

@Composable
fun ModelCard(model: Model) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = model.name,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("参数量：${model.size}", style = MaterialTheme.typography.bodySmall)
                    Text("文件大小：${model.fileSize}", style = MaterialTheme.typography.bodySmall)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(model.status, style = MaterialTheme.typography.bodySmall)
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = { }) {
                        Text("下载")
                    }
                }
            }
        }
    }
}

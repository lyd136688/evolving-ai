package org.omnibot.selfevolvingai.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModelsScreen() {
    var selectedSource by remember { mutableStateOf("ModelScope") }
    var downloadProgress by remember { mutableStateOf<Map<String, Float>>(emptyMap()) }
    var downloadedModels by remember { mutableStateOf(setOf<String>()) }
    val scope = rememberCoroutineScope()
    
    val models = listOf(
        ModelInfo(
            "Qwen2.5-7B-Instruct-GGUF-Q4_K_M",
            "7B",
            "4.2 GB",
            "通义千问，中文优秀，推荐首选",
            "modelscope"
        ),
        ModelInfo(
            "Qwen2.5-14B-Instruct-GGUF-Q4_K_M",
            "14B",
            "8.5 GB",
            "通义千问，更强推理，12GB+ 内存推荐",
            "modelscope"
        ),
        ModelInfo(
            "DeepSeek-V2-Lite-GGUF-Q4_K_M",
            "16B",
            "6.8 GB",
            "深度求索，性价比高",
            "modelscope"
        ),
        ModelInfo(
            "ChatGLM3-6B-GGUF-Q4_K_M",
            "6B",
            "3.8 GB",
            "智谱 AI，轻量高效，8GB 内存可用",
            "modelscope"
        ),
        ModelInfo(
            "Yi-1.5-9B-GGUF-Q4_K_M",
            "9B",
            "5.5 GB",
            "零一万物，平衡性好",
            "modelscope"
        )
    )
    
    LaunchedEffect(Unit) {
        val modelDir = File("/storage/models")
        if (modelDir.exists()) {
            downloadedModels = modelDir.listFiles()
                ?.filter { it.extension == "gguf" }
                ?.map { it.nameWithoutExtension }
                ?.toSet() ?: emptySet()
        }
    }
    
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("🤖 模型管理", style = MaterialTheme.typography.headlineMedium, modifier = Modifier.padding(bottom = 16.dp))
        
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("选择下载源", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(selected = selectedSource == "ModelScope", onClick = { selectedSource = "ModelScope" }, label = { Text("🇨 ModelScope") })
                    FilterChip(selected = selectedSource == "HuggingFace", onClick = { selectedSource = "HuggingFace" }, label = { Text("🌐 HuggingFace") })
                    FilterChip(selected = selectedSource == "本地", onClick = { selectedSource = "本地" }, label = { Text("📁 本地") })
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(models.filter { 
                selectedSource == "ModelScope" && it.source == "modelscope" ||
                selectedSource == "本地" && downloadedModels.contains(it.name)
            }) { model ->
                ModelCard(model, downloadProgress[model.name] ?: 0f, downloadedModels.contains(model.name))
            }
            
            item {
                Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("💡 下载说明", style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("• ModelScope: 阿里达摩院模型平台，国内访问快", style = MaterialTheme.typography.bodySmall)
                        Text("• 下载后的模型保存在 /storage/models 目录", style = MaterialTheme.typography.bodySmall)
                        Text("• 支持 GGUF 量化格式，节省存储空间", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}

data class ModelInfo(val name: String, val params: String, val fileSize: String, val description: String, val source: String)

@Composable
fun ModelCard(model: ModelInfo, progress: Float, isDownloaded: Boolean) {
    Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = model.name, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = model.description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text("参数量：${model.params}", style = MaterialTheme.typography.bodySmall)
                    Text("大小：${model.fileSize}", style = MaterialTheme.typography.bodySmall)
                }
                Column(horizontalAlignment = Alignment.End) {
                    if (isDownloaded) {
                        AssistChip(onClick = { }, label = { Text("✅ 已下载") })
                    } else if (progress > 0f && progress < 1f) {
                        LinearProgressIndicator(progress = progress, modifier = Modifier.width(100.dp))
                        Text("${(progress * 100).toInt()}%", style = MaterialTheme.typography.bodySmall)
                    } else {
                        Button(onClick = { }) { Text("⬇️ 下载") }
                    }
                }
            }
        }
    }
}

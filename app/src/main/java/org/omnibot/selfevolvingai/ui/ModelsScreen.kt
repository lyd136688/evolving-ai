package org.omnibot.selfevolvingai.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.omnibot.selfevolvingai.hardware.HardwareDetector

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModelsScreen() {
    var deviceInfo by remember { mutableStateOf<HardwareDetector.DeviceInfo?>(null) }
    var selectedSource by remember { mutableStateOf("国内源") }
    var downloadProgress by remember { mutableStateOf<Map<String, Float>>(emptyMap()) }
    
    val models = listOf(
        ModelInfo("Qwen2.5-7B-Instruct-GGUF", "7B", "4.2 GB", "通义千问，中文优秀", "modelscope"),
        ModelInfo("Qwen2.5-14B-Instruct-GGUF", "14B", "8.5 GB", "通义千问，更强推理", "modelscope"),
        ModelInfo("DeepSeek-V2-Lite-GGUF", "16B", "6.8 GB", "深度求索，性价比高", "modelscope"),
        ModelInfo("ChatGLM3-6B-GGUF", "6B", "3.8 GB", "智谱 AI，轻量高效", "modelscope"),
        ModelInfo("Yi-1.5-9B-GGUF", "9B", "5.5 GB", "零一万物，平衡性好", "huggingface")
    )
    
    LaunchedEffect(Unit) {
        deviceInfo = HardwareDetector.detect()
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            "🤖 模型管理",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        deviceInfo?.let { info ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = if (info.isHighEnd) 
                        MaterialTheme.colorScheme.primaryContainer 
                    else 
                        MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(" 设备信息", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("芯片：${info.soc} (${info.socCores}核)", style = MaterialTheme.typography.bodyMedium)
                    Text("内存：${info.ramGB} GB", style = MaterialTheme.typography.bodyMedium)
                    Text("系统：Android ${info.androidVersion}", style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Divider()
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("✅ 推荐模型：${info.recommendedModel}", style = MaterialTheme.typography.titleSmall)
                    Text("🚀 ${info.optimizationPack}", style = MaterialTheme.typography.bodySmall)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
        
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = selectedSource == "国内源",
                onClick = { selectedSource = "国内源" },
                label = { Text("🇨🇳 国内源") }
            )
            FilterChip(
                selected = selectedSource == "HuggingFace",
                onClick = { selectedSource = "HuggingFace" },
                label = { Text("🌐 HuggingFace") }
            )
            FilterChip(
                selected = selectedSource == "本地",
                onClick = { selectedSource = "本地" },
                label = { Text("📁 本地") }
            )
        }
        
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(models.filter { 
                selectedSource == "国内源" && it.source == "modelscope" ||
                selectedSource == "HuggingFace" && it.source == "huggingface" ||
                selectedSource == "本地"
            }) { model ->
                ModelCard(model, downloadProgress[model.name] ?: 0f)
            }
        }
    }
}

data class ModelInfo(
    val name: String,
    val params: String,
    val fileSize: String,
    val description: String,
    val source: String
)

@Composable
fun ModelCard(model: ModelInfo, progress: Float) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = model.name,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = model.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("参数量：${model.params}", style = MaterialTheme.typography.bodySmall)
                    Text("大小：${model.fileSize}", style = MaterialTheme.typography.bodySmall)
                }
                Column(horizontalAlignment = Alignment.End) {
                    if (progress > 0f && progress < 1f) {
                        LinearProgressIndicator(
                            progress = progress,
                            modifier = Modifier.width(100.dp)
                        )
                        Text("${(progress * 100).toInt()}%", style = MaterialTheme.typography.bodySmall)
                    } else if (progress >= 1f) {
                        AssistChip(onClick = { }, label = { Text("✅ 已下载") })
                    } else {
                        Button(onClick = { /* 开始下载 */ }) {
                            Text("⬇️ 下载")
                        }
                    }
                }
            }
        }
    }
}

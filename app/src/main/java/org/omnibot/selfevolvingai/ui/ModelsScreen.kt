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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModelsScreen() {
    var selectedSource by remember { mutableStateOf("ModelScope") }
    var downloadProgress by remember { mutableStateOf<Map<String, Float>>(emptyMap()) }
    var downloadedModels by remember { mutableStateOf(setOf<String>()) }
    
    val models = listOf(
        ModelInfo("Qwen2.5-7B-Instruct-GGUF", "7B", "4.2 GB", "通义千问，中文优秀", "modelscope", "https://modelscope.cn/models/qwen/Qwen2.5-7B-Instruct-GGUF"),
        ModelInfo("Qwen2.5-14B-Instruct-GGUF", "14B", "8.5 GB", "通义千问，更强推理", "modelscope", "https://modelscope.cn/models/qwen/Qwen2.5-14B-Instruct-GGUF"),
        ModelInfo("DeepSeek-V2-Lite-GGUF", "16B", "6.8 GB", "深度求索，性价比高", "modelscope", "https://modelscope.cn/models/deepseek/DeepSeek-V2-Lite"),
        ModelInfo("ChatGLM3-6B-GGUF", "6B", "3.8 GB", "智谱 AI，轻量高效", "modelscope", "https://modelscope.cn/models/ZhipuAI/chatglm3-6b"),
        ModelInfo("Yi-1.5-9B-GGUF", "9B", "5.5 GB", "零一万物，平衡性好", "modelscope", "https://modelscope.cn/models/01ai/Yi-1.5-9B"),
        ModelInfo("Llama-3-8B-Instruct-GGUF", "8B", "4.8 GB", "Meta 官方，英文优秀", "huggingface", "https://huggingface.co/meta-llama/Llama-3-8B-Instruct"),
        ModelInfo("Gemma-2-9B-Instruct-GGUF", "9B", "5.2 GB", "Google，多语言支持", "huggingface", "https://huggingface.co/google/gemma-2-9b"),
        ModelInfo("Mistral-7B-Instruct-GGUF", "7B", "4.1 GB", "Mistral AI，开源优秀", "huggingface", "https://huggingface.co/mistralai/Mistral-7B-Instruct"),
        ModelInfo("Baichuan2-7B-GGUF", "7B", "4.0 GB", "百川智能，中文优化", "huggingface", "https://huggingface.co/baichuan-inc/Baichuan2-7B"),
        ModelInfo("Aquila2-7B-GGUF", "7B", "4.3 GB", "智源研究院，开源", "huggingface", "https://huggingface.co/BAAI/Aquila2-7B")
    )
    
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
        
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("选择下载源", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = selectedSource == "ModelScope",
                        onClick = { selectedSource = "ModelScope" },
                        label = { Text("🇨🇳 ModelScope") }
                    )
                    FilterChip(
                        selected = selectedSource == "HuggingFace",
                        onClick = { selectedSource = "HuggingFace" },
                        label = { Text("🌐 HuggingFace") }
                    )
                    FilterChip(
                        selected = selectedSource == "智谱",
                        onClick = { selectedSource = "智谱" },
                        label = { Text("智谱 AI") }
                    )
                    FilterChip(
                        selected = selectedSource == "本地",
                        onClick = { selectedSource = "本地" },
                        label = { Text("📁 本地") }
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(models.filter { 
                selectedSource == "ModelScope" && it.source == "modelscope" ||
                selectedSource == "HuggingFace" && it.source == "huggingface" ||
                selectedSource == "智谱" && it.source == "modelscope" ||
                selectedSource == "本地" && downloadedModels.contains(it.name)
            }) { model ->
                ModelCard(
                    model = model,
                    progress = downloadProgress[model.name] ?: 0f,
                    isDownloaded = downloadedModels.contains(model.name),
                    onDownload = {
                        downloadProgress = downloadProgress + (model.name to 0.1f)
                    }
                )
            }
            
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.infoContainer.copy(alpha = 0.3f)
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("💡 下载说明", style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("• ModelScope: 阿里达摩院模型平台，国内访问快", style = MaterialTheme.typography.bodySmall)
                        Text("• HuggingFace: 国际最大模型社区，需要网络", style = MaterialTheme.typography.bodySmall)
                        Text("• 下载后的模型保存在 /storage/models 目录", style = MaterialTheme.typography.bodySmall)
                        Text("• 支持 GGUF 量化格式，节省存储空间", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}

data class ModelInfo(
    val name: String,
    val params: String,
    val fileSize: String,
    val description: String,
    val source: String,
    val downloadUrl: String
)

@Composable
fun ModelCard(model: ModelInfo, progress: Float, isDownloaded: Boolean, onDownload: () -> Unit) {
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
                    if (isDownloaded) {
                        AssistChip(onClick = { }, label = { Text("✅ 已下载") })
                    } else if (progress > 0f && progress < 1f) {
                        LinearProgressIndicator(
                            progress = progress,
                            modifier = Modifier.width(100.dp)
                        )
                        Text("${(progress * 100).toInt()}%", style = MaterialTheme.typography.bodySmall)
                    } else {
                        Button(onClick = onDownload) {
                            Text("⬇️ 下载")
                        }
                    }
                }
            }
        }
    }
}

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
import org.omnibot.selfevolvingai.tools.ToolManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilesScreen() {
    var files by remember { mutableStateOf(listOf<FileInfo>()) }
    var currentPath by remember { mutableStateOf("/workspace") }
    var isLoading by remember { mutableStateOf(false) }
    var serverUrl by remember { mutableStateOf("https://api.omnibot.cn.com") }
    var apiKey by remember { mutableStateOf("") }
    
    val scope = rememberCoroutineScope()
    val toolManager = remember { ToolManager(serverUrl, apiKey) }
    
    LaunchedEffect(currentPath) {
        isLoading = true
        val result = toolManager.executeCommand("ls -la $currentPath")
        files = parseLsOutput(result)
        isLoading = false
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            "📁 文件管理",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        Card(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(currentPath, style = MaterialTheme.typography.bodyMedium)
                IconButton(onClick = {
                    if (currentPath != "/workspace") {
                        currentPath = currentPath.substringBeforeLast("/")
                        if (currentPath.isEmpty()) currentPath = "/"
                    }
                }) {
                    Text("📤")
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(files) { file ->
                    FileCard(file, onNavigate = {
                        if (file.type == "dir") {
                            currentPath = "$currentPath/${file.name}"
                        }
                    })
                }
            }
        }
    }
}

data class FileInfo(val name: String, val type: String, val size: String)

fun parseLsOutput(output: String): List<FileInfo> {
    return output.lines()
        .filter { it.trim().isNotEmpty() }
        .mapNotNull { line ->
            val parts = line.split(Regex("\\s+"))
            if (parts.size >= 9) {
                val type = if (parts[0].startsWith("d")) "dir" else "file"
                val name = parts.last()
                val size = parts[4]
                FileInfo(name, type, size)
            } else null
        }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FileCard(file: FileInfo, onNavigate: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onNavigate
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row {
                Text(
                    text = if (file.type == "dir") "📁" else "",
                    modifier = Modifier.padding(end = 12.dp)
                )
                Text(file.name, style = MaterialTheme.typography.bodyMedium)
            }
            Text(file.size, style = MaterialTheme.typography.bodySmall)
        }
    }
}

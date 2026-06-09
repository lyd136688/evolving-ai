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
fun FilesScreen() {
    var files by remember { mutableStateOf(listOf<FileInfo>()) }
    var currentPath by remember { mutableStateOf("/workspace") }
    
    LaunchedEffect(currentPath) {
        files = listOf(
            FileInfo("test.py", "file", "1.2 KB"),
            FileInfo("config.json", "file", "0.5 KB"),
            FileInfo("memory", "dir", "-"),
            FileInfo("skills", "dir", "-")
        )
    }
    
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("📁 文件管理", style = MaterialTheme.typography.headlineMedium, modifier = Modifier.padding(bottom = 16.dp))
        
        Card(modifier = Modifier.fillMaxWidth()) {
            Row(modifier = Modifier.fillMaxWidth().padding(12.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(currentPath, style = MaterialTheme.typography.bodyMedium)
                IconButton(onClick = { if (currentPath != "/workspace") { currentPath = currentPath.substringBeforeLast("/") } }) { Text("📤") }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(files) { file ->
                FileCard(file, onNavigate = { if (file.type == "dir") { currentPath = "$currentPath/${file.name}" } })
            }
        }
    }
}

data class FileInfo(val name: String, val type: String, val size: String)

@Composable
fun FileCard(file: FileInfo, onNavigate: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth(), onClick = onNavigate) {
        Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            Row { Text(text = if (file.type == "dir") "📁" else "📄", modifier = Modifier.padding(end = 12.dp)); Text(file.name, style = MaterialTheme.typography.bodyMedium) }
            Text(file.size, style = MaterialTheme.typography.bodySmall)
        }
    }
}

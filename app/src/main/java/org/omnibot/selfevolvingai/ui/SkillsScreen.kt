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
fun SkillsScreen() {
    var skills by remember { mutableStateOf(listOf<Skill>()) }
    var isLoading by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    
    LaunchedEffect(Unit) {
        isLoading = true
        skills = listOf(
            Skill("find-install-skills", "技能安装", "查找和安装 Omnibot 技能", true),
            Skill("self-improving-agent", "自我改进", "记录失败和最佳实践", true),
            Skill("skill-creator", "技能创建", "创建和更新技能指南", false)
        )
        isLoading = false
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            "📚 技能中心",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            placeholder = { Text("搜索技能...") },
            singleLine = true,
            leadingIcon = { Text("🔍") }
        )
        
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
                items(skills.filter { searchQuery.isBlank() || it.name.contains(searchQuery, ignoreCase = true) }) { skill ->
                    SkillCard(skill)
                }
                
                item {
                    Button(
                        onClick = { },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp)
                    ) {
                        Text("🔄 刷新技能列表")
                    }
                }
            }
        }
    }
}

data class Skill(
    val id: String,
    val name: String,
    val description: String,
    val installed: Boolean
)

@Composable
fun SkillCard(skill: Skill) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = skill.name,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = skill.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                if (skill.installed) {
                    AssistChip(
                        onClick = { },
                        label = { Text("已安装") }
                    )
                } else {
                    AssistChip(
                        onClick = { },
                        label = { Text("安装") }
                    )
                }
            }
        }
    }
}

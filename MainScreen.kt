package org.omnibot.selfevolvingai.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import org.omnibot.selfevolvingai.domain.model.Knowledge
import org.omnibot.selfevolvingai.domain.model.EvolutionStatus

/**
 * 主屏幕 - Compose UI
 * 显示自进化 AI 的核心功能
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: MainViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var searchText by remember { mutableStateOf("") }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "🧠 自进化 AI",
                        fontWeight = FontWeight.Bold
                    ) 
                },
                subtitle = {
                    Text(
                        "状态：${uiState.status}",
                        style = MaterialTheme.typography.bodySmall
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            // 状态卡片
            StatusCard(uiState.evolutionStatus)
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 搜索框
            OutlinedTextField(
                value = searchText,
                onValueChange = { searchText = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("搜索知识...") },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = "搜索")
                },
                trailingIcon = {
                    if (searchText.isNotEmpty()) {
                        IconButton(onClick = { searchText = "" }) {
                            Icon(Icons.Default.Clear, contentDescription = "清除")
                        }
                    }
                },
                singleLine = true
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 快捷操作
            QuickActions(
                onLearn = { viewModel.startLearning(it) },
                onSearch = { viewModel.searchKnowledge(searchText) },
                onEvaluate = { viewModel.runSelfEvaluation() }
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 知识列表
            Text(
                "已学知识",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(uiState.knowledgeList) { knowledge ->
                    KnowledgeCard(knowledge)
                }
            }
            
            // 加载指示器
            if (uiState.isLoading) {
                Spacer(modifier = Modifier.height(16.dp))
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun StatusCard(status: EvolutionStatus) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "自进化状态",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                AssistChip(
                    onClick = { },
                    label = { Text(status.status) },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Refresh,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                )
            }
            
            Divider()
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatusItem("学习次数", status.learningCount.toString())
                StatusItem("知识条目", status.knowledgeCount.toString())
                StatusItem("评估得分", "${status.evaluationScore}%")
            }
        }
    }
}

@Composable
fun StatusItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun QuickActions(
    onLearn: (String) -> Unit,
    onSearch: () -> Unit,
    onEvaluate: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FilledTonalButton(
            onClick = { onLearn("python") },
            modifier = Modifier.weight(1f)
        ) {
            Icon(Icons.Default.AutoAwesome, contentDescription = null)
            Spacer(modifier = Modifier.width(4.dp))
            Text("学习")
        }
        
        FilledTonalButton(
            onClick = onSearch,
            modifier = Modifier.weight(1f)
        ) {
            Icon(Icons.Default.Search, contentDescription = null)
            Spacer(modifier = Modifier.width(4.dp))
            Text("搜索")
        }
        
        FilledTonalButton(
            onClick = onEvaluate,
            modifier = Modifier.weight(1f)
        ) {
            Icon(Icons.Default.Analytics, contentDescription = null)
            Spacer(modifier = Modifier.width(4.dp))
            Text("评估")
        }
    }
}

@Composable
fun KnowledgeCard(knowledge: Knowledge) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                knowledge.title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Medium
            )
            
            Text(
                knowledge.source,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                knowledge.tags.take(3).forEach { tag ->
                    AssistChip(
                        onClick = { },
                        label = { Text("#$tag", style = MaterialTheme.typography.labelSmall) }
                    )
                }
            }
        }
    }
}

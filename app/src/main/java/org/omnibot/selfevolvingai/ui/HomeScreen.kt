package org.omnibot.selfevolvingai.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun HomeScreen() {
    val features = listOf(
        Feature("💬", "AI 对话", "与 AI 助手自然对话"),
        Feature("📚", "技能系统", "自动下载和加载技能"),
        Feature("", "模型管理", "下载本地量化大模型"),
        Feature("💻", "终端执行", "运行 Shell 命令"),
        Feature("📁", "文件管理", "读写工作区文件"),
        Feature("🌐", "浏览器", "网页浏览和内容提取"),
        Feature("", "记忆系统", "长期/短期记忆"),
        Feature("⏰", "定时任务", "创建自动化任务"),
        Feature("📅", "日历闹钟", "管理日程提醒"),
        Feature("🎵", "音乐播放", "系统级音乐控制")
    )
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            "🧠 自进化 AI 系统",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            "版本：2.0.0 | 状态：在线",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 24.dp)
        )
        
        Text(
            "功能列表",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(features) { feature ->
                FeatureCard(feature)
            }
        }
    }
}

data class Feature(val icon: String, val title: String, val desc: String)

@Composable
fun FeatureCard(feature: Feature) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = feature.icon,
                style = MaterialTheme.typography.headlineMedium
            )
            Column {
                Text(
                    text = feature.title,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = feature.desc,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

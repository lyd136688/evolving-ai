package org.omnibot.selfevolvingai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                App()
            }
        }
    }
}

@Composable
fun App() {
    val navController = rememberNavController()
    var selectedTab by remember { mutableStateOf(0) }
    
    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Text("🏠") },
                    label = { Text("首页") },
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0; navController.navigate("home") }
                )
                NavigationBarItem(
                    icon = { Text("💬") },
                    label = { Text("对话") },
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1; navController.navigate("chat") }
                )
                NavigationBarItem(
                    icon = { Text("📚") },
                    label = { Text("技能") },
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2; navController.navigate("skills") }
                )
                NavigationBarItem(
                    icon = { Text("🤖") },
                    label = { Text("模型") },
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3; navController.navigate("models") }
                )
                NavigationBarItem(
                    icon = { Text("⚙️") },
                    label = { Text("设置") },
                    selected = selectedTab == 4,
                    onClick = { selectedTab = 4; navController.navigate("settings") }
                )
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(padding)
        ) {
            composable("home") { HomeScreen() }
            composable("chat") { ChatScreen() }
            composable("skills") { SkillsScreen() }
            composable("models") { ModelsScreen() }
            composable("settings") { SettingsScreen() }
        }
    }
}

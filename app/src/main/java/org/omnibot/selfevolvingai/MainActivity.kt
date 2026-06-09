package org.omnibot.selfevolvingai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.omnibot.selfevolvingai.ui.HomeScreen
import org.omnibot.selfevolvingai.ui.ChatScreen
import org.omnibot.selfevolvingai.ui.TasksScreen
import org.omnibot.selfevolvingai.ui.TerminalScreen
import org.omnibot.selfevolvingai.ui.SettingsScreen

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
                    icon = { Text("") },
                    label = { Text("任务") },
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2; navController.navigate("tasks") }
                )
                NavigationBarItem(
                    icon = { Text("💻") },
                    label = { Text("终端") },
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3; navController.navigate("terminal") }
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
            composable("tasks") { TasksScreen() }
            composable("terminal") { TerminalScreen() }
            composable("settings") { SettingsScreen() }
        }
    }
}

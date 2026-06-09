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
                    icon = { Text("📋") },
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
                    icon = { Text("️") },
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

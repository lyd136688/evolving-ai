package org.omnibot.selfevolvingai.tools

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ToolManager(
    private val serverUrl: String,
    private val apiKey: String
) {
    private val client = ToolClient(serverUrl, apiKey)
    
    suspend fun executeCommand(command: String): String = withContext(Dispatchers.IO) {
        client.terminalExecute(command).getOrDefault("执行失败")
    }
    
    suspend fun readFile(path: String): String = withContext(Dispatchers.IO) {
        client.fileRead(path).getOrDefault("读取失败")
    }
    
    suspend fun writeFile(path: String, content: String): String = withContext(Dispatchers.IO) {
        client.fileWrite(path, content).getOrDefault("写入失败")
    }
    
    suspend fun searchMemory(query: String): String = withContext(Dispatchers.IO) {
        client.memorySearch(query).getOrDefault("搜索失败")
    }
    
    suspend fun openUrl(url: String): String = withContext(Dispatchers.IO) {
        client.browserNavigate(url).getOrDefault("打开失败")
    }
    
    suspend fun listApps(query: String = ""): String = withContext(Dispatchers.IO) {
        client.contextAppsQuery(query).getOrDefault("查询失败")
    }
}

package org.omnibot.selfevolvingai.tools

import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException

class ToolClient(
    private val serverUrl: String,
    private val apiKey: String
) {
    private val client = OkHttpClient().newBuilder()
        .connectTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
        .readTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
        .build()
    
    private val mediaType = "application/json".toMediaType()
    
    suspend fun terminalExecute(command: String, timeoutSeconds: Int = 60): Result<String> {
        return executeTool("terminal_execute", """
            {
                "tool_title": "执行命令",
                "command": "$command",
                "timeoutSeconds": $timeoutSeconds
            }
        """)
    }
    
    suspend fun fileRead(path: String, maxChars: Int = 8000): Result<String> {
        return executeTool("file_read", """
            {
                "tool_title": "读取文件",
                "path": "$path",
                "maxChars": $maxChars
            }
        """)
    }
    
    suspend fun fileWrite(path: String, content: String): Result<String> {
        return executeTool("file_write", """
            {
                "tool_title": "写入文件",
                "path": "$path",
                "content": "${content.replace("\"", "\\\"").replace("\n", "\\n")}"
            }
        """)
    }
    
    suspend fun subagentDispatch(
        tasks: List<String>,
        concurrency: Int = 2,
        mergeInstruction: String = ""
    ): Result<String> {
        val tasksJson = tasks.joinToString(",") { task -> 
            "\"${task.replace("\"", "\\\"").replace("\n", "\\n")}\"" 
        }
        return executeTool("subagent_dispatch", """
            {
                "tool_title": "任务分发",
                "tasks": [$tasksJson],
                "concurrency": $concurrency,
                "mergeInstruction": "$mergeInstruction"
            }
        """)
    }
    
    suspend fun memorySearch(query: String, limit: Int = 8): Result<String> {
        return executeTool("memory_search", """
            {
                "tool_title": "搜索记忆",
                "query": "$query",
                "limit": $limit
            }
        """)
    }
    
    suspend fun contextAppsQuery(query: String = "", limit: Int = 20): Result<String> {
        return executeTool("context_apps_query", """
            {
                "tool_title": "查询应用",
                "query": "$query",
                "limit": $limit
            }
        """)
    }
    
    private suspend fun executeTool(toolName: String, params: String): Result<String> {
        return try {
            val jsonBody = """
                {
                    "tool": "$toolName",
                    "params": $params,
                    "api_key": "$apiKey"
                }
            """.trimIndent()
            
            val request = Request.Builder()
                .url("$serverUrl/api/execute_tool")
                .post(jsonBody.toRequestBody(mediaType))
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer $apiKey")
                .build()
            
            val response = client.newCall(request).execute()
            
            if (response.isSuccessful) {
                val body = response.body?.string() ?: return Result.failure(IOException("空响应"))
                Result.success(body)
            } else {
                Result.failure(IOException("工具执行失败：${response.code}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

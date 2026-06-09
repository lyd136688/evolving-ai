package org.omnibot.selfevolvingai.tools

import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

class ToolClient(
    private val serverUrl: String,
    private val apiKey: String
) {
    private val client = OkHttpClient()
    private val mediaType = "application/json".toMediaType()
    
    // 文件读取
    suspend fun fileRead(path: String, maxChars: Int = 8000): Result<String> {
        return executeTool("file_read", """
            {
                "tool_title": "读取文件",
                "path": "$path",
                "maxChars": $maxChars
            }
        """)
    }
    
    // 文件写入
    suspend fun fileWrite(path: String, content: String): Result<String> {
        return executeTool("file_write", """
            {
                "tool_title": "写入文件",
                "path": "$path",
                "content": "${content.replace("\"", "\\\"")}"
            }
        """)
    }
    
    // 终端执行
    suspend fun terminalExecute(command: String, timeoutSeconds: Int = 60): Result<String> {
        return executeTool("terminal_execute", """
            {
                "tool_title": "执行命令",
                "command": "$command",
                "timeoutSeconds": $timeoutSeconds
            }
        """)
    }
    
    // 应用查询
    suspend fun contextAppsQuery(query: String = "", limit: Int = 20): Result<String> {
        return executeTool("context_apps_query", """
            {
                "tool_title": "查询应用",
                "query": "$query",
                "limit": $limit
            }
        """)
    }
    
    // 浏览器导航
    suspend fun browserNavigate(url: String): Result<String> {
        return executeTool("browser_use", """
            {
                "tool_title": "打开网页",
                "action": "navigate",
                "url": "$url"
            }
        """)
    }
    
    // 浏览器截图
    suspend fun browserScreenshot(): Result<String> {
        return executeTool("browser_use", """
            {
                "tool_title": "网页截图",
                "action": "screenshot",
                "read_image": true
            }
        """)
    }
    
    // 浏览器点击
    suspend fun browserClick(selector: String): Result<String> {
        return executeTool("browser_use", """
            {
                "tool_title": "点击元素",
                "action": "click",
                "selector": "$selector"
            }
        """)
    }
    
    // 浏览器输入
    suspend fun browserType(selector: String, text: String): Result<String> {
        return executeTool("browser_use", """
            {
                "tool_title": "输入文本",
                "action": "type",
                "selector": "$selector",
                "text": "$text"
            }
        """)
    }
    
    // 记忆搜索
    suspend fun memorySearch(query: String, limit: Int = 8): Result<String> {
        return executeTool("memory_search", """
            {
                "tool_title": "搜索记忆",
                "query": "$query",
                "limit": $limit
            }
        """)
    }
    
    // 记忆写入
    suspend fun memoryWriteDaily(text: String): Result<String> {
        return executeTool("memory_write_daily", """
            {
                "tool_title": "写入记忆",
                "text": "$text"
            }
        """)
    }
    
    // 创建定时任务
    suspend fun scheduleTaskCreate(title: String, goal: String, fixedTime: String): Result<String> {
        return executeTool("schedule_task_create", """
            {
                "tool_title": "创建任务",
                "title": "$title",
                "goal": "$goal",
                "scheduleType": "fixed_time",
                "fixedTime": "$fixedTime",
                "repeatDaily": false,
                "targetKind": "vlm",
                "enabled": true
            }
        """)
    }
    
    // 创建闹钟
    suspend fun alarmCreate(title: String, message: String, triggerAt: String): Result<String> {
        return executeTool("alarm_reminder_create", """
            {
                "tool_title": "创建闹钟",
                "mode": "exact_alarm",
                "title": "$title",
                "message": "$message",
                "triggerAt": "$triggerAt"
            }
        """)
    }
    
    // 图片生成
    suspend fun imageGenerate(prompt: String, outputPath: String): Result<String> {
        return executeTool("image_generate", """
            {
                "tool_title": "生成图片",
                "prompt": "$prompt",
                "outputPath": "$outputPath"
            }
        """)
    }
    
    // 通用工具执行
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
                val body = response.body?.string() ?: return Result.failure(Exception("空响应"))
                Result.success(body)
            } else {
                Result.failure(Exception("工具执行失败：${response.code}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

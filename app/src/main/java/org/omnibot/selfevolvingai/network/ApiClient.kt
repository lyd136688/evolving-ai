package org.omnibot.selfevolvingai.network

import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException

class ApiService {
    
    companion object {
        private val MEDIA_TYPE = "application/json; charset=utf-8".toMediaType()
        
        val API_PROVIDERS = mapOf(
            "DeepSeek" to "https://api.deepseek.com/v1",
            "智谱 AI" to "https://open.bigmodel.cn/api/paas/v4",
            "通义千问" to "https://dashscope.aliyuncs.com/compatible-mode/v1",
            "OpenAI" to "https://api.openai.com/v1",
            "月之暗面" to "https://api.moonshot.cn/v1"
        )
    }
    
    data class ChatMessage(val role: String, val content: String)
    
    fun chat(apiKey: String, baseUrl: String, model: String, messages: List<ChatMessage>): Result<String> {
        return try {
            val client = OkHttpClient().newBuilder()
                .connectTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
                .readTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
                .build()
            
            val messagesJson = messages.joinToString(",") { 
                """{"role":"${it.role}","content":"${it.content.replace("\"", "\\\"").replace("\n", "\\n")}"}""" 
            }
            
            val jsonBody = """{"model": "$model", "messages": [$messagesJson], "max_tokens": 2048, "stream": false}""".trimIndent()
            
            val request = Request.Builder()
                .url("$baseUrl/chat/completions")
                .addHeader("Authorization", "Bearer $apiKey")
                .addHeader("Content-Type", "application/json")
                .post(jsonBody.toRequestBody(MEDIA_TYPE))
                .build()
            
            val response = client.newCall(request).execute()
            
            if (response.isSuccessful) {
                val responseBody = response.body?.string() ?: return Result.failure(IOException("空响应"))
                Result.success(responseBody)
            } else {
                Result.failure(IOException("API 错误：${response.code}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

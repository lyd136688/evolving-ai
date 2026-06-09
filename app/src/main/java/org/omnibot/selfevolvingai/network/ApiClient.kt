package org.omnibot.selfevolvingai.network

import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException

class ApiService {
    
    companion object {
        private val MEDIA_TYPE = "application/json; charset=utf-8".toMediaType()
        
        val API_PROVIDERS = mapOf(
            "OpenAI" to "https://api.openai.com/v1",
            "DeepSeek" to "https://api.deepseek.com/v1",
            "智谱 AI" to "https://open.bigmodel.cn/api/paas/v4",
            "通义千问" to "https://dashscope.aliyuncs.com/compatible-mode/v1",
            "文心一言" to "https://qianfan.baidubce.com/v2",
            "月之暗面" to "https://api.moonshot.cn/v1"
        )
    }
    
    data class ChatMessage(val role: String, val content: String)
    data class ChatRequest(val model: String, val messages: List<ChatMessage>, val maxTokens: Int = 2048)
    
    fun chat(apiKey: String, baseUrl: String, model: String, messages: List<ChatMessage>): Result<String> {
        return try {
            val client = OkHttpClient().newBuilder()
                .connectTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
                .readTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
                .build()
            
            val messagesJson = messages.joinToString(",") { 
                """{"role":"${it.role}","content":"${it.content.replace("\"", "\\\"")}"}""" 
            }
            
            val jsonBody = """{
                "model": "$model",
                "messages": [$messagesJson],
                "max_tokens": 2048,
                "stream": false
            }""".trimIndent()
            
            val request = Request.Builder()
                .url("$baseUrl/chat/completions")
                .addHeader("Authorization", "Bearer $apiKey")
                .addHeader("Content-Type", "application/json")
                .post(jsonBody.toRequestBody(MEDIA_TYPE))
                .build()
            
            val response = client.newCall(request).execute()
            
            if (response.isSuccessful) {
                val responseBody = response.body?.string() ?: return Result.failure(IOException("空响应"))
                val content = parseResponseContent(responseBody)
                Result.success(content)
            } else {
                Result.failure(IOException("API 错误：${response.code}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    private fun parseResponseContent(json: String): String {
        return try {
            val gson = com.google.gson.Gson()
            val jsonObject = gson.fromJson(json, com.google.gson.JsonObject::class.java)
            jsonObject.getAsJsonObject("choices")
                ?.getAsJsonArray("choices")
                ?.get(0)?.asJsonObject
                ?.getAsJsonObject("message")
                ?.get("content")?.asString ?: "无法解析响应"
        } catch (e: Exception) {
            json
        }
    }
}

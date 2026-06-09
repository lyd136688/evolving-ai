package org.omnibot.selfevolvingai.network

import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException

class ApiClient(
    private val apiKey: String,
    private val baseUrl: String = "https://api.openai.com/v1"
) {
    private val client = OkHttpClient()
    private val mediaType = "application/json; charset=utf-8".toMediaType()
    
    data class ChatRequest(
        val model: String,
        val messages: List<Message>,
        val maxTokens: Int = 1024
    )
    
    data class Message(
        val role: String,
        val content: String
    )
    
    data class ChatResponse(
        val choices: List<Choice>,
        val usage: Usage?
    )
    
    data class Choice(
        val message: Message,
        val finishReason: String?
    )
    
    data class Usage(
        val promptTokens: Int,
        val completionTokens: Int,
        val totalTokens: Int
    )
    
    fun chat(request: ChatRequest): Result<String> {
        return try {
            val json = buildJson(request)
            val body = json.toRequestBody(mediaType)
            
            val httpRequest = Request.Builder()
                .url("$baseUrl/chat/completions")
                .addHeader("Authorization", "Bearer $apiKey")
                .addHeader("Content-Type", "application/json")
                .post(body)
                .build()
            
            val response = client.newCall(httpRequest).execute()
            
            if (response.isSuccessful) {
                response.body?.string()?.let {
                    Result.success(parseResponse(it))
                } ?: Result.failure(IOException("Empty response"))
            } else {
                Result.failure(IOException("API Error: ${response.code}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    private fun buildJson(request: ChatRequest): String {
        val messagesJson = request.messages.joinToString(",") { 
            """{"role":"${it.role}","content":"${it.content}"}""" 
        }
        return """{
            "model": "${request.model}",
            "messages": [$messagesJson],
            "max_tokens": ${request.maxTokens}
        }"""
    }
    
    private fun parseResponse(json: String): String {
        // 简单解析，实际应使用 Gson
        return json
    }
    
    suspend fun fetchSkills(): Result<List<SkillInfo>> {
        return try {
            val httpRequest = Request.Builder()
                .url("https://raw.githubusercontent.com/omnibot-ai/omnibot/main/skills/index.json")
                .get()
                .build()
            
            val response = client.newCall(httpRequest).execute()
            
            if (response.isSuccessful) {
                Result.success(emptyList()) // TODO: 解析技能列表
            } else {
                Result.failure(IOException("Fetch failed: ${response.code}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    data class SkillInfo(
        val id: String,
        val name: String,
        val description: String,
        val url: String
    )
}

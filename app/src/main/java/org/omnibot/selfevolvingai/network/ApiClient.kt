package org.omnibot.selfevolvingai.network

import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException

class ApiClient(private val apiKey: String, private val baseUrl: String) {
    
    private val client = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("Authorization", "Bearer $apiKey")
                .addHeader("Content-Type", "application/json")
                .build()
            chain.proceed(request)
        }
        .build()
    
    fun chat(messages: List<Map<String, String>>, model: String, callback: (String?, String?) -> Unit) {
        val json = JSONObject()
        json.put("model", model)
        json.put("messages", org.json.JSONArray(messages.map { JSONObject(it) }))
        
        val request = Request.Builder()
            .url("$baseUrl/chat/completions")
            .post(json.toString().toRequestBody("application/json".toMediaType()))
            .build()
        
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback(null, e.message)
            }
            
            override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string()
                if (response.isSuccessful && body != null) {
                    try {
                        val result = JSONObject(body)
                        val content = result
                            .getJSONObject("choices")
                            .getJSONObject(0)
                            .getJSONObject("message")
                            .getString("content")
                        callback(content, null)
                    } catch (e: Exception) {
                        callback(null, e.message)
                    }
                } else {
                    callback(null, body ?: "请求失败")
                }
            }
        })
    }
    
    fun downloadFile(url: String, savePath: String, callback: (Boolean, String?) -> Unit) {
        val request = Request.Builder().url(url).build()
        
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback(false, e.message)
            }
            
            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    // TODO: 保存文件到 savePath
                    callback(true, null)
                } else {
                    callback(false, "下载失败：${response.code}")
                }
            }
        })
    }
}

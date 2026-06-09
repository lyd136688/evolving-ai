package org.omnibot.selfevolvingai.llm

import android.content.Context
import de.kherud.llama.LlamaContext
import de.kherud.llama.LlamaModel
import de.kherud.llama.ModelParameters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class LocalLLMEngine(private val context: Context) {
    
    companion object {
        const val MODEL_PATH = "/storage/models/Qwen2.5-7B-Instruct-GGUF-Q4_K_M.gguf"
        const val CACHE_SIZE = 16 * 1024 * 1024
        const val MAX_CONTEXT = 8192
        const val MEMORY_THRESHOLD = 0.85f
    }
    
    private var model: LlamaModel? = null
    private var ctx: LlamaContext? = null
    private var isLoaded = false
    private var currentContextTokens = 0
    private val memoryCache = mutableListOf<MemoryEntry>()
    private val longTermMemory = mutableListOf<LongTermMemoryEntry>()
    
    data class MemoryEntry(val role: String, val content: String, val tokens: Int, val timestamp: Long)
    data class LongTermMemoryEntry(val summary: String, val keywords: List<String>, val timestamp: Long)
    
    suspend fun loadModel(modelPath: String = MODEL_PATH): Result<String> = withContext(Dispatchers.IO) {
        try {
            val modelFile = File(modelPath)
            if (!modelFile.exists()) {
                return@withContext Result.failure(Exception("模型文件不存在：$modelPath\n请前往模型管理下载"))
            }
            
            val params = ModelParameters()
            params.modelPath = modelFile.absolutePath
            params.nContext = MAX_CONTEXT
            params.nBatch = 512
            params.nThreads = Runtime.getRuntime().availableProcessors()
            
            model = LlamaModel(params)
            ctx = LlamaContext(model!!)
            isLoaded = true
            
            Result.success("模型加载成功：${modelFile.name}")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun generate(prompt: String, maxTokens: Int = 2048): Result<String> = withContext(Dispatchers.IO) {
        if (!isLoaded) {
            return@withContext Result.failure(Exception("模型未加载"))
        }
        
        try {
            addMemoryEntry("user", prompt)
            checkAndCompressMemory()
            
            val fullPrompt = buildPrompt()
            val response = ctx?.generate(fullPrompt, maxTokens) ?: ""
            
            addMemoryEntry("assistant", response)
            
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    private fun buildPrompt(): String {
        val sb = StringBuilder()
        sb.append("<|im_start|>system\n你是一个自进化 AI 助手，具有自学习、自改正、自构架能力。记忆缓存 16MB，支持超长上下文。\n<|im_end|>\n")
        
        memoryCache.forEach { entry ->
            sb.append("<|im_start|>${entry.role}\n${entry.content}<|im_end|>\n")
        }
        
        return sb.toString()
    }
    
    private fun addMemoryEntry(role: String, content: String) {
        val tokens = estimateTokens(content)
        memoryCache.add(MemoryEntry(role, content, tokens, System.currentTimeMillis()))
        currentContextTokens += tokens
    }
    
    private fun checkAndCompressMemory() {
        if (currentContextTokens > MAX_CONTEXT * MEMORY_THRESHOLD) {
            compressMemory()
        }
    }
    
    private fun compressMemory() {
        if (memoryCache.size < 4) return
        
        val oldestEntries = memoryCache.take(4)
        val summary = summarizeConversation(oldestEntries)
        val keywords = extractKeywords(oldestEntries)
        
        longTermMemory.add(LongTermMemoryEntry(summary, keywords, System.currentTimeMillis()))
        
        memoryCache.removeAll(oldestEntries)
        currentContextTokens -= oldestEntries.sumOf { it.tokens }
        
        saveLongTermMemory()
    }
    
    private fun summarizeConversation(entries: List<MemoryEntry>): String {
        val content = entries.joinToString("\n") { "${it.role}: ${it.content}" }
        return "对话摘要：${content.take(500)}..."
    }
    
    private fun extractKeywords(entries: List<MemoryEntry>): List<String> {
        val text = entries.joinToString(" ") { it.content }
        return text.split(" ", "\n", "，", "。")
            .filter { it.length > 2 }
            .distinct()
            .take(20)
    }
    
    private fun saveLongTermMemory() {
        val memoryDir = File(context.filesDir, "memory")
        memoryDir.mkdirs()
        
        val memoryFile = File(memoryDir, "long_term_memory.json")
        val gson = com.google.gson.Gson()
        memoryFile.writeText(gson.toJson(longTermMemory))
    }
    
    fun loadLongTermMemory() {
        val memoryFile = File(context.filesDir, "memory/long_term_memory.json")
        if (memoryFile.exists()) {
            val gson = com.google.gson.Gson()
            val entries = gson.fromJson(memoryFile.readText(), Array<LongTermMemoryEntry>::class.java)
            longTermMemory.addAll(entries.toList())
        }
    }
    
    private fun estimateTokens(text: String): Int {
        return text.length / 4
    }
    
    fun clearMemory() {
        memoryCache.clear()
        currentContextTokens = 0
    }
    
    fun getMemoryStats(): String {
        return """
            当前上下文：$currentContextTokens / $MAX_CONTEXT tokens
            缓存条目：${memoryCache.size}
            长期记忆：${longTermMemory.size}
        """.trimIndent()
    }
    
    fun isModelLoaded(): Boolean = isLoaded
    
    suspend fun selfImprove(feedback: String): Result<String> = withContext(Dispatchers.IO) {
        val prompt = "根据以下反馈改进：$feedback\n输出改进计划："
        return@withContext generate(prompt)
    }
    
    suspend fun selfLearn(newKnowledge: String): Result<String> = withContext(Dispatchers.IO) {
        val prompt = "学习新知识：$newKnowledge\n输出学习总结："
        return@withContext generate(prompt)
    }
    
    suspend fun selfArchitect(task: String): Result<String> = withContext(Dispatchers.IO) {
        val prompt = "设计系统架构：$task\n输出架构设计："
        return@withContext generate(prompt)
    }
}

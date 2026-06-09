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
        const val CACHE_SIZE = 16 * 1024 * 1024 // 16MB 上下文缓存
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
                return@withContext Result.failure(Exception("模型文件不存在：$modelPath"))
            }
            
            val params = ModelParameters().apply {
                modelPath = modelFile.absolutePath
                nContext = MAX_CONTEXT
                nBatch = 512
                nThreads = Runtime.getRuntime().availableProcessors()
                useMmap = true
                useMlock = false
            }
            
            model = LlamaModel(params)
            ctx = model?.createContext()
            isLoaded = true
            
            Result.success("模型加载成功：${modelFile.name}\n显存占用：${getMemoryUsage()}MB")
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
        sb.append("<|im_start|>system\n你是一个自进化 AI 助手，具有自学习、自改正、自构架能力。你的记忆缓存为 16MB，支持超长上下文。\n<|im_end|>\n")
        
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
    
    private fun getMemoryUsage(): Int {
        return memoryCache.sumOf { it.tokens } * 4 / 1024
    }
    
    suspend fun selfImprove(feedback: String): Result<String> = withContext(Dispatchers.IO) {
        val improvementPrompt = """
            根据以下反馈改进你的行为：
            $feedback
            
            请分析：
            1. 哪里做得不好
            2. 如何改进
            3. 记录到长期记忆
            
            输出改进计划：
        """.trimIndent()
        
        return@withContext generate(improvementPrompt)
    }
    
    suspend fun selfLearn(newKnowledge: String): Result<String> = withContext(Dispatchers.IO) {
        val learnPrompt = """
            学习新知识并整合到知识库：
            $newKnowledge
            
            请：
            1. 提取关键概念
            2. 关联已有知识
            3. 更新知识结构
            
            输出学习总结：
        """.trimIndent()
        
        return@withContext generate(learnPrompt)
    }
    
    suspend fun selfArchitect(task: String): Result<String> = withContext(Dispatchers.IO) {
        val architectPrompt = """
            为以下任务设计系统架构：
            $task
            
            请输出：
            1. 模块划分
            2. 接口设计
            3. 数据流
            4. 代码结构
            
            架构设计：
        """.trimIndent()
        
        return@withContext generate(architectPrompt)
    }
    
    suspend fun selfCorrect(code: String, error: String): Result<String> = withContext(Dispatchers.IO) {
        val correctPrompt = """
            代码出现错误，请自动修正：
            
            原始代码：
            $code
            
            错误信息：
            $error
            
            请：
            1. 分析错误原因
            2. 给出修正代码
            3. 说明修改点
            
            修正结果：
        """.trimIndent()
        
        return@withContext generate(correctPrompt)
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
            显存占用：${getMemoryUsage()}MB
        """.trimIndent()
    }
    
    fun isModelLoaded(): Boolean = isLoaded
}

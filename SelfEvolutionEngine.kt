package org.omnibot.selfevolvingai.core.evolution

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 自进化引擎 - 核心自学习、自改进系统
 * 
 * 功能:
 * 1. 定期自我评估
 * 2. 生成改进建议
 * 3. 执行代码进化
 * 4. 验证更改效果
 */
@Singleton
class SelfEvolutionEngine @Inject constructor(
    private val selfEvaluationUseCase: SelfEvaluationUseCase,
    private val codeEvolutionService: CodeEvolutionService,
    private val learningScheduler: LearningScheduler
) {
    
    // 进化状态
    private var isRunning = false
    private var evolutionCycle = 0
    
    /**
     * 启动自进化循环
     * @param intervalMinutes 评估间隔（分钟）
     */
    fun startEvolutionCycle(intervalMinutes: Long = 60) {
        if (isRunning) return
        
        isRunning = true
        
        // 启动后台循环
        kotlinx.coroutines.GlobalScope.launch {
            while (isRunning) {
                runEvolutionStep()
                delay(intervalMinutes * 60 * 1000)
            }
        }
    }
    
    /**
     * 停止自进化循环
     */
    fun stop() {
        isRunning = false
    }
    
    /**
     * 执行单次进化步骤
     */
    suspend fun runEvolutionStep(): EvolutionResult {
        evolutionCycle++
        
        return try {
            // 步骤 1: 自我评估
            val evaluation = selfEvaluationUseCase.evaluate()
            
            // 步骤 2: 生成改进建议
            val suggestions = evaluation.generateSuggestions()
            
            // 步骤 3: 执行代码进化（如果有建议）
            val evolutionResults = if (suggestions.isNotEmpty()) {
                suggestions.map { suggestion ->
                    codeEvolutionService.evolve(suggestion)
                }
            } else {
                emptyList()
            }
            
            // 步骤 4: 触发新的学习周期
            learningScheduler.scheduleNextLearning(evaluation.weakTopics)
            
            EvolutionResult(
                cycle = evolutionCycle,
                evaluation = evaluation,
                suggestions = suggestions,
                evolutions = evolutionResults
            )
        } catch (e: Exception) {
            EvolutionResult(
                cycle = evolutionCycle,
                error = e
            )
        }
    }
    
    /**
     * 获取进化状态流
     */
    fun getEvolutionStatus(): Flow<EvolutionStatus> = flow {
        while (isRunning) {
            emit(
                EvolutionStatus(
                    isRunning = isRunning,
                    currentCycle = evolutionCycle,
                    lastUpdate = System.currentTimeMillis()
                )
            )
            delay(5000)
        }
    }
}

/**
 * 进化状态数据类
 */
data class EvolutionStatus(
    val isRunning: Boolean = false,
    val currentCycle: Int = 0,
    val lastUpdate: Long = System.currentTimeMillis()
) {
    val status: String
        get() = if (isRunning) "运行中" else "已停止"
}

/**
 * 进化结果数据类
 */
data class EvolutionResult(
    val cycle: Int,
    val evaluation: SelfEvaluation? = null,
    val suggestions: List<EvolutionSuggestion> = emptyList(),
    val evolutions: List<CodeEvolution> = emptyList(),
    val error: Throwable? = null
) {
    val isSuccess: Boolean
        get() = error == null
    
    val summary: String
        get() = if (isSuccess) {
            "周期 $cycle: ${suggestions.size} 条建议，${evolutions.size} 次进化"
        } else {
            "周期 $cycle: 失败 - ${error?.message}"
        }
}

/**
 * 自我评估结果
 */
data class SelfEvaluation(
    val score: Int,
    val metrics: PerformanceMetrics,
    val issues: List<EvaluationIssue>,
    val weakTopics: List<String>
) {
    fun generateSuggestions(): List<EvolutionSuggestion> {
        return issues.map { issue ->
            EvolutionSuggestion(
                id = java.util.UUID.randomUUID().toString(),
                title = issue.title,
                description = issue.description,
                priority = issue.severity,
                suggestedAction = issue.suggestedFix
            )
        }
    }
}

/**
 * 性能指标
 */
data class PerformanceMetrics(
    val learningSpeed: Double,      // 学习速度（页面/分钟）
    val memoryUsage: Double,        // 内存使用率（%）
    val responseTime: Double,       // 响应时间（ms）
    val errorRate: Double,          // 错误率（%）
    val knowledgeGrowth: Int        // 知识增长（条/天）
)

/**
 * 评估问题
 */
data class EvaluationIssue(
    val title: String,
    val description: String,
    val severity: Priority,
    val suggestedFix: String
)

enum class Priority {
    LOW, MEDIUM, HIGH, CRITICAL
}

/**
 * 进化建议
 */
data class EvolutionSuggestion(
    val id: String,
    val title: String,
    val description: String,
    val priority: Priority,
    val suggestedAction: String
)

/**
 * 代码进化结果
 */
data class CodeEvolution(
    val suggestionId: String,
    val oldCode: String,
    val newCode: String,
    val testPassed: Boolean,
    val merged: Boolean
)

package org.omnibot.selfevolvingai.hardware

import android.os.Build

object HardwareDetector {
    
    data class DeviceInfo(
        val brand: String,
        val model: String,
        val soc: String,
        val socCores: Int,
        val ramGB: Float,
        val androidVersion: String,
        val apiLevel: Int,
        val isHighEnd: Boolean,
        val recommendedModel: String,
        val optimizationPack: String
    )
    
    fun detect(): DeviceInfo {
        val brand = Build.BRAND ?: "Unknown"
        val model = Build.MODEL ?: "Unknown"
        val soc = detectSOC()
        val cores = Runtime.getRuntime().availableProcessors()
        val ramGB = getRamGB()
        val androidVersion = Build.VERSION.RELEASE ?: "Unknown"
        val apiLevel = Build.VERSION.SDK_INT
        
        val isHighEnd = ramGB >= 12f && cores >= 8
        val recommendedModel = recommendModel(ramGB, soc)
        val optimizationPack = getOptimizationPack(soc, ramGB)
        
        return DeviceInfo(
            brand = brand,
            model = model,
            soc = soc,
            socCores = cores,
            ramGB = ramGB,
            androidVersion = androidVersion,
            apiLevel = apiLevel,
            isHighEnd = isHighEnd,
            recommendedModel = recommendedModel,
            optimizationPack = optimizationPack
        )
    }
    
    private fun detectSOC(): String {
        val cpuInfo = System.getProperty("os.arch") ?: ""
        val hardware = Build.HARDWARE ?: ""
        
        return when {
            cpuInfo.contains("arm64", ignoreCase = true) && hardware.contains("mt", ignoreCase = true) -> 
                "MediaTek Dimensity"
            cpuInfo.contains("arm64", ignoreCase = true) && hardware.contains("qcom", ignoreCase = true) -> 
                "Qualcomm Snapdragon"
            cpuInfo.contains("arm64", ignoreCase = true) -> 
                "ARM64"
            else -> "Unknown"
        }
    }
    
    private fun getRamGB(): Float {
        return try {
            val memInfo = Class.forName("android.app.ActivityManager")
                .getMethod("getMemoryInfo", android.app.ActivityManager.MemoryInfo::class.java)
            val activityManager = memInfo.invoke(null) as? android.app.ActivityManager.MemoryInfo
            activityManager?.availMem?.let { 
                val totalMem = java.lang.reflect.Field::class.java
                return@let 0f
            } ?: 0f
        } catch (e: Exception) {
            8f
        }
    }
    
    private fun recommendModel(ramGB: Float, soc: String): String {
        return when {
            ramGB >= 16f -> "Qwen2.5-14B-GGUF (Q4_K_M)"
            ramGB >= 12f -> "Qwen2.5-7B-GGUF (Q5_K_M)"
            ramGB >= 8f -> "Qwen2.5-3B-GGUF (Q6_K)"
            else -> "TinyLlama-1.1B-GGUF (Q8_0)"
        }
    }
    
    private fun getOptimizationPack(soc: String, ramGB: Float): String {
        return when {
            soc.contains("Dimensity", ignoreCase = true) && ramGB >= 12f -> 
                "天旗舰优化包 (APU 6.0 加速)"
            soc.contains("Snapdragon", ignoreCase = true) && ramGB >= 12f -> 
                "骁龙精英优化包 (Hexagon DSP 加速)"
            ramGB >= 8f -> 
                "标准性能优化包"
            else -> 
                "轻量级优化包"
        }
    }
}

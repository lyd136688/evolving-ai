package org.omnibot.selfevolvingai.service

import android.app.*
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import org.omnibot.selfevolvingai.R
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL

class DownloadService : Service() {
    
    companion object {
        const val CHANNEL_ID = "download_channel"
        const val NOTIFICATION_ID = 1001
    }
    
    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }
    
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val url = intent?.getStringExtra("url")
        val savePath = intent?.getStringExtra("savePath")
        
        if (url != null && savePath != null) {
            startForeground(NOTIFICATION_ID, createNotification("正在下载...", 0))
            downloadFile(url, savePath)
        }
        
        return START_NOT_STICKY
    }
    
    private fun downloadFile(url: String, savePath: String) {
        Thread {
            try {
                val connection = URL(url).openConnection() as HttpURLConnection
                connection.connect()
                val totalSize = connection.contentLength
                val inputStream = connection.inputStream
                val outputStream = FileOutputStream(File(savePath))
                
                val buffer = ByteArray(4096)
                var downloaded = 0
                var bytesRead: Int
                
                while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                    outputStream.write(buffer, 0, bytesRead)
                    downloaded += bytesRead
                    val progress = (downloaded * 100 / totalSize).toInt()
                    updateNotification(progress)
                }
                
                outputStream.close()
                inputStream.close()
                
                updateNotification(100, "下载完成")
                stopForeground(STOP_FOREGROUND_REMOVE)
                stopSelf()
            } catch (e: Exception) {
                updateNotification(0, "下载失败：${e.message}")
                stopForeground(STOP_FOREGROUND_REMOVE)
                stopSelf()
            }
        }.start()
    }
    
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "下载服务",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }
    
    private fun createNotification(text: String, progress: Int): Notification {
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("模型下载")
            .setContentText(text)
            .setSmallIcon(android.R.drawable.stat_sys_download)
            .setProgress(100, progress, false)
            .setOngoing(true)
        
        return builder.build()
    }
    
    private fun updateNotification(progress: Int, text: String? = null) {
        val notification = createNotification(text ?: "下载中... $progress%", progress)
        val manager = getSystemService(NotificationManager::class.java)
        manager.notify(NOTIFICATION_ID, notification)
    }
    
    override fun onBind(intent: Intent?): IBinder? = null
}

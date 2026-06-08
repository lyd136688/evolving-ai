package org.omnibot.selfevolvingai

import android.app.Activity
import android.os.Bundle
import android.widget.TextView

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val textView = TextView(this)
        textView.text = "🧠 自进化 AI 系统\n\n版本：1.0.0"
        textView.textSize = 20f
        textView.setPadding(50, 100, 50, 100)
        setContentView(textView)
    }
}

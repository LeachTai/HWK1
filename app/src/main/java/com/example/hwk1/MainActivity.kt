package com.example.hwk1

import android.os.Bundle
import android.os.CountDownTimer
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    private var score = 0
    private lateinit var timer: CountDownTimer
    private var isGameRunning = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val scoreText = findViewById<TextView>(R.id.scoreText)
        val timerText = findViewById<TextView>(R.id.timerText)
        val clickButton = findViewById<Button>(R.id.clickButton)
        val startButton = findViewById<Button>(R.id.startButton)
        val endButton = findViewById<Button>(R.id.endButton)

        // 取得最高分
        val prefs = getSharedPreferences("ClickGamePrefs", MODE_PRIVATE)
        val highScore = prefs.getInt("highScore", 0)
        scoreText.text = "得分: 0 (最高分: $highScore)"

        // 開始按鈕點擊事件
        startButton.setOnClickListener {
            if (!isGameRunning) {
                startGame(scoreText, timerText, clickButton, endButton, highScore)
                startButton.isEnabled = false // 開始後禁用開始按鈕
            }
        }

        // 點擊加分按鈕
        clickButton.setOnClickListener {
            score++
            scoreText.text = "得分: $score (最高分: $highScore)"
        }

        // 結束按鈕點擊事件
        endButton.setOnClickListener {
            if (isGameRunning) {
                endGame(scoreText, clickButton, endButton, highScore)
            }
        }
    }

    private fun startGame(
        scoreText: TextView,
        timerText: TextView,
        clickButton: Button,
        endButton: Button,
        highScore: Int
    ) {
        score = 0 // 重置得分
        scoreText.text = "得分: 0 (最高分: $highScore)"
        clickButton.isEnabled = true
        endButton.isEnabled = true
        isGameRunning = true

        // 計時器：10秒
        timer = object : CountDownTimer(10000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsLeft = millisUntilFinished / 1000
                timerText.text = "剩餘時間: $secondsLeft 秒"
            }

            override fun onFinish() {
                endGame(scoreText, clickButton, endButton, highScore)
            }
        }.start()
    }

    private fun endGame(
        scoreText: TextView,
        clickButton: Button,
        endButton: Button,
        highScore: Int
    ) {
        timer.cancel() // 停止計時器
        clickButton.isEnabled = false
        endButton.isEnabled = false
        isGameRunning = false

        // 更新最高分
        val prefs = getSharedPreferences("ClickGamePrefs", MODE_PRIVATE)
        if (score > highScore) {
            prefs.edit().putInt("highScore", score).apply()
            scoreText.text = "遊戲結束！新最高分: $score"
        } else {
            scoreText.text = "遊戲結束！得分: $score (最高分: $highScore)"
        }

        // 遊戲結束後重新啟用開始按鈕
        findViewById<Button>(R.id.startButton).isEnabled = true
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isGameRunning) {
            timer.cancel() // 避免記憶體洩漏
        }
    }
}
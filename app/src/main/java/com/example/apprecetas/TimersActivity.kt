package com.example.apprecetas

import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class TimersActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timers)

        val container = findViewById<LinearLayout>(R.id.linearLayoutContainer)
        val btnAgregar = findViewById<Button>(R.id.btnAgregarTimer)
        val btnBack = findViewById<Button>(R.id.btnBack)

        btnBack.setOnClickListener {
            finish()
        }

        btnAgregar.setOnClickListener {
            val timerView = layoutInflater.inflate(R.layout.item_timer, container, false)

            val etTitle = timerView.findViewById<EditText>(R.id.etTimerTitle)
            val etMinutes = timerView.findViewById<EditText>(R.id.etInputMinutes)
            val tvClock = timerView.findViewById<TextView>(R.id.tvTimerClock)
            val btnPause = timerView.findViewById<Button>(R.id.btnPause)
            val btnCancel = timerView.findViewById<Button>(R.id.btnCancel)

            val numeroTimer = container.childCount + 1
            etTitle.setText("Timer para Receta $numeroTimer")

            var timeLeftInMillis: Long = 300000
            var countDownTimer: CountDownTimer? = null
            var isTimerRunning = false
            var isStarted = false

            fun updateCountDownText() {
                val minutes = (timeLeftInMillis / 1000) / 60
                val seconds = (timeLeftInMillis / 1000) % 60
                tvClock.text = String.format("%02d:%02d", minutes, seconds)
            }

            fun startTimer() {
                countDownTimer = object : CountDownTimer(timeLeftInMillis, 1000) {
                    override fun onTick(millisUntilFinished: Long) {
                        timeLeftInMillis = millisUntilFinished
                        updateCountDownText()
                    }

                    override fun onFinish() {
                        isTimerRunning = false
                        isStarted = false
                        btnPause.text = "Iniciar"
                        tvClock.text = "00:00"
                        etMinutes.isEnabled = true
                    }
                }.start()

                isTimerRunning = true
                isStarted = true
                btnPause.text = "Pausar"
                etMinutes.isEnabled = false
            }

            fun pauseTimer() {
                countDownTimer?.cancel()
                isTimerRunning = false
                btnPause.text = "Reanudar"
            }

            btnPause.setOnClickListener {
                if (!isStarted) {
                    val inputMinutesStr = etMinutes.text.toString()
                    val inputMinutes = inputMinutesStr.toIntOrNull()

                    if (inputMinutes == null || inputMinutes <= 0) {
                        Toast.makeText(this, "Ingresá un número de minutos válido", Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }

                    timeLeftInMillis = inputMinutes * 60 * 1000L
                    updateCountDownText()
                    startTimer()
                } else if (isTimerRunning) {
                    pauseTimer()
                } else {
                    startTimer()
                }
            }

            btnCancel.setOnClickListener {
                countDownTimer?.cancel()
                container.removeView(timerView)
            }

            container.addView(timerView)
        }
    }
}
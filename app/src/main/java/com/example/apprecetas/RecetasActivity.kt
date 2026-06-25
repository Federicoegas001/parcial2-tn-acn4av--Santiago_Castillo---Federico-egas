package com.example.apprecetas

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class RecetasActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recetas)
        findViewById<Button>(R.id.btnBack).setOnClickListener { finish() }
    }
}

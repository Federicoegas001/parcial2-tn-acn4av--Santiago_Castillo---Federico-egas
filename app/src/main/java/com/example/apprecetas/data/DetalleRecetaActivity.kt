package com.example.apprecetas

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class DetalleRecetaActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalle_receta)

        val tvTitle = findViewById<TextView>(R.id.tvDetalleRecipeName)
        val tvEmoji = findViewById<TextView>(R.id.tvDetalleEmoji)
        val tvInstructions = findViewById<TextView>(R.id.tvDetalleInstructions)
        val btnBack = findViewById<Button>(R.id.btnDetalleBack)
        val btnTimers = findViewById<Button>(R.id.btnGoToTimers)


        val recetaNombre = intent.getStringExtra("RECIPE_NAME") ?: "Receta"
        val recetaEmoji = intent.getStringExtra("RECIPE_EMOJI") ?: "🍽️"
        val recetaInstructions = intent.getStringExtra("RECIPE_INSTRUCTIONS") ?: ""

        tvTitle.text = recetaNombre
        tvEmoji.text = recetaEmoji
        tvInstructions.text = recetaInstructions


        btnBack.setOnClickListener { finish() }


        btnTimers.setOnClickListener {
            val intent = Intent(this, TimersActivity::class.java)

            startActivity(intent)
        }
    }
}
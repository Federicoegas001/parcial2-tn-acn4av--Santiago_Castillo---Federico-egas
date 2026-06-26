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

        tvTitle.text = recetaNombre
        tvEmoji.text = recetaEmoji


        tvInstructions.text = when (recetaNombre) {
            "Tortilla de tomate y queso" -> {
                "1. Batir los huevos en un bol con una pizca de sal.\n\n" +
                        "2. Cortar el tomate en rodajas finas o cubos pequeños.\n\n" +
                        "3. Calentar una sartén con un chorrito de aceite de oliva y dorar el tomate por 2 minutos.\n\n" +
                        "4. Verter los huevos batidos y sumar el queso en hebras por encima.\n\n" +
                        "5. Cocinar a fuego lento durante 5 minutos, dar vuelta con cuidado y dejar gratinar el queso."
            }
            "Huevos revueltos gratinados" -> {
                "1. Romper los huevos directamente sobre una sartén fría con un cubo de manteca o aceite.\n\n" +
                        "2. Llevar a fuego medio y revolver constantemente con espátula.\n\n" +
                        "3. Retirar del fuego intermitentemente para que queden bien cremosos.\n\n" +
                        "4. En el último minuto, espolvorear el queso y tapar la sartén para que se derrita por completo."
            }
            else -> "Pasos preliminares:\nPrecalentar utensilios, reunir ingredientes seleccionados y cocinar al gusto controlando los minutos."
        }


        btnBack.setOnClickListener { finish() }


        btnTimers.setOnClickListener {
            val intent = Intent(this, TimersActivity::class.java)

            startActivity(intent)
        }
    }
}
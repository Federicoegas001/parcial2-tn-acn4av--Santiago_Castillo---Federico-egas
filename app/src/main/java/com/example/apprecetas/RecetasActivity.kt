package com.example.apprecetas

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.apprecetas.data.RecetasRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class RecetasActivity : AppCompatActivity() {


    private val recetasRepository = RecetasRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recetas)

        val btnBack = findViewById<Button>(R.id.btnBack)
        val container = findViewById<LinearLayout>(R.id.linearLayoutRecetasContainer)

        btnBack.setOnClickListener {
            finish()
        }


        // Escucha segura conectada al ciclo de vida de la pantalla
        lifecycleScope.launch {

            repeatOnLifecycle(Lifecycle.State.STARTED) {
                try {
                    // 1. Forzamos inicio de sesión anónimo si hace falta
                    if (FirebaseAuth.getInstance().currentUser == null) {
                        FirebaseAuth.getInstance().signInAnonymously()
                    }

                    // 2. Cargamos el catálogo inicial en la nube si está vacío
                    recetasRepository.seedCatalogIfEmpty()

                    // 3. Escuchamos las recetas reales en tiempo real
                    recetasRepository.observeRecipes().collect { recetas ->
                        container.removeAllViews()

                        val inflater = LayoutInflater.from(this@RecetasActivity)

                        for (receta in recetas) {
                            val recipeView =
                                inflater.inflate(R.layout.item_receta, container, false)

                            val tvEmoji = recipeView.findViewById<TextView>(R.id.tvRecipeEmoji)
                            val tvTitle = recipeView.findViewById<TextView>(R.id.tvRecipeTitle)
                            val tvMinutes = recipeView.findViewById<TextView>(R.id.tvRecipeMinutes)
                            val tvDifficulty =
                                recipeView.findViewById<TextView>(R.id.tvRecipeDifficulty)
                            val tvServings =
                                recipeView.findViewById<TextView>(R.id.tvRecipeServings)
                            val tvIngredients =
                                recipeView.findViewById<TextView>(R.id.tvRecipeIngredients)

                            tvEmoji.text = receta.emoji
                            tvTitle.text = receta.title
                            tvMinutes.text = "🕒 ${receta.minutes} min"
                            tvDifficulty.text = "🔥 ${receta.difficulty}"
                            tvServings.text = "👥 ${receta.servings} porc."

                            val ingredientesTexto =
                                receta.ingredientNames.joinToString(", ") { it.lowercase() }
                            tvIngredients.text = "Usa: $ingredientesTexto"

                            // Evento de clic para ir al detalle
                            recipeView.setOnClickListener {
                                val intent = Intent(
                                    this@RecetasActivity,
                                    DetalleRecetaActivity::class.java
                                ).apply {
                                    putExtra("RECIPE_NAME", receta.title)
                                    putExtra("RECIPE_EMOJI", receta.emoji)
                                }
                                startActivity(intent)
                            }

                            container.addView(recipeView)
                        }
                    }
                } catch (e: Exception) {
                    // Evitamos mostrar el Toast si el error es solo porque la corrutina se canceló al cambiar de pantalla
                    if (e !is kotlinx.coroutines.CancellationException) {
                        e.printStackTrace()
                        Toast.makeText(
                            this@RecetasActivity,
                            "Error al conectar con Firestore",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }
    }
}
package com.example.apprecetas

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.apprecetas.ui.home.HomeViewModel
import com.example.apprecetas.ui.home.IngredientRowAdapter
import com.example.apprecetas.ui.home.RecipeCardAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: HomeViewModel
    private lateinit var ingredientAdapter: IngredientRowAdapter
    private lateinit var recipeAdapter: RecipeCardAdapter
    private lateinit var rvRecipes: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (FirebaseAuth.getInstance().currentUser == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }
        setContentView(R.layout.activity_main)

        viewModel = ViewModelProvider(this)[HomeViewModel::class.java]

        setupAdapters()
        setupButtons()
        setupSearch()
        setupBottomNav()
        observeState()
    }

    private fun setupAdapters() {
        ingredientAdapter = IngredientRowAdapter { ingredient ->
            viewModel.onIngredientToggle(ingredient.name)
        }
        val rvIngredients = findViewById<RecyclerView>(R.id.rvIngredients)
        rvIngredients.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        rvIngredients.adapter = ingredientAdapter

        recipeAdapter = RecipeCardAdapter { recipe ->
            val intent = Intent(this, DetalleRecetaActivity::class.java).apply {
                putExtra("RECIPE_NAME", recipe.title)
                putExtra("RECIPE_EMOJI", recipe.emoji)
                putExtra("RECIPE_INSTRUCTIONS", recipe.instructions)
            }
            startActivity(intent)
        }
        rvRecipes = findViewById(R.id.rvRecipes)
        rvRecipes.adapter = recipeAdapter
    }

    private fun setupButtons() {
        findViewById<Button>(R.id.btnViewRecipes).setOnClickListener {
            val suggestions = viewModel.uiState.value.suggestedRecipes
            if (suggestions.isEmpty()) {
                Toast.makeText(this, "Seleccioná ingredientes para ver recetas sugeridas", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            rvRecipes.visibility = if (rvRecipes.visibility == View.VISIBLE) View.GONE else View.VISIBLE
        }
        findViewById<Button>(R.id.btnLogout).setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun setupSearch() {
        findViewById<EditText>(R.id.etSearch).addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                viewModel.onSearchQueryChange(s?.toString() ?: "")
            }
        })
    }

    private fun setupBottomNav() {
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)
        bottomNav.selectedItemId = R.id.nav_home
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> true
                R.id.nav_ingredientes -> {
                    startActivity(Intent(this, IngredientsActivity::class.java))
                    false
                }
                R.id.nav_recetas -> {
                    startActivity(Intent(this, RecetasActivity::class.java))
                    false
                }
                R.id.nav_timers -> {
                    startActivity(Intent(this, TimersActivity::class.java))
                    false
                }
                else -> false
            }
        }
    }

    private fun observeState() {
        lifecycleScope.launch {
            viewModel.uiState.collectLatest { state ->
                findViewById<TextView>(R.id.tvGreeting).text = getString(R.string.home_greeting, state.userName)
                ingredientAdapter.update(state.filteredIngredients, state.selectedIngredientNames)
                val suggestions = state.suggestedRecipes
                recipeAdapter.update(suggestions.map { it.recipe }, state.selectedIngredientNames)
                if (suggestions.isEmpty()) rvRecipes.visibility = View.GONE
            }
        }
    }
}

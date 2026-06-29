package com.example.apprecetas.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.apprecetas.R
import com.example.apprecetas.model.Recipe

class RecipeCardAdapter(
    private val onRecipeClick: (Recipe) -> Unit = {}
) : RecyclerView.Adapter<RecipeCardAdapter.ViewHolder>() {

    private var recipes: List<Recipe> = emptyList()
    private var selectedNames: Set<String> = emptySet()

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val container: View = view.findViewById(R.id.cardContainer)
        val tvEmoji: TextView = view.findViewById(R.id.tvRecipeEmoji)
        val tvTitle: TextView = view.findViewById(R.id.tvRecipeTitle)
        val tvSubtitle: TextView = view.findViewById(R.id.tvRecipeSubtitle)
        val tvBadge: TextView = view.findViewById(R.id.tvMatchBadge)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_recipe_card, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val recipe = recipes[position]
        val matched = recipe.ingredientNames.count { it in selectedNames }
        val isFullMatch = matched == recipe.ingredientNames.size && recipe.ingredientNames.isNotEmpty()

        holder.tvEmoji.text = recipe.emoji.ifEmpty { "🍽️" }
        holder.tvTitle.text = recipe.title
        holder.tvSubtitle.text = holder.itemView.context.getString(
            R.string.home_recipe_time, recipe.minutes
        )

        holder.container.setBackgroundResource(
            if (isFullMatch) R.drawable.bg_recipe_card_matched else R.drawable.bg_recipe_card
        )

        if (matched > 0) {
            holder.tvBadge.visibility = View.VISIBLE
            holder.tvBadge.text = "$matched/${recipe.ingredientNames.size}"
        } else {
            holder.tvBadge.visibility = View.GONE
        }

        holder.container.setOnClickListener { onRecipeClick(recipe) }
    }

    override fun getItemCount() = recipes.size

    fun update(newRecipes: List<Recipe>, newSelected: Set<String>) {
        recipes = newRecipes
        selectedNames = newSelected
        notifyDataSetChanged()
    }
}

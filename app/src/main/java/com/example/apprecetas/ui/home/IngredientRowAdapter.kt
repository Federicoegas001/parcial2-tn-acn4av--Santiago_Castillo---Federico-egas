package com.example.apprecetas.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.apprecetas.R
import com.example.apprecetas.model.Ingredient

class IngredientRowAdapter(
    private val onToggle: (Ingredient) -> Unit
) : RecyclerView.Adapter<IngredientRowAdapter.ViewHolder>() {

    private var ingredients: List<Ingredient> = emptyList()
    private var selectedNames: Set<String> = emptySet()

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val container: View = view.findViewById(R.id.rowContainer)
        val tvEmoji: TextView = view.findViewById(R.id.tvEmoji)
        val tvName: TextView = view.findViewById(R.id.tvName)
        val tvCheckbox: TextView = view.findViewById(R.id.tvCheckbox)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_ingredient_row, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val ingredient = ingredients[position]
        val isSelected = ingredient.name in selectedNames

        holder.tvEmoji.text = ingredient.emoji.ifEmpty { "🥦" }
        holder.tvName.text = ingredient.name

        holder.container.setBackgroundResource(
            if (isSelected) R.drawable.bg_ingredient_row_selected else R.drawable.bg_ingredient_row
        )
        holder.tvCheckbox.setBackgroundResource(
            if (isSelected) R.drawable.bg_checkbox_selected else R.drawable.bg_checkbox_unselected
        )

        holder.container.setOnClickListener { onToggle(ingredient) }
    }

    override fun getItemCount() = ingredients.size

    fun update(newIngredients: List<Ingredient>, newSelected: Set<String>) {
        ingredients = newIngredients
        selectedNames = newSelected
        notifyDataSetChanged()
    }
}

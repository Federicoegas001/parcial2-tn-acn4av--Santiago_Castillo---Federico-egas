package com.example.apprecetas.ui.ingredients

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.apprecetas.R
import com.example.apprecetas.model.PantryItem

sealed class IngredientListItem {
    data class Header(val category: String) : IngredientListItem()
    data class Item(val item: PantryItem) : IngredientListItem()
}

class IngredientsAdapter(
    private val onDelete: (PantryItem) -> Unit,
    private val onEdit: (PantryItem) -> Unit,
    private val onUpdateQuantity: (PantryItem) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val items = mutableListOf<IngredientListItem>()

    companion object {
        private const val VIEW_TYPE_HEADER = 0
        private const val VIEW_TYPE_ITEM = 1
    }

    inner class HeaderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvCategory: TextView = view.findViewById(R.id.tvCategoryHeader)
    }

    inner class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvEmoji: TextView = view.findViewById(R.id.tvIngredientEmoji)
        val tvName: TextView = view.findViewById(R.id.tvIngredientName)
        val tvCategory: TextView = view.findViewById(R.id.tvIngredientCategory)
        val tvQuantity: TextView = view.findViewById(R.id.tvQuantity)
        val tvMenu: TextView = view.findViewById(R.id.tvMenu)
    }

    override fun getItemViewType(position: Int) = when (items[position]) {
        is IngredientListItem.Header -> VIEW_TYPE_HEADER
        is IngredientListItem.Item -> VIEW_TYPE_ITEM
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == VIEW_TYPE_HEADER) {
            HeaderViewHolder(inflater.inflate(R.layout.item_category_header, parent, false))
        } else {
            ItemViewHolder(inflater.inflate(R.layout.item_ingredient, parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val listItem = items[position]) {
            is IngredientListItem.Header -> {
                (holder as HeaderViewHolder).tvCategory.text = listItem.category.uppercase()
            }
            is IngredientListItem.Item -> {
                val vh = holder as ItemViewHolder
                val pantryItem = listItem.item
                vh.tvEmoji.text = pantryItem.emoji.ifEmpty { "🥦" }
                vh.tvName.text = pantryItem.name
                vh.tvCategory.text = pantryItem.category.ifEmpty { "Otros" }

                val qty = pantryItem.quantity.trim()
                val isLowStock = qty.isEmpty() || qty.lowercase() == "poco"
                if (isLowStock) {
                    vh.tvQuantity.text = "poco"
                    vh.tvQuantity.setBackgroundResource(R.drawable.bg_badge_yellow)
                    vh.tvQuantity.setTextColor(0xFFF59E0B.toInt())
                } else {
                    vh.tvQuantity.text = qty
                    vh.tvQuantity.setBackgroundResource(R.drawable.bg_badge_purple)
                    vh.tvQuantity.setTextColor(0xFFC4B5FD.toInt())
                }

                vh.tvMenu.setOnClickListener { anchor ->
                    val popup = PopupMenu(anchor.context, anchor)
                    popup.menu.add(0, 1, 0, anchor.context.getString(R.string.menu_edit))
                    popup.menu.add(0, 2, 1, anchor.context.getString(R.string.menu_update_qty))
                    popup.menu.add(0, 3, 2, anchor.context.getString(R.string.menu_delete))
                    popup.setOnMenuItemClickListener { menuItem ->
                        when (menuItem.itemId) {
                            1 -> { onEdit(pantryItem); true }
                            2 -> { onUpdateQuantity(pantryItem); true }
                            3 -> { onDelete(pantryItem); true }
                            else -> false
                        }
                    }
                    popup.show()
                }
            }
        }
    }

    override fun getItemCount() = items.size

    fun updateItems(newItems: List<IngredientListItem>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }
}

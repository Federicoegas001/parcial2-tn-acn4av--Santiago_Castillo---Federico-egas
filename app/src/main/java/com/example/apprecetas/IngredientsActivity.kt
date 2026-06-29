package com.example.apprecetas

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.apprecetas.model.PantryItem
import com.example.apprecetas.ui.ingredients.IngredientListItem
import com.example.apprecetas.ui.ingredients.IngredientsAdapter
import com.example.apprecetas.ui.ingredients.IngredientsViewModel
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class IngredientsActivity : AppCompatActivity() {

    private lateinit var viewModel: IngredientsViewModel
    private lateinit var adapter: IngredientsAdapter
    private var currentFilter = "Todos"
    private var currentSearch = ""
    private var allItems: List<PantryItem> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ingredients)

        viewModel = ViewModelProvider(this)[IngredientsViewModel::class.java]

        setupRecyclerView()
        setupChips()
        setupSearch()
        setupButtons()
        observePantry()
    }

    private fun setupRecyclerView() {
        adapter = IngredientsAdapter(
            onDelete = { item -> confirmDelete(item) },
            onEdit = { item -> showEditDialog(item) },
            onUpdateQuantity = { item -> showUpdateQuantityDialog(item) }
        )
        val rv = findViewById<RecyclerView>(R.id.rvIngredients)
        rv.layoutManager = LinearLayoutManager(this)
        rv.adapter = adapter
    }

    private fun setupChips() {
        val chipGroup = findViewById<ChipGroup>(R.id.chipGroup)
        chipGroup.setOnCheckedStateChangeListener { group, checkedIds ->
            if (checkedIds.isEmpty()) return@setOnCheckedStateChangeListener
            val chip = group.findViewById<Chip>(checkedIds[0])
            currentFilter = chip?.text?.toString() ?: getString(R.string.ing_filter_all)
            applyFilters()
        }
    }

    private fun setupSearch() {
        val etSearch = findViewById<EditText>(R.id.etSearch)
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                currentSearch = s?.toString() ?: ""
                applyFilters()
            }
        })
    }

    private fun setupButtons() {
        findViewById<Button>(R.id.btnBack).setOnClickListener { finish() }
    }

    private fun observePantry() {
        lifecycleScope.launch {
            viewModel.pantry.collectLatest { items ->
                allItems = items
                updateStats(items)
                applyFilters()
            }
        }
    }

    private fun applyFilters() {
        var filtered = allItems

        val filterAll = getString(R.string.ing_filter_all)
        if (currentFilter != filterAll) {
            filtered = filtered.filter { it.category == currentFilter }
        }

        if (currentSearch.isNotEmpty()) {
            filtered = filtered.filter { it.name.contains(currentSearch, ignoreCase = true) }
        }

        val listItems = mutableListOf<IngredientListItem>()
        val grouped = filtered
            .groupBy { it.category.ifEmpty { "Otros" } }
            .entries
            .sortedBy { it.key }

        for ((category, items) in grouped) {
            listItems.add(IngredientListItem.Header(category))
            items.sortedBy { it.name }.forEach { listItems.add(IngredientListItem.Item(it)) }
        }

        adapter.updateItems(listItems)
    }

    private fun updateStats(items: List<PantryItem>) {
        val total = items.size
        val lowStock = items.count {
            val qty = it.quantity.trim()
            qty.isEmpty() || qty.lowercase() == "poco"
        }
        val subtitle = getString(R.string.ing_subtitle, total)
        findViewById<TextView>(R.id.tvTotalCount).text = total.toString()
        findViewById<TextView>(R.id.tvLowStockCount).text = lowStock.toString()
        findViewById<TextView>(R.id.tvSubtitle).text = subtitle
    }

    private fun showEditDialog(item: PantryItem) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_ingredient, null)
        val etName = dialogView.findViewById<EditText>(R.id.etIngredientName)
        val etEmoji = dialogView.findViewById<EditText>(R.id.etIngredientEmoji)
        val etQty = dialogView.findViewById<EditText>(R.id.etIngredientQty)
        val spinner = dialogView.findViewById<Spinner>(R.id.spinnerCategory)

        val categories = categoryList()
        spinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, categories)

        etName.setText(item.name)
        etEmoji.setText(item.emoji)
        etQty.setText(item.quantity)
        val categoryIndex = categories.indexOf(item.category).takeIf { it >= 0 } ?: 0
        spinner.setSelection(categoryIndex)

        AlertDialog.Builder(this)
            .setTitle(getString(R.string.ing_dialog_edit_title))
            .setView(dialogView)
            .setPositiveButton(getString(R.string.ing_dialog_confirm)) { _, _ ->
                val name = etName.text.toString().trim()
                if (name.isNotEmpty()) {
                    viewModel.editItem(item.copy(
                        name = name,
                        emoji = etEmoji.text.toString().trim().ifEmpty { "🥦" },
                        category = spinner.selectedItem.toString(),
                        quantity = etQty.text.toString().trim()
                    ))
                } else {
                    Toast.makeText(this, getString(R.string.ing_error_empty_name), Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton(getString(R.string.ing_dialog_cancel), null)
            .show()
    }

    private fun showUpdateQuantityDialog(item: PantryItem) {
        val etQty = EditText(this).apply {
            setText(item.quantity)
            hint = getString(R.string.ing_dialog_qty_hint)
            setPadding(48, 24, 48, 24)
        }

        AlertDialog.Builder(this)
            .setTitle(getString(R.string.ing_dialog_qty_title))
            .setView(etQty)
            .setPositiveButton(getString(R.string.ing_dialog_confirm)) { _, _ ->
                viewModel.editItem(item.copy(quantity = etQty.text.toString().trim()))
            }
            .setNegativeButton(getString(R.string.ing_dialog_cancel), null)
            .show()
    }

    private fun confirmDelete(item: PantryItem) {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.menu_delete))
            .setMessage("¿Eliminar \"${item.name}\"?")
            .setPositiveButton(getString(R.string.menu_delete)) { _, _ ->
                viewModel.deleteItem(item.id)
            }
            .setNegativeButton(getString(R.string.ing_dialog_cancel), null)
            .show()
    }

    private fun categoryList() = listOf(
        getString(R.string.ing_filter_dairy),
        getString(R.string.ing_filter_veggies),
        getString(R.string.ing_filter_meats),
        getString(R.string.ing_filter_proteins),
        "Otros"
    )
}

package com.example.apprecetas.ui.ingredients

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.apprecetas.data.RecetasRepository
import com.example.apprecetas.model.PantryItem
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class IngredientsViewModel : ViewModel() {

    private val repository = RecetasRepository()

    val pantry: StateFlow<List<PantryItem>> = repository.observePantry()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addItem(item: PantryItem) {
        viewModelScope.launch {
            runCatching { repository.addPantryItem(item) }
        }
    }

    fun deleteItem(id: String) {
        viewModelScope.launch {
            runCatching { repository.deletePantryItem(id) }
        }
    }

    fun editItem(item: PantryItem) {
        viewModelScope.launch {
            runCatching { repository.updatePantryItem(item) }
        }
    }
}

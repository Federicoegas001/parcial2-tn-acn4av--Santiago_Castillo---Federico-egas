package com.example.apprecetas.ui.ingredients

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.apprecetas.data.AuthRepository
import com.example.apprecetas.data.RecetasRepository
import com.example.apprecetas.model.PantryItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class IngredientsViewModel : ViewModel() {

    private val repository = RecetasRepository()
    private val authRepository = AuthRepository()

    private val _userId = MutableStateFlow<String?>(null)

    val pantry: StateFlow<List<PantryItem>> = _userId
        .filterNotNull()
        .flatMapLatest { uid -> repository.observePantry(uid) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch {
            runCatching {
                _userId.value = authRepository.currentUserId()
            }
        }
    }

    fun addItem(item: PantryItem) {
        val uid = _userId.value ?: return
        viewModelScope.launch {
            runCatching { repository.addPantryItem(item.copy(userId = uid)) }
        }
    }

    fun deleteItem(id: String) {
        viewModelScope.launch {
            runCatching { repository.deletePantryItem(id) }
        }
    }

    fun editItem(item: PantryItem) {
        val uid = _userId.value ?: return
        viewModelScope.launch {
            runCatching { repository.updatePantryItem(item.copy(userId = uid)) }
        }
    }
}

package com.example.apprecetas.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.apprecetas.data.AuthRepository
import com.example.apprecetas.data.RecetasRepository
import com.example.apprecetas.model.Ingredient
import com.example.apprecetas.model.Recipe
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HomeUiState(
    val userName: String = "Federico",
    val searchQuery: String = "",
    val ingredients: List<Ingredient> = emptyList(),
    val selectedIngredientNames: Set<String> = emptySet(),
    val recipes: List<Recipe> = emptyList(),
    val isLoading: Boolean = true
) {
    val filteredIngredients: List<Ingredient>
        get() = if (searchQuery.isBlank()) {
            ingredients
        } else {
            ingredients.filter { it.name.contains(searchQuery, ignoreCase = true) }
        }

    val suggestedRecipes: List<RecipeSuggestion>
        get() {
            if (selectedIngredientNames.isEmpty()) return emptyList()
            return recipes
                .mapNotNull { recipe ->
                    if (!selectedIngredientNames.all { it in recipe.ingredientNames }) return@mapNotNull null
                    val matchedCount = recipe.ingredientNames.count { it in selectedIngredientNames }
                    RecipeSuggestion(
                        recipe = recipe,
                        matchedCount = matchedCount,
                        isFullMatch = matchedCount == recipe.ingredientNames.size
                    )
                }
                .sortedWith(
                    compareByDescending<RecipeSuggestion> { it.isFullMatch }
                        .thenByDescending { it.matchedCount }
                )
        }
}

data class RecipeSuggestion(
    val recipe: Recipe,
    val matchedCount: Int,
    val isFullMatch: Boolean
)

class HomeViewModel : ViewModel() {
    private val authRepository = AuthRepository()
    private val recetasRepository = RecetasRepository()

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private var userId: String? = null

    init {
        viewModelScope.launch {
            runCatching {
                val uid = authRepository.ensureSignedIn()
                userId = uid
                recetasRepository.seedCatalogIfEmpty()
                recetasRepository.seedUserPantryIfEmpty(uid)
                val selected = recetasRepository.getSelectedIngredientNames(uid)
                _uiState.update { it.copy(selectedIngredientNames = selected) }
            }.onFailure {
                _uiState.update { it.copy(isLoading = false) }
            }

            launch {
                runCatching {
                    recetasRepository.observeIngredients().collect { ingredients ->
                        val validNames = ingredients.map { it.name }.toSet()
                        _uiState.update { state ->
                            state.copy(
                                ingredients = ingredients,
                                isLoading = false,
                                selectedIngredientNames = state.selectedIngredientNames.filter { it in validNames }.toSet()
                            )
                        }
                    }
                }.onFailure {
                    _uiState.update { it.copy(isLoading = false) }
                }
            }
            launch {
                runCatching {
                    recetasRepository.observeRecipes().collect { recipes ->
                        _uiState.update { it.copy(recipes = recipes) }
                    }
                }
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun onIngredientToggle(name: String) {
        val updated = _uiState.value.selectedIngredientNames.let { current ->
            if (name in current) current - name else current + name
        }
        _uiState.update { it.copy(selectedIngredientNames = updated) }

        val uid = userId ?: return
        viewModelScope.launch {
            runCatching { recetasRepository.setSelectedIngredientNames(uid, updated) }
        }
    }
}

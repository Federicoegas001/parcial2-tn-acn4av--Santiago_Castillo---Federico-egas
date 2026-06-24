package com.example.apprecetas.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.apprecetas.model.Ingredient
import com.example.apprecetas.ui.theme.PurpleAccent
import com.example.apprecetas.ui.theme.PurpleAccentLight
import com.example.apprecetas.ui.theme.SurfaceDark
import com.example.apprecetas.ui.theme.SurfaceVariantDark
import com.example.apprecetas.ui.theme.TextPrimary
import com.example.apprecetas.ui.theme.TextSecondary

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 20.dp)
    ) {
        item {
            HomeHeader(userName = uiState.userName)
            Spacer(Modifier.height(20.dp))
            SearchField(
                query = uiState.searchQuery,
                onQueryChange = viewModel::onSearchQueryChange
            )
            Spacer(Modifier.height(24.dp))
            SectionLabel("MIS INGREDIENTES")
            Spacer(Modifier.height(12.dp))
        }

        items(uiState.filteredIngredients, key = { it.id }) { ingredient ->
            IngredientRow(
                ingredient = ingredient,
                isSelected = ingredient.name in uiState.selectedIngredientNames,
                onToggle = { viewModel.onIngredientToggle(ingredient.name) }
            )
            Spacer(Modifier.height(10.dp))
        }

        item {
            Spacer(Modifier.height(6.dp))
            ViewRecipesButton(selectedCount = uiState.selectedIngredientNames.size)
            Spacer(Modifier.height(24.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
            Spacer(Modifier.height(24.dp))
            SectionLabel("RECETAS SUGERIDAS")
            Spacer(Modifier.height(12.dp))
        }

        items(uiState.suggestedRecipes, key = { it.recipe.id }) { suggestion ->
            RecipeCard(suggestion = suggestion)
            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun HomeHeader(userName: String) {
    Row(verticalAlignment = Alignment.Top) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Hola, $userName 👋",
                color = TextSecondary,
                fontSize = 15.sp
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = "¿Qué cocino hoy?",
                color = TextPrimary,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = "Seleccioná los ingredientes que tenés",
                color = TextSecondary,
                fontSize = 14.sp
            )
        }
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(PurpleAccent),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = userName.take(1).uppercase(),
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        }
    }
}

@Composable
private fun SearchField(query: String, onQueryChange: (String) -> Unit) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = Modifier.fillMaxWidth(),
        placeholder = { Text("Buscar ingrediente...", color = TextSecondary) },
        leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null, tint = TextSecondary) },
        singleLine = true,
        shape = RoundedCornerShape(14.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = SurfaceDark,
            unfocusedContainerColor = SurfaceDark,
            focusedBorderColor = PurpleAccent,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
            focusedTextColor = TextPrimary,
            unfocusedTextColor = TextPrimary,
            cursorColor = PurpleAccent
        )
    )
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text,
        color = TextSecondary,
        fontSize = 12.sp,
        fontWeight = FontWeight.SemiBold,
        letterSpacing = 1.2.sp
    )
}

@Composable
private fun IngredientRow(
    ingredient: Ingredient,
    isSelected: Boolean,
    onToggle: () -> Unit
) {
    val backgroundColor = if (isSelected) SurfaceVariantDark else SurfaceDark
    val borderColor = if (isSelected) PurpleAccent else MaterialTheme.colorScheme.outline
    val textColor = if (isSelected) PurpleAccentLight else TextPrimary

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(backgroundColor)
            .border(1.dp, borderColor, RoundedCornerShape(14.dp))
            .clickable(onClick = onToggle)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(RoundedCornerShape(6.dp))
                .then(if (isSelected) Modifier.background(PurpleAccent) else Modifier.border(1.5.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(6.dp))),
            contentAlignment = Alignment.Center
        ) {
            if (isSelected) {
                Icon(Icons.Filled.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
            }
        }
        Spacer(Modifier.width(12.dp))
        Text(text = ingredient.emoji, fontSize = 20.sp)
        Spacer(Modifier.width(12.dp))
        Text(text = ingredient.name, color = textColor, fontSize = 16.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun ViewRecipesButton(selectedCount: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(14.dp))
            .clickable(onClick = {})
            .padding(vertical = 14.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Filled.Search, contentDescription = null, tint = TextPrimary, modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(8.dp))
        Text(
            text = "Ver recetas ($selectedCount ingredientes)",
            color = TextPrimary,
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp
        )
    }
}

@Composable
private fun RecipeCard(suggestion: RecipeSuggestion) {
    val recipe = suggestion.recipe
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(SurfaceDark)
            .then(
                if (suggestion.isFullMatch) {
                    Modifier.border(1.5.dp, PurpleAccent, RoundedCornerShape(18.dp))
                } else {
                    Modifier
                }
            )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .background(SurfaceVariantDark),
            contentAlignment = Alignment.Center
        ) {
            Text(text = recipe.emoji, fontSize = 48.sp)
            if (suggestion.isFullMatch) {
                Row(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(10.dp)
                        .clip(RoundedCornerShape(50))
                        .background(PurpleAccent)
                        .padding(horizontal = 10.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Filled.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(13.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Coincidencia total", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
        Column(modifier = Modifier.padding(16.dp)) {
            Text(recipe.title, color = TextPrimary, fontSize = 17.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                RecipeMeta(Icons.Filled.Schedule, "${recipe.minutes} min")
                Spacer(Modifier.width(16.dp))
                RecipeMeta(Icons.Filled.Whatshot, recipe.difficulty)
                Spacer(Modifier.width(16.dp))
                RecipeMeta(Icons.Filled.Group, "${recipe.servings} porciones")
            }
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Usa: ${recipe.ingredientNames.joinToString(", ") { it.lowercase() }}",
                color = PurpleAccentLight,
                fontSize = 13.sp
            )
        }
    }
}

@Composable
private fun RecipeMeta(icon: ImageVector, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(14.dp))
        Spacer(Modifier.width(4.dp))
        Text(label, color = TextSecondary, fontSize = 13.sp)
    }
}

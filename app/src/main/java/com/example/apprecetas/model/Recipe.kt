package com.example.apprecetas.model

data class Recipe(
    val id: String = "",
    val title: String = "",
    val emoji: String = "",
    val minutes: Int = 0,
    val difficulty: String = "",
    val servings: Int = 0,
    val ingredientNames: List<String> = emptyList(),
    val instructions: String = "",
    val imageUrl: String? = null
)

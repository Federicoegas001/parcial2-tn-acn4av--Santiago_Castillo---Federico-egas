package com.example.apprecetas.data

import com.example.apprecetas.model.Ingredient
import com.example.apprecetas.model.PantryItem
import com.example.apprecetas.model.Recipe
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

private val DEFAULT_INGREDIENTS = listOf(
    Ingredient(name = "Huevos", emoji = "🥚"),
    Ingredient(name = "Tomate", emoji = "🍅"),
    Ingredient(name = "Queso", emoji = "🧀"),
    Ingredient(name = "Cebolla", emoji = "🧅"),
    Ingredient(name = "Aceite de oliva", emoji = "🫒")
)

private val DEFAULT_RECIPES = listOf(
    Recipe(
        title = "Tortilla de tomate y queso",
        emoji = "🍳",
        minutes = 15,
        difficulty = "Fácil",
        servings = 2,
        ingredientNames = listOf("Huevos", "Tomate", "Queso")
    ),
    Recipe(
        title = "Huevos revueltos gratinados",
        emoji = "🥘",
        minutes = 10,
        difficulty = "Fácil",
        servings = 1,
        ingredientNames = listOf("Huevos", "Queso")
    )
)

class RecetasRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    private val ingredientsRef = firestore.collection("ingredients")
    private val recipesRef = firestore.collection("recipes")
    private val usersRef = firestore.collection("users")
    private val pantryRef = firestore.collection("pantry")

    suspend fun seedCatalogIfEmpty() {
        if (ingredientsRef.get().await().isEmpty) {
            val batch = firestore.batch()
            DEFAULT_INGREDIENTS.forEach { batch.set(ingredientsRef.document(), it) }
            batch.commit().await()
        }
        if (recipesRef.get().await().isEmpty) {
            val batch = firestore.batch()
            DEFAULT_RECIPES.forEach { batch.set(recipesRef.document(), it) }
            batch.commit().await()
        }
    }

    fun observeIngredients(): Flow<List<Ingredient>> = callbackFlow {
        val registration = ingredientsRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            val ingredients = snapshot?.documents?.mapNotNull { doc ->
                doc.toObject(Ingredient::class.java)?.copy(id = doc.id)
            } ?: emptyList()
            trySend(ingredients)
        }
        awaitClose { registration.remove() }
    }

    fun observeRecipes(): Flow<List<Recipe>> = callbackFlow {
        val registration = recipesRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            val recipes = snapshot?.documents?.mapNotNull { doc ->
                doc.toObject(Recipe::class.java)?.copy(id = doc.id)
            } ?: emptyList()
            trySend(recipes)
        }
        awaitClose { registration.remove() }
    }

    suspend fun getSelectedIngredientNames(userId: String): Set<String> {
        val snapshot = usersRef.document(userId).get().await()
        @Suppress("UNCHECKED_CAST")
        val names = snapshot.get("selectedIngredients") as? List<String> ?: emptyList()
        return names.toSet()
    }

    suspend fun setSelectedIngredientNames(userId: String, names: Set<String>) {
        usersRef.document(userId)
            .set(mapOf("selectedIngredients" to names.toList()), SetOptions.merge())
            .await()
    }

    fun observePantry(): Flow<List<PantryItem>> = callbackFlow {
        val registration = pantryRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            val items = snapshot?.documents?.mapNotNull { doc ->
                doc.toObject(PantryItem::class.java)?.copy(id = doc.id)
            } ?: emptyList()
            trySend(items)
        }
        awaitClose { registration.remove() }
    }

    suspend fun addPantryItem(item: PantryItem) {
        val data = mapOf(
            "name" to item.name,
            "emoji" to item.emoji,
            "category" to item.category,
            "quantity" to item.quantity
        )
        pantryRef.add(data).await()
    }

    suspend fun deletePantryItem(id: String) {
        pantryRef.document(id).delete().await()
    }

    suspend fun updatePantryItem(item: PantryItem) {
        val data = mapOf(
            "name" to item.name,
            "emoji" to item.emoji,
            "category" to item.category,
            "quantity" to item.quantity
        )
        pantryRef.document(item.id).set(data).await()
    }
}

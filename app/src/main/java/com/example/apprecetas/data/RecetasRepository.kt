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
    Ingredient(name = "Aceite de oliva", emoji = "🫒"),
    Ingredient(name = "Ajo", emoji = "🧄"),
    Ingredient(name = "Papa", emoji = "🥔"),
    Ingredient(name = "Pollo", emoji = "🍗"),
    Ingredient(name = "Carne molida", emoji = "🥩"),
    Ingredient(name = "Arroz", emoji = "🍚"),
    Ingredient(name = "Pasta", emoji = "🍝"),
    Ingredient(name = "Manteca", emoji = "🧈"),
    Ingredient(name = "Leche", emoji = "🥛"),
    Ingredient(name = "Harina", emoji = "🌾"),
    Ingredient(name = "Zanahoria", emoji = "🥕"),
    Ingredient(name = "Pimiento", emoji = "🫑"),
    Ingredient(name = "Atún", emoji = "🐟"),
    Ingredient(name = "Espinaca", emoji = "🥬"),
    Ingredient(name = "Lechuga", emoji = "🥗"),
    Ingredient(name = "Jamón", emoji = "🥓")
)

private val DEFAULT_RECIPES = listOf(
    Recipe(
        title = "Tortilla de tomate y queso",
        emoji = "🍳",
        minutes = 15,
        difficulty = "Fácil",
        servings = 2,
        ingredientNames = listOf("Huevos", "Tomate", "Queso"),
        instructions = "1. Batí los huevos en un bol con una pizca de sal.\n\n2. Cortá el tomate en cubos pequeños.\n\n3. Calentá aceite en una sartén y dorá el tomate 2 minutos.\n\n4. Vertí los huevos batidos y agregá el queso rallado encima.\n\n5. Cociná a fuego lento 5 minutos, dala vuelta con cuidado y dejá gratinar el queso."
    ),
    Recipe(
        title = "Huevos revueltos gratinados",
        emoji = "🥘",
        minutes = 10,
        difficulty = "Fácil",
        servings = 1,
        ingredientNames = listOf("Huevos", "Queso"),
        instructions = "1. Rompé los huevos directamente en una sartén fría con manteca o aceite.\n\n2. Llevá a fuego medio y revolvé constantemente con espátula.\n\n3. Retirá del fuego intermitentemente para que queden bien cremosos.\n\n4. En el último minuto espolvoreá el queso, tapá la sartén y dejá que se derrita."
    ),
    Recipe(
        title = "Arroz con pollo",
        emoji = "🍗",
        minutes = 40,
        difficulty = "Media",
        servings = 4,
        ingredientNames = listOf("Arroz", "Pollo", "Cebolla", "Ajo", "Tomate"),
        instructions = "1. Cortá el pollo en trozos y salpimentá.\n\n2. Dorá el pollo en aceite caliente hasta que esté dorado. Reservá.\n\n3. En la misma sartén sofreí la cebolla y el ajo picados 3 minutos.\n\n4. Agregá el tomate picado y cociná 5 minutos más.\n\n5. Sumá el arroz, revolvé 2 minutos y cubrí con el doble de agua o caldo.\n\n6. Volvé a poner el pollo, tapá y cociná 20 minutos a fuego bajo hasta que el arroz esté listo."
    ),
    Recipe(
        title = "Pasta con tomate y ajo",
        emoji = "🍝",
        minutes = 20,
        difficulty = "Fácil",
        servings = 2,
        ingredientNames = listOf("Pasta", "Tomate", "Ajo", "Aceite de oliva"),
        instructions = "1. Cociná la pasta en agua con sal según el tiempo del paquete.\n\n2. Mientras, picá finamente el ajo y sofreílo en aceite de oliva 1 minuto a fuego bajo.\n\n3. Agregá el tomate picado, salpimentá y cociná 10 minutos a fuego medio.\n\n4. Escurrí la pasta y mezclala con la salsa.\n\n5. Serví con queso rallado si tenés."
    ),
    Recipe(
        title = "Puré de papas",
        emoji = "🥔",
        minutes = 30,
        difficulty = "Fácil",
        servings = 4,
        ingredientNames = listOf("Papa", "Manteca", "Leche"),
        instructions = "1. Pelá y cortá las papas en trozos medianos.\n\n2. Hervilas en agua con sal hasta que estén tiernas, unos 20 minutos.\n\n3. Escurrí bien y pisalas con un pisapapas mientras están calientes.\n\n4. Agregá la manteca en cubos y la leche caliente de a poco, mezclando hasta obtener una textura suave y cremosa.\n\n5. Rectificá la sal y serví inmediatamente."
    ),
    Recipe(
        title = "Pollo al horno con ajo",
        emoji = "🍗",
        minutes = 45,
        difficulty = "Fácil",
        servings = 4,
        ingredientNames = listOf("Pollo", "Ajo", "Aceite de oliva"),
        instructions = "1. Precalentá el horno a 200°C.\n\n2. Hacé cortes en el pollo e insertá láminas de ajo en cada corte.\n\n3. Frotá el pollo con aceite de oliva, sal y pimienta por todos lados.\n\n4. Colocá en una fuente para horno y horneá 40-45 minutos, rociando con los jugos cada 15 minutos.\n\n5. Dejá reposar 5 minutos antes de servir."
    ),
    Recipe(
        title = "Sopa de verduras",
        emoji = "🥣",
        minutes = 35,
        difficulty = "Fácil",
        servings = 4,
        ingredientNames = listOf("Zanahoria", "Papa", "Cebolla", "Ajo"),
        instructions = "1. Pelá y cortá en cubos la zanahoria, la papa y la cebolla. Picá el ajo.\n\n2. Sofreí la cebolla y el ajo en aceite 3 minutos.\n\n3. Agregá la zanahoria y la papa, revolvé 2 minutos.\n\n4. Cubrí con agua o caldo, llevá a hervor y cociná a fuego medio 25 minutos hasta que las verduras estén tiernas.\n\n5. Salpimentá, rectificá y serví caliente."
    ),
    Recipe(
        title = "Tortilla española",
        emoji = "🍳",
        minutes = 30,
        difficulty = "Media",
        servings = 4,
        ingredientNames = listOf("Papa", "Huevos", "Cebolla", "Aceite de oliva"),
        instructions = "1. Pelá y cortá las papas en rodajas finas. Picá la cebolla.\n\n2. Freí las papas y la cebolla en aceite abundante a fuego bajo durante 15 minutos hasta que estén tiernas. Escurrí bien.\n\n3. Batí los huevos con sal en un bol y mezclá con las papas y la cebolla.\n\n4. Cociná en sartén antiadherente a fuego bajo 5 minutos por un lado.\n\n5. Dala vuelta con la ayuda de un plato y cociná el otro lado 4 minutos más hasta que esté firme."
    ),
    Recipe(
        title = "Pasta a la carbonara",
        emoji = "🍝",
        minutes = 20,
        difficulty = "Media",
        servings = 2,
        ingredientNames = listOf("Pasta", "Huevos", "Queso"),
        instructions = "1. Cociná la pasta en agua con sal. Reservá una taza del agua de cocción antes de escurrir.\n\n2. Batí los huevos con el queso rallado y pimienta negra generosa en un bol.\n\n3. Escurrí la pasta y ponerla en la olla caliente pero fuera del fuego.\n\n4. Agregá la mezcla de huevo y queso revolviendo rápido. Sumá agua de cocción de a poco hasta lograr una salsa cremosa.\n\n5. Serví inmediatamente con más queso encima."
    ),
    Recipe(
        title = "Arroz con atún",
        emoji = "🍚",
        minutes = 25,
        difficulty = "Fácil",
        servings = 2,
        ingredientNames = listOf("Arroz", "Atún", "Cebolla", "Tomate"),
        instructions = "1. Cociná el arroz según las instrucciones del paquete.\n\n2. Picá la cebolla y sofreíla en aceite hasta que esté transparente, unos 4 minutos.\n\n3. Agregá el tomate picado y cociná 5 minutos más.\n\n4. Sumá el atún bien escurrido, mezclá y cociná 2 minutos.\n\n5. Mezclá todo con el arroz cocido, rectificá la sal y serví."
    ),
    Recipe(
        title = "Fideos con manteca y queso",
        emoji = "🧀",
        minutes = 15,
        difficulty = "Fácil",
        servings = 2,
        ingredientNames = listOf("Pasta", "Manteca", "Queso"),
        instructions = "1. Cociná la pasta en abundante agua con sal hasta que esté al dente.\n\n2. Escurrí y volvé a ponerla en la olla caliente fuera del fuego.\n\n3. Agregá la manteca cortada en cubos y revolvé hasta que se derrita completamente.\n\n4. Sumá el queso rallado generosamente y mezclá bien.\n\n5. Serví inmediatamente antes de que enfríe."
    ),
    Recipe(
        title = "Revuelto de espinaca",
        emoji = "🥬",
        minutes = 15,
        difficulty = "Fácil",
        servings = 2,
        ingredientNames = listOf("Espinaca", "Huevos", "Ajo"),
        instructions = "1. Lavá bien la espinaca y retirá los tallos más gruesos.\n\n2. Picá el ajo finamente y sofreílo en aceite a fuego medio 1 minuto.\n\n3. Agregá la espinaca y cociná 2-3 minutos revolviendo hasta que se reduzca y pierda el agua.\n\n4. Batí los huevos con sal y vertílos sobre la espinaca.\n\n5. Revolvé constantemente a fuego medio-bajo hasta que los huevos estén cocidos pero cremosos."
    ),
    Recipe(
        title = "Ensalada de atún",
        emoji = "🥗",
        minutes = 10,
        difficulty = "Fácil",
        servings = 2,
        ingredientNames = listOf("Atún", "Lechuga", "Tomate"),
        instructions = "1. Lavá y escurrí bien la lechuga. Cortala en trozos con la mano.\n\n2. Cortá el tomate en cubos o rodajas.\n\n3. Escurrí bien el atún.\n\n4. Mezclá la lechuga, el tomate y el atún en un bol.\n\n5. Aderezá con aceite de oliva, sal y unas gotas de limón si tenés. Serví frío."
    ),
    Recipe(
        title = "Salteado de pollo",
        emoji = "🥘",
        minutes = 25,
        difficulty = "Media",
        servings = 2,
        ingredientNames = listOf("Pollo", "Pimiento", "Cebolla", "Ajo"),
        instructions = "1. Cortá el pollo en tiras finas. Cortá el pimiento y la cebolla en juliana. Picá el ajo.\n\n2. Calentá aceite en sartén o wok a fuego alto.\n\n3. Saltéa el pollo 4-5 minutos hasta que esté dorado. Reservá.\n\n4. En la misma sartén, saltéa la cebolla, el pimiento y el ajo 3 minutos a fuego alto.\n\n5. Volvé a agregar el pollo, mezclá bien, salpimentá y serví."
    ),
    Recipe(
        title = "Milanesas",
        emoji = "🍖",
        minutes = 30,
        difficulty = "Media",
        servings = 4,
        ingredientNames = listOf("Carne molida", "Huevos", "Harina"),
        instructions = "1. Formá medallones planos con la carne molida y salpimentá.\n\n2. Batí los huevos con sal y pimienta en un plato hondo.\n\n3. Ponés la harina extendida en otro plato.\n\n4. Pasás cada milanesa primero por harina (sacudí el exceso) y luego por el huevo batido.\n\n5. Freís en aceite caliente a fuego medio-alto 3 minutos por lado hasta que estén doradas.\n\n6. Escurrís sobre papel absorbente y servís."
    )
)

private val INGREDIENT_CATEGORIES = mapOf(
    "Huevos" to "Proteínas",
    "Tomate" to "Verduras",
    "Queso" to "Lácteos",
    "Cebolla" to "Verduras",
    "Aceite de oliva" to "Otros",
    "Ajo" to "Verduras",
    "Papa" to "Verduras",
    "Pollo" to "Carnes",
    "Carne molida" to "Carnes",
    "Arroz" to "Otros",
    "Pasta" to "Otros",
    "Manteca" to "Lácteos",
    "Leche" to "Lácteos",
    "Harina" to "Otros",
    "Zanahoria" to "Verduras",
    "Pimiento" to "Verduras",
    "Atún" to "Proteínas",
    "Espinaca" to "Verduras",
    "Lechuga" to "Verduras",
    "Jamón" to "Carnes"
)

// Declaramos explícitamente la clase para que Android la reconozca
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

    suspend fun seedUserPantryIfEmpty(userId: String) {
        val snapshot = pantryRef.whereEqualTo("userId", userId).get().await()
        if (snapshot.isEmpty) {
            val batch = firestore.batch()
            DEFAULT_INGREDIENTS.forEach { ingredient ->
                val data = mapOf(
                    "name" to ingredient.name,
                    "emoji" to ingredient.emoji,
                    "category" to (INGREDIENT_CATEGORIES[ingredient.name] ?: "Otros"),
                    "quantity" to "",
                    "userId" to userId
                )
                batch.set(pantryRef.document(), data)
            }
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

    fun observePantry(userId: String): Flow<List<PantryItem>> = callbackFlow {
        val registration = pantryRef
            .whereEqualTo("userId", userId)
            .addSnapshotListener { snapshot, error ->
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
            "quantity" to item.quantity,
            "userId" to item.userId
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
            "quantity" to item.quantity,
            "userId" to item.userId
        )
        pantryRef.document(item.id).set(data).await()
    }
}
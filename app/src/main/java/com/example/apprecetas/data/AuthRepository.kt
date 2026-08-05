package com.example.apprecetas.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class AuthRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    data class UserProfile(
        val nombre: String,
        val email: String
    )

    fun currentUser(): FirebaseUser? = auth.currentUser

    fun currentUserId(): String? = auth.currentUser?.uid

    suspend fun login(email: String, password: String): FirebaseUser {
        val result = auth.signInWithEmailAndPassword(email.trim(), password).await()
        return result.user ?: error("No se pudo obtener el usuario autenticado")
    }

    suspend fun register(nombre: String, email: String, password: String): FirebaseUser {
        val result = auth.createUserWithEmailAndPassword(email.trim(), password).await()
        val user = result.user ?: error("No se pudo crear el usuario")
        try {
            firestore.collection(USERS_COLLECTION)
                .document(user.uid)
                .set(UserProfile(nombre.trim(), email.trim()))
                .await()
        } catch (exception: Exception) {
            user.delete().await()
            throw exception
        }
        return user
    }

    suspend fun getUserProfile(uid: String = currentUserId() ?: error("No hay una sesión iniciada")): UserProfile {
        val snapshot = firestore.collection(USERS_COLLECTION).document(uid).get().await()
        if (!snapshot.exists()) error("No se encontró el perfil del usuario")

        return UserProfile(
            nombre = snapshot.getString("nombre").orEmpty(),
            email = snapshot.getString("email").orEmpty()
        )
    }

    fun logout() = auth.signOut()

    private companion object {
        const val USERS_COLLECTION = "usuarios"
    }
}

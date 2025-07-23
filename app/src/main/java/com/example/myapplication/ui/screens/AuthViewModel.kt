package com.example.myapplication.ui.screens

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.AuthResult
import kotlinx.coroutines.tasks.await

class AuthViewModel : ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    suspend fun loginEmailSenha(email: String, senha: String): AuthResult? {
        return try {
            auth.signInWithEmailAndPassword(email, senha).await()
        } catch (e: Exception) {
            null
        }
    }

    suspend fun registrarEmailSenha(email: String, senha: String): AuthResult? {
        return try {
            auth.createUserWithEmailAndPassword(email, senha).await()
        } catch (e: Exception) {
            null
        }
    }

    fun logout() {
        auth.signOut()
    }

    fun usuarioAtual() = auth.currentUser
}

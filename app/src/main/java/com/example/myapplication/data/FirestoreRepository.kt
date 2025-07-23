package com.example.myapplication.data

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.tasks.await

class FirestoreRepository {
    private val db = FirebaseFirestore.getInstance().collection("produtos")

    suspend fun salvarProduto(nome: String, preco: Double) {
        val produto = mapOf("nome" to nome, "preco" to preco)
        db.add(produto).await()
    }

    fun escutarProdutos(onChange: (List<Map<String, Any>>) -> Unit): ListenerRegistration {
        return db.addSnapshotListener { snapshot, _ ->
            val lista = snapshot?.documents?.mapNotNull { it.data } ?: emptyList()
            onChange(lista)
        }
    }
}

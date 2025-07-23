package com.example.myapplication.data

import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await

class FirebaseRepository {
    private val db = FirebaseDatabase.getInstance().getReference("produtos")

    suspend fun salvarProduto(id: String, nome: String, preco: Double) {
        val produto = mapOf("nome" to nome, "preco" to preco)
        db.child(id).setValue(produto).await()
    }

    fun escutarProdutos(onChange: (Map<String, Any>?) -> Unit) {
        db.addValueEventListener(object : com.google.firebase.database.ValueEventListener {
            override fun onDataChange(snapshot: com.google.firebase.database.DataSnapshot) {
                val data = snapshot.value
                if (data is Map<*, *>) {
                    @Suppress("UNCHECKED_CAST")
                    onChange(data as Map<String, Any>)
                } else {
                    onChange(null)
                }
            }
            override fun onCancelled(error: com.google.firebase.database.DatabaseError) {}
        })
    }
}

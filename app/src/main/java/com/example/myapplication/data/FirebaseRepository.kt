package com.example.myapplication.data

import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await
import android.net.Uri
import com.google.firebase.storage.FirebaseStorage

class FirebaseRepository {
    val db = com.google.firebase.database.FirebaseDatabase.getInstance().getReference("receitas")
    private val storage = FirebaseStorage.getInstance().getReference("receita_images")

    suspend fun salvarReceita(
        id: String,
        nome: String,
        descricaoCurta: String,
        imagemUri: Uri?,
        ingredientes: List<String>,
        modoPreparo: List<String>,
        tempoPreparo: String,
        porcoes: Int,
        userId: String,
        userEmail: String?,
        imagemUrl: String?
    ): String? {
        var imageUrl = ""
        if (imagemUri != null) {
            val imageRef = storage.child("${id}_${System.currentTimeMillis()}")
            val uploadTask = imageRef.putFile(imagemUri).await()
            imageUrl = imageRef.downloadUrl.await().toString()
        }
        val receita = mapOf(
            "id" to id,
            "nome" to nome,
            "descricaoCurta" to descricaoCurta,
            "imagemUrl" to (imagemUrl ?: ""),
            "ingredientes" to ingredientes,
            "modoPreparo" to modoPreparo,
            "tempoPreparo" to tempoPreparo,
            "porcoes" to porcoes,
            "userId" to userId,
            "userEmail" to userEmail
        )
        db.child(id).setValue(receita).await()
        return imagemUrl
    }

    fun escutarReceitas(onChange: (Map<String, Any>?) -> Unit) {
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

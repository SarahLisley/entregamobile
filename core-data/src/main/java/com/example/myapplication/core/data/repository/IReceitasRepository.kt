package com.example.myapplication.core.data.repository

import android.content.Context
import android.net.Uri
import com.example.myapplication.core.data.database.entity.ReceitaEntity
import kotlinx.coroutines.flow.Flow

// Interface comum para repositories de receitas
interface IReceitasRepository {
    fun getReceitas(): Flow<List<ReceitaEntity>>
    suspend fun getReceitaById(id: String): ReceitaEntity?
    suspend fun addReceita(receita: ReceitaEntity): Result<Unit>
    suspend fun updateReceita(receita: ReceitaEntity): Result<Unit>
    suspend fun deleteReceita(id: String): Result<Unit>
    suspend fun deletarReceita(id: String, imageUrl: String?): Result<Unit>
    suspend fun atualizarCurtidas(id: String, curtidas: List<String>): Result<Unit>
    suspend fun atualizarFavoritos(id: String, favoritos: List<String>): Result<Unit>
    suspend fun salvarReceita(
        context: Context,
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
    ): Result<String>
    suspend fun sincronizarComFirebase(): Result<Unit>
    suspend fun escutarReceitas(callback: (List<ReceitaEntity>) -> Unit)
    suspend fun updateImage(context: Context, novaImagemUri: Uri, imagemUrlAntiga: String?, id: String): String?
    suspend fun curtirReceita(id: String, userId: String, curtidas: List<String>): Result<Unit>
    suspend fun favoritarReceita(id: String, userId: String, favoritos: List<String>): Result<Unit>
    suspend fun syncFromFirebase(): Result<Int>
    
    // Métodos de verificação de permissões
    fun canEditReceita(receita: ReceitaEntity, currentUserId: String?): Boolean
    fun canDeleteReceita(receita: ReceitaEntity, currentUserId: String?): Boolean
    
    // Método para limpar todos os dados locais
    suspend fun clearAllLocalData(): Result<Unit>
} 
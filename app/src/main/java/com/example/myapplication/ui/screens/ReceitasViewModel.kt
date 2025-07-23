package com.example.myapplication.ui.screens

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.example.myapplication.model.DadosMockados
import com.example.myapplication.model.Receita

class ReceitasViewModel : ViewModel() {
    // Lista mutável de receitas
    val receitas = mutableStateListOf<Receita>().apply {
        addAll(DadosMockados.listaDeReceitas)
    }

    fun toggleFavorito(receitaId: Int) {
        val idx = receitas.indexOfFirst { it.id == receitaId }
        if (idx >= 0) {
            val atual = receitas[idx]
            receitas[idx] = atual.copy(isFavorita = !atual.isFavorita)
        }
    }

    fun editarNome(receitaId: Int, novoNome: String) {
        val idx = receitas.indexOfFirst { it.id == receitaId }
        if (idx >= 0) {
            val atual = receitas[idx]
            receitas[idx] = atual.copy(nome = novoNome)
        }
    }

    fun adicionarReceita(nome: String) {
        val novoId = (receitas.maxOfOrNull { it.id } ?: 0) + 1
        receitas.add(0, DadosMockados.listaDeReceitas.first().copy(
            id = novoId,
            nome = nome,
            descricaoCurta = "Novo item adicionado",
            imagemUrl = ""
        ))
    }

    fun getFavoritos(): List<Receita> = receitas.filter { it.isFavorita }
}

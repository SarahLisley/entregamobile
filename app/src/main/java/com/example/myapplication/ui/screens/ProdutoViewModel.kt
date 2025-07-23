package com.example.myapplication.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.RetrofitInstance
import com.example.myapplication.model.Produto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProdutoViewModel : ViewModel() {
    private val _produtos = MutableStateFlow<List<Produto>>(emptyList())
    val produtos: StateFlow<List<Produto>> = _produtos

    fun buscarProdutos() {
        viewModelScope.launch {
            try {
                _produtos.value = RetrofitInstance.api.getProdutos()
            } catch (e: Exception) {
                // Trate o erro
            }
        }
    }

    fun adicionarProduto(produto: Produto) {
        viewModelScope.launch {
            try {
                RetrofitInstance.api.addProduto(produto)
                buscarProdutos() // Atualiza a lista após adicionar
            } catch (e: Exception) {
                // Trate o erro
            }
        }
    }
}

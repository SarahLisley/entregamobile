package com.example.myapplication.ui.screens

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.AppDatabase
import com.example.myapplication.model.ShoppingItem
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ShoppingListViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = AppDatabase.getDatabase(application).shoppingItemDao()
    val items = dao.getAll().stateIn(viewModelScope, SharingStarted.Lazily, emptyList<ShoppingItem>())

    fun addItem(item: ShoppingItem) = viewModelScope.launch {
        dao.insert(item)
    }

    fun updateItem(item: ShoppingItem) = viewModelScope.launch {
        dao.update(item)
    }

    fun deleteItem(item: ShoppingItem) = viewModelScope.launch {
        dao.delete(item)
    }
}

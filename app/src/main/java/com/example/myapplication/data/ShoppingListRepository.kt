package com.example.myapplication.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.myapplication.model.Receita
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ShoppingListRepository(private val context: Context) {
    private val SHOPPING_LIST_KEY = stringPreferencesKey("shopping_list")

    fun getShoppingList(): Flow<List<Receita>> = context.dataStore.data
        .map { preferences ->
            val json = preferences[SHOPPING_LIST_KEY] ?: "[]"
            Gson().fromJson(json, object : TypeToken<List<Receita>>() {}.type)
        }

    suspend fun saveShoppingList(list: List<Receita>) {
        val json = Gson().toJson(list)
        context.dataStore.edit { preferences ->
            preferences[SHOPPING_LIST_KEY] = json
        }
    }
} 
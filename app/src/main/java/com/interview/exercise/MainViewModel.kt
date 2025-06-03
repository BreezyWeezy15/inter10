package com.interview.exercise

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.core.content.edit
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


/**
 * ViewModel for managing a list of items and the selected item in an MVVM architecture.
 *
 * Responsibilities:
 * - Generate or load a list of items from SharedPreferences.
 * - Track the currently selected item.
 * - Persist both the item list and selected item across app restarts.
 *
 * This ViewModel uses Gson to serialize/deserialize the item list into JSON
 * for persistence via SharedPreferences.
 */

class MainViewModel(application: Application) : AndroidViewModel(application) {

    // SharedPreferences for persisting selected item and item list
    private val prefs = application.getSharedPreferences("ItemPrefs", Context.MODE_PRIVATE)

    // Gson instance for serializing/deserializing item list
    private val gson = Gson()

    // Backing property for the LiveData item list
    private val _items = MutableLiveData<List<Item>>()

    /** Public LiveData containing the list of items */
    val items: LiveData<List<Item>> = _items

    // Backing property for the LiveData selected item ID
    private val _selectedId = MutableLiveData<Int?>()

    /** Public LiveData representing the ID of the currently selected item */
    val selectedId: LiveData<Int?> = _selectedId

    companion object {
        private const val SELECTED_ID_KEY = "SelectedId"  // Key for selected item ID
        private const val ITEMS_KEY = "ItemList"          // Key for item list
    }

    init {
        // Attempt to load previously saved item list from SharedPreferences
        val savedListJson = prefs.getString(ITEMS_KEY, null)

        val itemList = if (savedListJson != null) {
            // Deserialize saved list from JSON
            val type = object : TypeToken<List<Item>>() {}.type
            gson.fromJson(savedListJson, type)
        } else {
            // No saved list, generate a default list of 20 items
            val newList = List(20) { Item(it, "Item ${it + 1}") }
            saveItemList(newList)
            newList
        }

        _items.value = itemList

        // Load previously selected item ID if available
        _selectedId.value = prefs.getInt(SELECTED_ID_KEY, -1).takeIf { it != -1 }
    }

    /**
     * Selects an item, persists its ID to SharedPreferences,
     * and updates LiveData to notify observers.
     */
    fun selectItem(item: Item) {
        _selectedId.value = item.id
        prefs.edit { putInt(SELECTED_ID_KEY, item.id) }
    }

    /**
     * Persists the entire item list to SharedPreferences in JSON format.
     */
    private fun saveItemList(list: List<Item>) {
        val json = gson.toJson(list)
        prefs.edit { putString(ITEMS_KEY, json) }
    }
}

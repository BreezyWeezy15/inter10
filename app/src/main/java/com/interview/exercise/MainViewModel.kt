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
 * - Track the currently selected item(s).
 * - Persist both the item list and selected item IDs across app restarts.
 *
 * This ViewModel uses Gson to serialize/deserialize the item list into JSON
 * for persistence via SharedPreferences.
 */

class MainViewModel(application: Application) : AndroidViewModel(application) {

    // SharedPreferences instance for persistence
    private val prefs = application.getSharedPreferences("ItemPrefs", Context.MODE_PRIVATE)

    // Gson instance for JSON serialization/deserialization
    private val gson = Gson()

    // LiveData holding the list of items
    private val _items = MutableLiveData<List<Item>>()
    val items: LiveData<List<Item>> = _items

    // LiveData holding the set of selected item IDs
    private val _selectedIds = MutableLiveData<Set<Int>>(emptySet())
    val selectedIds: LiveData<Set<Int>> = _selectedIds

    companion object {
        private const val SELECTED_IDS_KEY = "SelectedIds" // Key for selected item IDs in SharedPreferences
        private const val ITEMS_KEY = "ItemList"           // Key for item list in SharedPreferences
    }

    init {
        // Load item list from SharedPreferences, or create and save a default list if not present
        val savedListJson = prefs.getString(ITEMS_KEY, null)
        val itemList = if (savedListJson != null) {
            val type = object : TypeToken<List<Item>>() {}.type
            gson.fromJson(savedListJson, type)
        } else {
            val newList = List(20) { Item(it, "Item ${it + 1}") }
            saveItemList(newList)
            newList
        }
        _items.value = itemList

        // Load selected item IDs from SharedPreferences
        val savedIds = prefs.getStringSet(SELECTED_IDS_KEY, emptySet())
            ?.mapNotNull { it.toIntOrNull() }
            ?.toSet()
            ?: emptySet()
        _selectedIds.value = savedIds
    }

    /**
     * Toggle selection state of a given item.
     * Adds the item ID to the selected set if not selected, or removes it if already selected.
     * Updates and saves the new selected set to SharedPreferences.
     */
    fun toggleItemSelection(item: Item) {
        val currentSet = _selectedIds.value?.toMutableSet() ?: mutableSetOf()
        if (currentSet.contains(item.id)) {
            currentSet.remove(item.id)
        } else {
            currentSet.add(item.id)
        }
        _selectedIds.value = currentSet
        saveSelectedIds(currentSet)
    }

    /**
     * Save the current list of items to SharedPreferences as JSON.
     */
    private fun saveItemList(list: List<Item>) {
        val json = gson.toJson(list)
        prefs.edit { putString(ITEMS_KEY, json) }
    }

    /**
     * Save the selected item IDs to SharedPreferences as a Set<String>.
     */
    private fun saveSelectedIds(ids: Set<Int>) {
        val stringSet = ids.map { it.toString() }.toSet()
        prefs.edit { putStringSet(SELECTED_IDS_KEY, stringSet) }
    }
}


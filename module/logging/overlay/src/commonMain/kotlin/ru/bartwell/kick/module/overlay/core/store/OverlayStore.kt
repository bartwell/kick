package ru.bartwell.kick.module.overlay.core.store

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

internal const val DEFAULT_CATEGORY: String = "Default"

internal object OverlayStore {
    private val categoriesMap: LinkedHashMap<String, LinkedHashMap<String, String>> = LinkedHashMap()

    private val _items = MutableStateFlow<List<Pair<String, String>>>(emptyList())
    val items: StateFlow<List<Pair<String, String>>> = _items.asStateFlow()

    private val _categories = MutableStateFlow(listOf(DEFAULT_CATEGORY))
    val categories: StateFlow<List<String>> = _categories.asStateFlow()

    private val _selectedCategory = MutableStateFlow(DEFAULT_CATEGORY)
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    internal fun addCategory(category: String) {
        categoriesMap.getOrPut(category) { LinkedHashMap() }
        updateCategoriesList(category)
    }

    fun set(key: String, value: String) {
        set(key = key, value = value, category = DEFAULT_CATEGORY)
    }

    fun set(key: String, value: String, category: String) {
        val cat = category.ifBlank { DEFAULT_CATEGORY }
        val mapForCategory = categoriesMap.getOrPut(cat) { LinkedHashMap() }
        mapForCategory[key] = value
        updateCategoriesList(extra = cat)
        updateItems()
    }

    fun clear() {
        categoriesMap.clear()
        updateCategoriesList()
        updateItems()
    }

    fun selectCategory(category: String) {
        val cat = category.ifBlank { DEFAULT_CATEGORY }
        _selectedCategory.value = cat
        updateCategoriesList(extra = cat)
        updateItems()
    }

    private fun updateItems() {
        val cat = _selectedCategory.value
        val mapForCategory = categoriesMap[cat]
        _items.value = mapForCategory?.entries?.map { it.key to it.value } ?: emptyList()
    }

    private fun updateCategoriesList(extra: String? = null) {
        val set = LinkedHashSet<String>()
        set.add(DEFAULT_CATEGORY)
        set.addAll(categoriesMap.keys)
        extra?.let { set.add(it) }
        set.add(_selectedCategory.value)
        _categories.value = set.toList()
    }
}

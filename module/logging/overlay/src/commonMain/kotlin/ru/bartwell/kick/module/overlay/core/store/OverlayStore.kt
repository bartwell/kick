package ru.bartwell.kick.module.overlay.core.store

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

internal const val DEFAULT_CATEGORY: String = "Default"

private const val EMBEDDED_CATEGORY_SEPARATOR = "::"

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
        val (resolvedKey, category) = resolveKeyAndCategory(key)
        set(key = resolvedKey, value = value, category = category)
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

    private fun resolveKeyAndCategory(key: String): Pair<String, String> {
        val separatorIndex = key.indexOf(EMBEDDED_CATEGORY_SEPARATOR)
        val hasCategoryPrefix = separatorIndex > 0 &&
            separatorIndex < key.length - EMBEDDED_CATEGORY_SEPARATOR.length

        return if (hasCategoryPrefix) {
            key.substring(separatorIndex + EMBEDDED_CATEGORY_SEPARATOR.length) to key.substring(0, separatorIndex)
        } else {
            key to DEFAULT_CATEGORY
        }
    }
}

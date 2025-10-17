package ru.bartwell.kick.module.overlay.core.store

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

internal const val DEFAULT_CATEGORY: String = "Default"

internal object OverlayStore {
    private val categoriesMap: LinkedHashMap<String, LinkedHashMap<String, String>> = LinkedHashMap()
    private val declaredCategories: LinkedHashSet<String> = LinkedHashSet()
    private const val KEY_CATEGORY_DELIMITER: String = "::"

    private val _items = MutableStateFlow<List<Pair<String, String>>>(emptyList())
    val items: StateFlow<List<Pair<String, String>>> = _items.asStateFlow()

    private val _categories = MutableStateFlow<List<String>>(listOf(DEFAULT_CATEGORY))
    val categories: StateFlow<List<String>> = _categories.asStateFlow()

    private val _selectedCategory = MutableStateFlow(DEFAULT_CATEGORY)
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    fun set(key: String, value: String) {
        val (category, normalizedKey) = splitKey(key)
        setInternal(
            key = normalizedKey,
            value = value,
            category = category,
        )
    }

    fun set(key: String, value: String, category: String) {
        setInternal(
            key = key,
            value = value,
            category = category.ifBlank { DEFAULT_CATEGORY },
        )
    }

    fun clear() {
        categoriesMap.clear()
        declaredCategories.clear()
        updateCategoriesList()
        updateItems()
    }

    fun selectCategory(category: String) {
        val resolvedCategory = category.ifBlank { DEFAULT_CATEGORY }
        _selectedCategory.value = resolvedCategory
        updateCategoriesList(extra = resolvedCategory)
        updateItems()
    }

    fun declareCategories(categories: Collection<String>) {
        var changed = false
        for (category in categories) {
            val normalized = category.ifBlank { DEFAULT_CATEGORY }
            if (declaredCategories.add(normalized)) {
                changed = true
            }
        }
        if (changed) {
            updateCategoriesList()
        }
    }

    private fun updateItems() {
        val category = _selectedCategory.value
        val mapForCategory = categoriesMap[category]
        _items.value = mapForCategory?.entries?.map { it.key to it.value } ?: emptyList()
    }

    private fun updateCategoriesList(extra: String? = null) {
        val set = LinkedHashSet<String>()
        set.add(DEFAULT_CATEGORY)
        set.addAll(declaredCategories)
        set.addAll(categoriesMap.keys)
        extra?.let { set.add(it) }
        set.add(_selectedCategory.value)
        _categories.value = set.toList()
    }

    private fun setInternal(key: String, value: String, category: String) {
        val mapForCategory = categoriesMap.getOrPut(category) { LinkedHashMap() }
        mapForCategory[key] = value
        updateCategoriesList(extra = category)
        updateItems()
    }

    private fun splitKey(key: String): Pair<String, String> {
        val delimiterIndex = key.indexOf(KEY_CATEGORY_DELIMITER)
        if (delimiterIndex <= 0) {
            return DEFAULT_CATEGORY to key
        }

        val category = key.substring(0, delimiterIndex).ifBlank { DEFAULT_CATEGORY }
        val normalizedKey = key.substring(delimiterIndex + KEY_CATEGORY_DELIMITER.length)
            .takeIf { it.isNotEmpty() }
            ?: key

        return category to normalizedKey
    }
}

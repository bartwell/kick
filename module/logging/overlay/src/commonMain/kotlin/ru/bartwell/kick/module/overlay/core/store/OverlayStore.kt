package ru.bartwell.kick.module.overlay.core.store

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

internal const val DEFAULT_CATEGORY: String = "Default"

internal object OverlayStore {
    private val categoriesMapState: MutableStateFlow<LinkedHashMap<String, LinkedHashMap<String, String>>> =
        MutableStateFlow(linkedMapOf<String, LinkedHashMap<String, String>>())
    private val declaredCategoriesState: MutableStateFlow<LinkedHashSet<String>> =
        MutableStateFlow(linkedSetOf<String>())
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
        categoriesMapState.value = linkedMapOf<String, LinkedHashMap<String, String>>()
        declaredCategoriesState.value = linkedSetOf<String>()
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
        declaredCategoriesState.update { current ->
            val updated = LinkedHashSet(current)
            for (category in categories) {
                val normalized = category.ifBlank { DEFAULT_CATEGORY }
                if (updated.add(normalized)) {
                    changed = true
                }
            }
            updated
        }
        if (changed) {
            updateCategoriesList()
        }
    }

    private fun updateItems() {
        val category = _selectedCategory.value
        val mapForCategory = categoriesMapState.value[category]
        _items.value = mapForCategory?.entries?.map { it.key to it.value } ?: emptyList()
    }

    private fun updateCategoriesList(extra: String? = null) {
        val set = LinkedHashSet<String>()
        set.add(DEFAULT_CATEGORY)
        set.addAll(declaredCategoriesState.value)
        set.addAll(categoriesMapState.value.keys)
        extra?.let { set.add(it) }
        set.add(_selectedCategory.value)
        _categories.value = set.toList()
    }

    private fun setInternal(key: String, value: String, category: String) {
        categoriesMapState.update { current ->
            val updated = LinkedHashMap<String, LinkedHashMap<String, String>>(current.size + 1)
            for ((existingCategory, existingValues) in current) {
                updated[existingCategory] = LinkedHashMap(existingValues)
            }
            val mapForCategory = updated.getOrPut(category) { LinkedHashMap() }
            mapForCategory[key] = value
            updated
        }
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

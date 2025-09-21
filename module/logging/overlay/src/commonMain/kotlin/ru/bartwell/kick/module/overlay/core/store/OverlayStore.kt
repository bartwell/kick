package ru.bartwell.kick.module.overlay.core.store

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

internal object OverlayStore {
    private val map: LinkedHashMap<String, String> = LinkedHashMap()
    private val _items = MutableStateFlow<List<Pair<String, String>>>(emptyList())
    val items: StateFlow<List<Pair<String, String>>> = _items.asStateFlow()

    fun set(key: String, value: String) {
        map[key] = value
        _items.value = map.entries.map { it.key to it.value }
    }

    fun clear() {
        map.clear()
        _items.value = emptyList()
    }
}

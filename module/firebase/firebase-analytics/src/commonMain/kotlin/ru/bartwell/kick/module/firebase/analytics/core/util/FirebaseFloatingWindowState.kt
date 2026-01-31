package ru.bartwell.kick.module.firebase.analytics.core.util

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds
import ru.bartwell.kick.module.firebase.analytics.core.overlay.FirebaseFloatingWindowHost
import ru.bartwell.kick.module.firebase.analytics.core.persist.FirebaseFloatingWindowSettings

private data class FloatingEntry(val text: String)

internal object FirebaseFloatingWindowState {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val _entries = MutableStateFlow<List<FloatingEntry>>(emptyList())
    private val _visible = MutableStateFlow(false)

    val visible: StateFlow<Boolean> = _visible.asStateFlow()
    val lines: StateFlow<List<String>> = _entries
        .map { entries -> entries.map { it.text } }
        .stateIn(scope, SharingStarted.Eagerly, emptyList())

    internal fun initialize() {
        val enabled = FirebaseFloatingWindowSettings.isEnabled()
        _visible.value = enabled
        FirebaseFloatingWindowHost.setVisible(enabled)
    }

    fun setVisible(enabled: Boolean) {
        _visible.value = enabled
        FirebaseFloatingWindowHost.setVisible(enabled)
        FirebaseFloatingWindowSettings.setEnabled(enabled)
        if (!enabled) {
            _entries.value = emptyList()
        }
    }

    fun append(text: String) {
        if (!_visible.value) return
        val entry = FloatingEntry(text = text)
        _entries.update { entries ->
            (entries + entry).takeLast(3)
        }
        scope.launch {
            delay(3.seconds)
            _entries.update { entries -> entries.filterNot { it === entry } }
        }
    }

    fun clear() {
        _entries.value = emptyList()
    }

}

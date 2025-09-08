package ru.bartwell.kick.module.layout.core.introspector

import ru.bartwell.kick.module.layout.core.common.NodeRegistry
import ru.bartwell.kick.module.layout.core.data.LayoutNodeId
import ru.bartwell.kick.module.layout.core.data.LayoutNodeSnapshot
import ru.bartwell.kick.module.layout.core.data.LayoutProperty
import ru.bartwell.kick.module.layout.core.semantics.SemanticsPropertyCollector
import java.awt.KeyboardFocusManager
import java.awt.Window

private class DesktopLayoutIntrospector(
    private val registry: NodeRegistry = NodeRegistry(),
    private val swingBuilder: SwingSnapshotBuilder = SwingSnapshotBuilder(registry),
    private val semanticsBuilder: SemanticsTreeBuilder = SemanticsTreeBuilder(registry),
) : LayoutIntrospector {

    override suspend fun captureHierarchy(): LayoutNodeSnapshot? = runCatching {
        val window = activeWindow() ?: return null
        registry.clear()
        semanticsBuilder.clear()
        swingBuilder.build(window) { host ->
            semanticsBuilder.buildFor(host)
        }
    }.getOrNull()

    override suspend fun propertiesOf(id: LayoutNodeId): List<LayoutProperty> =
        registry.componentOf(id)?.let { registry.collectSwingProps(it) }
            ?: registry.semanticsOf(id)?.let { SemanticsPropertyCollector.collect(it) }
            ?: emptyList()
}

private fun activeWindow(): Window? {
    val manager = KeyboardFocusManager.getCurrentKeyboardFocusManager()
    return manager.activeWindow ?: Window.getWindows().firstOrNull { it.isVisible }
}

public actual fun provideLayoutIntrospector(): LayoutIntrospector =
    DesktopLayoutIntrospector()

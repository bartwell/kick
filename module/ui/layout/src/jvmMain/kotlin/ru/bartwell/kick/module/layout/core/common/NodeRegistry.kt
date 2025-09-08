package ru.bartwell.kick.module.layout.core.common

import androidx.compose.ui.semantics.SemanticsNode
import ru.bartwell.kick.module.layout.core.data.LayoutNodeId
import java.awt.Component

internal class NodeRegistry {
    private val components = mutableMapOf<String, Component>()
    private val semantics = mutableMapOf<String, SemanticsNode>()

    fun clear() {
        components.clear()
        semantics.clear()
    }

    fun register(c: Component): LayoutNodeId {
        val id = LayoutNodeId(System.identityHashCode(c).toString())
        components[id.raw] = c
        return id
    }

    fun register(n: SemanticsNode): LayoutNodeId {
        val id = LayoutNodeId("s${n.id}")
        semantics[id.raw] = n
        return id
    }

    fun componentOf(id: LayoutNodeId): Component? = components[id.raw]
    fun semanticsOf(id: LayoutNodeId): SemanticsNode? = semantics[id.raw]
}

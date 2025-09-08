package ru.bartwell.kick.module.layout.core.introspector

import androidx.compose.ui.node.RootForTest
import androidx.compose.ui.semantics.SemanticsNode
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import ru.bartwell.kick.module.layout.core.common.NodeRegistry
import ru.bartwell.kick.module.layout.core.common.safeLocationOnScreen
import ru.bartwell.kick.module.layout.core.data.LayoutNodeSnapshot
import ru.bartwell.kick.module.layout.core.data.LayoutRect
import ru.bartwell.kick.module.layout.core.semantics.SemanticsPropertyCollector
import java.awt.Component
import java.awt.Container
import java.awt.Point
import java.lang.reflect.Proxy

private const val COMPOSE_NODE_TYPE = "Compose"
private const val FALLBACK_DISPLAY_LABEL = "Compose Component"

internal class SemanticsTreeBuilder(
    private val registry: NodeRegistry
) {
    private val processedRoots = mutableSetOf<Int>()

    fun clear() { processedRoots.clear() }

    fun buildFor(host: Component): List<LayoutNodeSnapshot> = runCatching {
        val root = findRootForTest(host) ?: return emptyList()
        val rootId = System.identityHashCode(root)
        if (!processedRoots.add(rootId)) return emptyList()

        val hostOffset = host.safeLocationOnScreen()
        root.semanticsOwner.rootSemanticsNode.children.mapNotNull { buildSemNode(it, hostOffset) }
    }.getOrElse { emptyList() }

    private fun buildSemNode(n0: SemanticsNode, hostOffset: Point): LayoutNodeSnapshot? {
        val n = skipWrapper(n0)
        val role = n.config.getOrNull(SemanticsProperties.Role)?.toString()
        val text = SemanticsPropertyCollector.extractTextLabel(n)
        val display = when {
            role != null && !text.isNullOrBlank() -> "$role ($text)"
            role != null -> role
            !text.isNullOrBlank() -> text
            else -> FALLBACK_DISPLAY_LABEL
        }

        val children = n.children.mapNotNull { buildSemNode(it, hostOffset) }
        if (display == FALLBACK_DISPLAY_LABEL && children.isEmpty()) return null

        val r = n.boundsInRoot
        val rect = LayoutRect(
            (r.left + hostOffset.x).toInt(),
            (r.top + hostOffset.y).toInt(),
            r.width.toInt(),
            r.height.toInt()
        )

        val id = registry.register(n)
        return LayoutNodeSnapshot(
            id = id,
            typeName =  COMPOSE_NODE_TYPE,
            displayName = display,
            bounds = rect,
            isVisible = null,
            testTag = n.config.getOrNull(SemanticsProperties.TestTag),
            children = children
        )
    }

    private tailrec fun skipWrapper(node: SemanticsNode): SemanticsNode {
        val hasLabel = SemanticsPropertyCollector.hasAnyLabel(node)
        return if (!hasLabel && node.children.size == 1) skipWrapper(node.children.first()) else node
    }

    private fun findRootForTest(obj: Any?): RootForTest? {
        val target = obj ?: return null
        val candidate = target as? RootForTest
            ?: attachRootCandidate(target)
            ?: getFieldValue(target, "rootForTest")
            ?: invokeMethod(target, "getRootForTest")
            ?: searchFields(target, listOf("scene", "currentScene"))
            ?: searchFields(target, listOf("layer", "_layer", "skiaLayer", "composeLayer"))
            ?: (target as? Container)?.let { containerSearch(it) }
        return when (candidate) {
            is RootForTest -> candidate
            null -> null
            else -> findRootForTest(candidate)
        }
    }

    private fun attachRootCandidate(obj: Any): Any? {
        var result: Any? = null
        return if (tryAttachRootForTest(obj) { result = it }) result else null
    }

    private fun getFieldValue(obj: Any, name: String): Any? =
        runCatching { obj.javaClass.getDeclaredField(name).apply { isAccessible = true }.get(obj) }.getOrNull()

    private fun invokeMethod(obj: Any, name: String): Any? =
        runCatching { obj.javaClass.getDeclaredMethod(name).apply { isAccessible = true }.invoke(obj) }.getOrNull()

    private fun searchFields(obj: Any, names: List<String>): Any? =
        names.asSequence().mapNotNull { getFieldValue(obj, it) }.firstOrNull()

    private fun containerSearch(container: Container): Any? =
        container.components.asSequence().mapNotNull { findRootForTest(it) }.firstOrNull()

    private fun tryAttachRootForTest(host: Any, onRoot: (Any) -> Unit): Boolean {
        val m = host.javaClass.declaredMethods.firstOrNull {
            it.name.contains("setRootForTestListener") && it.parameterCount == 1
        } ?: return false
        val listenerType = m.parameterTypes[0]
        val proxy = Proxy.newProxyInstance(
            listenerType.classLoader,
            arrayOf(listenerType)
        ) { _, _, args ->
            if (args?.isNotEmpty() == true && args[0]?.javaClass?.name?.contains("RootForTest") == true) {
                onRoot(args[0])
            }
            null
        }
        return runCatching {
            m.isAccessible = true
            m.invoke(host, proxy)
            true
        }.getOrElse { false }
    }
}

package ru.bartwell.kick.module.layout.core.introspector

import android.content.res.Resources
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ViewRootForTest
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsNode
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import ru.bartwell.kick.module.layout.core.data.LayoutNodeId
import ru.bartwell.kick.module.layout.core.data.LayoutNodeSnapshot
import ru.bartwell.kick.module.layout.core.data.LayoutProperty
import ru.bartwell.kick.module.layout.core.data.LayoutRect

private class AndroidLayoutIntrospector : LayoutIntrospector {
    private val views = mutableMapOf<String, View>()
    private val semantics = mutableMapOf<String, SemanticsNode>()

    override suspend fun captureHierarchy(): LayoutNodeSnapshot? {
        return runCatching {
            val root = decorView() ?: return null
            views.clear()
            semantics.clear()
            buildSnapshot(root)
        }.getOrNull()
    }

    override suspend fun propertiesOf(id: LayoutNodeId): List<LayoutProperty> {
        views[id.raw]?.let { view ->
            val properties = mutableListOf<LayoutProperty>()
            properties += LayoutProperty("class", view.javaClass.name)
            view.id.takeIf { it != View.NO_ID }?.let {
                properties += LayoutProperty("id", safeResourceName(view.resources, it))
            }
            val location = IntArray(2)
            view.getLocationOnScreen(location)
            properties += LayoutProperty("bounds", "${location[0]},${location[1]},${view.width},${view.height}")
            properties += LayoutProperty(
                "visibility",
                when (view.visibility) {
                    View.VISIBLE -> "VISIBLE"
                    View.INVISIBLE -> "INVISIBLE"
                    else -> "GONE"
                }
            )
            properties += LayoutProperty("enabled", view.isEnabled.toString())
            properties += LayoutProperty("clickable", view.isClickable.toString())
            view.contentDescription?.let { properties += LayoutProperty("contentDescription", it.toString()) }
            view.tag?.let { properties += LayoutProperty("tag", it.toString()) }
            properties += LayoutProperty("alpha", view.alpha.toString())
            properties += LayoutProperty("elevation", view.elevation.toString())
            return properties
        }

        semantics[id.raw]?.let { node ->
            val props = mutableListOf<LayoutProperty>()
            node.config.getOrNull(SemanticsProperties.TestTag)?.let {
                props += LayoutProperty("testTag", it)
            }
            node.config.getOrNull(SemanticsProperties.ContentDescription)?.let {
                props += LayoutProperty("contentDescription", it.joinToString())
            }
            node.config.getOrNull(SemanticsProperties.Text)?.let { list ->
                props += LayoutProperty("text", list.joinToString { it.text })
            }
            val clickable = node.config.contains(SemanticsActions.OnClick)
            props += LayoutProperty("clickable", clickable.toString())

            return props
        }

        return emptyList()
    }

    private fun buildSnapshot(view: View): LayoutNodeSnapshot {
        val id = LayoutNodeId(System.identityHashCode(view).toString())
        views[id.raw] = view

        val typeName = view.javaClass.name
        val resName = view.id.takeIf { it != View.NO_ID }?.let { safeResourceName(view.resources, it) }
        val displayName = resName?.let { "$typeName($it)" } ?: typeName

        val location = IntArray(2)
        view.getLocationOnScreen(location)
        val rect = LayoutRect(location[0], location[1], view.width, view.height)

        val viewChildren = (view as? ViewGroup)?.let { group ->
            (0 until group.childCount).map { buildSnapshot(group.getChildAt(it)) }
        } ?: emptyList()

        val semanticChildren = semanticsSnapshots(view)

        return LayoutNodeSnapshot(
            id = id,
            typeName = typeName,
            displayName = displayName,
            bounds = rect,
            isVisible = (view.visibility == View.VISIBLE),
            testTag = view.tag?.toString(),
            children = viewChildren + semanticChildren
        )
    }

    private fun semanticsSnapshots(view: View): List<LayoutNodeSnapshot> {
        if (view is ViewRootForTest) {
            return try {
                val owner = view.semanticsOwner
                val rootNode = runCatching { owner.unmergedRootSemanticsNode }
                    .getOrElse { owner.rootSemanticsNode }
                listOf(buildSemanticsSnapshot(rootNode))
            } catch (_: Throwable) {
                emptyList()
            }
        }
        return emptyList()
    }

    private fun buildSemanticsSnapshot(node: SemanticsNode): LayoutNodeSnapshot {
        val id = LayoutNodeId("s${node.id}")
        semantics[id.raw] = node

        val testTag = node.config.getOrNull(SemanticsProperties.TestTag)
        val contentDesc = node.config.getOrNull(SemanticsProperties.ContentDescription)?.joinToString()
        val text = node.config.getOrNull(SemanticsProperties.Text)?.joinToString { it.text }
        val display = testTag ?: contentDesc ?: text ?: "ComposeNode"

        val r = node.boundsInRoot
        val layoutRect = LayoutRect(
            r.left.toInt(),
            r.top.toInt(),
            r.width.toInt(),
            r.height.toInt()
        )

        val children = node.children.map { childNode ->
            buildSemanticsSnapshot(childNode)
        }

        return LayoutNodeSnapshot(
            id = id,
            typeName = "Semantics",
            displayName = display,
            bounds = layoutRect,
            isVisible = null,
            testTag = testTag,
            children = children
        )
    }

    private fun decorView(): View? {
        return try {
            val wmClass = Class.forName("android.view.WindowManagerGlobal")
            val instance = wmClass.getMethod("getInstance").invoke(null)
            val viewsField = wmClass.getDeclaredField("mViews").apply { isAccessible = true }
            val raw = viewsField.get(instance)
            val list: List<View> = when (raw) {
                is List<*> -> raw.filterIsInstance<View>()
                is Array<*> -> raw.filterIsInstance<View>()
                else -> emptyList()
            }
            if (list.isEmpty()) {
                null
            } else {
                val idx = if (list.size > 1) list.lastIndex - 1 else list.lastIndex
                list.getOrNull(idx)
            }
        } catch (_: Throwable) {
            null
        }
    }

    private fun safeResourceName(res: Resources, id: Int): String {
        return runCatching { res.getResourceEntryName(id) }.getOrNull() ?: id.toString()
    }
}

public actual fun provideLayoutIntrospector(): LayoutIntrospector = AndroidLayoutIntrospector()

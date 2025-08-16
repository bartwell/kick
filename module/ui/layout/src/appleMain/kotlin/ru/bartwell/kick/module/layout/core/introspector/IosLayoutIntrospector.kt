package ru.bartwell.kick.module.layout.core.introspector

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import platform.CoreGraphics.*
import platform.Foundation.NSStringFromClass
import platform.UIKit.*
import platform.objc.object_getClass
import ru.bartwell.kick.module.layout.core.data.LayoutNodeId
import ru.bartwell.kick.module.layout.core.data.LayoutNodeSnapshot
import ru.bartwell.kick.module.layout.core.data.LayoutProperty
import ru.bartwell.kick.module.layout.core.data.LayoutRect

// Compose semantics are not currently available on iOS, so only UIKit views are captured.
@OptIn(ExperimentalForeignApi::class)
private class IosLayoutIntrospector : LayoutIntrospector {
    private val views = mutableMapOf<String, UIView>()

    override suspend fun captureHierarchy(): LayoutNodeSnapshot? {
        val window = UIApplication.sharedApplication.keyWindow
            ?: UIApplication.sharedApplication.windows.lastOrNull() as? UIWindow
            ?: return null
        val root = window.rootViewController?.view ?: window.subviews.firstOrNull() as? UIView ?: return null
        views.clear()
        return buildSnapshot(root)
    }

    override suspend fun propertiesOf(id: LayoutNodeId): List<LayoutProperty> {
        val view = views[id.raw] ?: return emptyList()
        val props = mutableListOf<LayoutProperty>()
        props += LayoutProperty("class", NSStringFromClass(object_getClass(view)!!))
        val rect = view.frame
        rect.useContents {
            props += LayoutProperty("bounds", "${origin.x.toInt()},${origin.y.toInt()},${size.width.toInt()},${size.height.toInt()}")
        }
        props += LayoutProperty("isHidden", view.hidden.toString())
        props += LayoutProperty("isUserInteractionEnabled", view.userInteractionEnabled.toString())
        view.accessibilityLabel?.let { label -> props += LayoutProperty("accessibilityLabel", label) }
        props += LayoutProperty("tag", view.tag.toString())
        return props
    }

    private fun buildSnapshot(view: UIView): LayoutNodeSnapshot {
        val id = LayoutNodeId(view.hashCode().toString())
        views[id.raw] = view
        val typeName = NSStringFromClass(object_getClass(view)!!)
        val label = view.accessibilityLabel ?: ""
        val display = if (label.isNotBlank()) "$typeName:$label" else typeName
        val rect = view.frame
        val layoutRect = rect.useContents {
            LayoutRect(origin.x.toInt(), origin.y.toInt(), size.width.toInt(), size.height.toInt())
        }
        val children = view.subviews.filterIsInstance<UIView>().map { buildSnapshot(it) }
        return LayoutNodeSnapshot(
            id = id,
            typeName = typeName,
            displayName = display,
            bounds = layoutRect,
            isVisible = !view.hidden,
            testTag = null,
            children = children
        )
    }
}

public actual fun provideLayoutIntrospector(): LayoutIntrospector = IosLayoutIntrospector()

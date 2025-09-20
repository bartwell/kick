package ru.bartwell.kick.module.layout.core.introspector

import kotlinx.cinterop.COpaquePointer
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.StableRef
import kotlinx.cinterop.useContents
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import platform.CoreGraphics.*
import platform.Foundation.NSSet
import platform.Foundation.NSStringFromClass
import platform.Foundation.NSUUID
import platform.UIKit.*
import platform.objc.*
import ru.bartwell.kick.module.layout.core.data.*
import kotlin.experimental.ExperimentalNativeApi
import kotlin.native.ref.WeakReference

private const val MIN_ALPHA = 0.01

@OptIn(ExperimentalForeignApi::class)
private class IosLayoutIntrospector : LayoutIntrospector {

    @OptIn(ExperimentalNativeApi::class)
    private val views = mutableMapOf<String, WeakReference<UIView>>()

    @OptIn(ExperimentalNativeApi::class)
    override suspend fun captureHierarchy(): LayoutNodeSnapshot? = onMain {
        val window = activeWindows().firstOrNull() ?: return@onMain null
        val rootView = window.rootViewController?.view
            ?: window.subviews.firstOrNull() as? UIView
            ?: return@onMain null

        views.clear()
        buildSnapshot(view = rootView, rootWindow = window)
    }

    @OptIn(ExperimentalNativeApi::class)
    override suspend fun propertiesOf(id: LayoutNodeId): List<LayoutProperty> = onMain {
        val view = views[id.raw]?.get() ?: return@onMain emptyList()
        val props = mutableListOf<LayoutProperty>()

        val typeName = NSStringFromClass(object_getClass(view)!!)
        props += LayoutProperty("class", typeName)

        view.frame.useContents {
            props += LayoutProperty(
                "frame",
                "${origin.x},${origin.y},${size.width},${size.height}"
            )
        }
        view.bounds.useContents {
            props += LayoutProperty(
                "bounds",
                "0,0,${size.width},${size.height}"
            )
        }

        props += LayoutProperty("alpha", view.alpha.toString())
        props += LayoutProperty("isHidden", view.hidden.toString())
        props += LayoutProperty("isUserInteractionEnabled", view.userInteractionEnabled.toString())
        props += LayoutProperty("clipsToBounds", view.clipsToBounds.toString())
        props += LayoutProperty("contentMode", view.contentMode.toString())
        props += LayoutProperty("zPosition", view.layer.zPosition.toString())

        (view as? UIAccessibilityIdentificationProtocol)?.accessibilityIdentifier?.let {
            props += LayoutProperty("accessibilityIdentifier", it)
        }
        view.accessibilityLabel?.let { props += LayoutProperty("accessibilityLabel", it) }
        props += LayoutProperty("tag", view.tag.toString())

        props
    }

    private suspend inline fun <T> onMain(crossinline block: () -> T): T {
        return withContext(Dispatchers.Main.immediate) { block() }
    }

    private fun activeWindows(): List<UIWindow> {
        val app = UIApplication.sharedApplication
        val result = mutableListOf<UIWindow>()

        val scenesSet = app.connectedScenes
        if (scenesSet is NSSet) {
            val enumScenes = scenesSet.objectEnumerator()
            @Suppress("LoopWithTooManyJumpStatements")
            while (true) {
                val obj = enumScenes.nextObject() ?: break
                val scene = obj as? UIWindowScene ?: continue
                if (scene.activationState == UISceneActivationStateForegroundActive) {
                    result += (scene.windows as List<*>).filterIsInstance<UIWindow>()
                }
            }
        }

        result += (app.windows as List<*>).filterIsInstance<UIWindow>()

        return result
            .distinct()
            .filter { !it.hidden && it.alpha > MIN_ALPHA }
    }

    private fun getOrAttachId(view: UIView): LayoutNodeId {
        (objc_getAssociatedObject(view, NODE_ID_ASSOC_KEY) as? String)?.let {
            return LayoutNodeId(it)
        }
        val newId = NSUUID().UUIDString
        objc_setAssociatedObject(
            view,
            NODE_ID_ASSOC_KEY,
            newId,
            OBJC_ASSOCIATION_RETAIN_NONATOMIC
        )
        return LayoutNodeId(newId)
    }

    private fun effectiveVisible(view: UIView): Boolean {
        if (view.hidden || view.alpha <= MIN_ALPHA) return false
        var v: UIView? = view.superview
        while (v != null) {
            if (v!!.hidden || v!!.alpha <= MIN_ALPHA) return false
            v = v!!.superview
        }
        return view.window != null
    }

    @OptIn(ExperimentalNativeApi::class)
    private fun buildSnapshot(view: UIView, rootWindow: UIWindow): LayoutNodeSnapshot {
        val typeName = NSStringFromClass(object_getClass(view)!!)
        val id = getOrAttachId(view)
        views[id.raw] = WeakReference(view)

        val label = extractText(view)
        val display = if (!label.isNullOrBlank()) "$typeName ($label)" else typeName

        val absRect = view.convertRect(view.bounds, toView = rootWindow)
        val layoutRect = absRect.useContents {
            LayoutRect(
                x = origin.x.toInt(),
                y = origin.y.toInt(),
                width = size.width.toInt(),
                height = size.height.toInt()
            )
        }

        val children = (view.subviews as List<*>)
            .filterIsInstance<UIView>()
            .map { buildSnapshot(it, rootWindow) }

        return LayoutNodeSnapshot(
            id = id,
            typeName = typeName,
            displayName = display,
            bounds = layoutRect,
            isVisible = effectiveVisible(view),
            testTag = (view as? UIAccessibilityIdentificationProtocol)?.accessibilityIdentifier,
            children = children
        )
    }

    private fun extractText(view: UIView): String? {
        val text = when (view) {
            is UILabel -> view.text
            is UIButton -> view.currentTitle ?: view.titleLabel?.text
            is UITextField -> view.text
            is UITextView -> view.text
            else -> null
        } ?: view.accessibilityLabel
        return text?.trim()?.takeIf { it.isNotEmpty() }
    }

    private companion object {
        private val NODE_ID_ASSOC_KEY: COpaquePointer = StableRef.create("ru.bartwell.kick.nodeId").asCPointer()
    }
}

public actual fun provideLayoutIntrospector(): LayoutIntrospector = IosLayoutIntrospector()

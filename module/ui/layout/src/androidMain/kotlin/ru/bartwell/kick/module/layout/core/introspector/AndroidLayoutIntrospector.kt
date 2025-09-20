package ru.bartwell.kick.module.layout.core.introspector

import android.content.res.Resources
import android.os.Build
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import android.widget.CompoundButton
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.RatingBar
import android.widget.SeekBar
import android.widget.TextView
import androidx.compose.ui.platform.ViewRootForTest
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsConfiguration
import androidx.compose.ui.semantics.SemanticsNode
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.SemanticsPropertyKey
import androidx.compose.ui.semantics.getOrNull
import androidx.core.view.isVisible
import ru.bartwell.kick.module.layout.core.data.LayoutNodeId
import ru.bartwell.kick.module.layout.core.data.LayoutNodeSnapshot
import ru.bartwell.kick.module.layout.core.data.LayoutProperty
import ru.bartwell.kick.module.layout.core.data.LayoutRect

private const val COMPOSE_LABEL = "Compose"

private class AndroidLayoutIntrospector : LayoutIntrospector {

    private val views = mutableMapOf<String, View>()
    private val semantics = mutableMapOf<String, SemanticsNode>()
    private val processedSemanticsOwners = mutableSetOf<Int>()

    override suspend fun captureHierarchy(): LayoutNodeSnapshot? {
        return runCatching {
            val root = decorView() ?: return null
            views.clear()
            semantics.clear()
            processedSemanticsOwners.clear()
            buildSnapshot(root)
        }.getOrNull()
    }

    override suspend fun propertiesOf(id: LayoutNodeId): List<LayoutProperty> {
        return views[id.raw]?.let { view -> viewProperties(view) }
            ?: semantics[id.raw]?.let { node -> semanticsProperties(node) }
            ?: emptyList()
    }

    private fun viewProperties(view: View): List<LayoutProperty> {
        return mutableListOf<LayoutProperty>().apply {
            addClassInfo(view)
            addViewId(view)
            addBounds(view)
            addVisibilityProps(view)
            addStateProps(view)
            addTransformProps(view)
            addPadding(view)
            addAccessibilityProps(view)
            addLayoutParamsProps(view)
            addBackgroundAndForeground(view)
            addScrollAbility(view)
            addContentAndTag(view)
            addTypeSpecificProps(view)
        }
    }

    private fun MutableList<LayoutProperty>.addClassInfo(view: View) {
        add(LayoutProperty(name = "classFqn", value = view.javaClass.name))
        add(
            LayoutProperty(
                name = "class",
                value = view.javaClass.simpleName.ifBlank { view.javaClass.name.substringAfterLast('.') }
            )
        )
    }

    private fun MutableList<LayoutProperty>.addViewId(view: View) {
        view.id.takeIf { it != View.NO_ID }?.let {
            add(LayoutProperty(name = "id", value = safeResourceName(view.resources, it)))
        }
    }

    private fun MutableList<LayoutProperty>.addBounds(view: View) {
        val loc = IntArray(2).also { view.getLocationOnScreen(it) }
        add(
            LayoutProperty(
                name = "bounds",
                value = "${loc[0]},${loc[1]},${view.width},${view.height}"
            )
        )
    }

    private fun MutableList<LayoutProperty>.addVisibilityProps(view: View) {
        add(
            LayoutProperty(
                "visibility",
                when (view.visibility) {
                    View.VISIBLE -> "VISIBLE"
                    View.INVISIBLE -> "INVISIBLE"
                    else -> "GONE"
                }
            )
        )
    }

    private fun MutableList<LayoutProperty>.addStateProps(view: View) {
        add(LayoutProperty("enabled", view.isEnabled.toString()))
        add(LayoutProperty("focusable", view.isFocusable.toString()))
        add(LayoutProperty("focused", view.isFocused.toString()))
        add(LayoutProperty("selected", view.isSelected.toString()))
        add(LayoutProperty("pressed", view.isPressed.toString()))
        add(LayoutProperty("clickable", view.isClickable.toString()))
        add(LayoutProperty("longClickable", view.isLongClickable.toString()))
    }

    private fun MutableList<LayoutProperty>.addTransformProps(view: View) {
        add(LayoutProperty("alpha", view.alpha.toString()))
        add(LayoutProperty("elevation", view.elevation.toString()))
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            add(LayoutProperty("translationZ", view.translationZ.toString()))
        }
        add(LayoutProperty("translationX", view.translationX.toString()))
        add(LayoutProperty("translationY", view.translationY.toString()))
        add(LayoutProperty("rotation", view.rotation.toString()))
        add(LayoutProperty("rotationX", view.rotationX.toString()))
        add(LayoutProperty("rotationY", view.rotationY.toString()))
        add(LayoutProperty("scaleX", view.scaleX.toString()))
        add(LayoutProperty("scaleY", view.scaleY.toString()))
        add(LayoutProperty("pivotX", view.pivotX.toString()))
        add(LayoutProperty("pivotY", view.pivotY.toString()))
    }

    private fun MutableList<LayoutProperty>.addPadding(view: View) {
        add(
            LayoutProperty(
                name = "padding",
                value = "${view.paddingLeft},${view.paddingTop},${view.paddingRight},${view.paddingBottom}"
            )
        )
    }

    private fun MutableList<LayoutProperty>.addAccessibilityProps(view: View) {
        addQAccessibilityProps(view)
        addPAccessibilityProps(view)
        addImportantForAccessibility(view)
        addLabelForProp(view)
        addLiveRegion(view)
    }

    private fun MutableList<LayoutProperty>.addQAccessibilityProps(view: View) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            add(LayoutProperty("isAccessibilityHeading", view.isAccessibilityHeading.toString()))
        }
    }

    private fun MutableList<LayoutProperty>.addPAccessibilityProps(view: View) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            add(
                LayoutProperty(
                    name = "accessibilityPaneTitle",
                    value = view.accessibilityPaneTitle?.toString() ?: "null"
                )
            )
            add(
                LayoutProperty(
                    name = "isScreenReaderFocusable",
                    value = view.isScreenReaderFocusable.toString()
                )
            )
        }
    }

    private fun MutableList<LayoutProperty>.addImportantForAccessibility(view: View) {
        add(
            LayoutProperty(
                name = "importantForAccessibility",
                value = when (view.importantForAccessibility) {
                    View.IMPORTANT_FOR_ACCESSIBILITY_AUTO -> "AUTO"
                    View.IMPORTANT_FOR_ACCESSIBILITY_YES -> "YES"
                    View.IMPORTANT_FOR_ACCESSIBILITY_NO -> "NO"
                    View.IMPORTANT_FOR_ACCESSIBILITY_NO_HIDE_DESCENDANTS -> "NO_HIDE_DESCENDANTS"
                    else -> view.importantForAccessibility.toString()
                }
            )
        )
    }

    private fun MutableList<LayoutProperty>.addLabelForProp(view: View) {
        add(
            LayoutProperty(
                name = "labelFor",
                value = view.labelFor.takeIf { it != View.NO_ID }
                    ?.let { safeResourceName(view.resources, it) }
                    ?: "none"
            )
        )
    }

    private fun MutableList<LayoutProperty>.addLiveRegion(view: View) {
        add(
            LayoutProperty(
                name = "accessibilityLiveRegion",
                value = when (view.accessibilityLiveRegion) {
                    View.ACCESSIBILITY_LIVE_REGION_NONE -> "NONE"
                    View.ACCESSIBILITY_LIVE_REGION_POLITE -> "POLITE"
                    View.ACCESSIBILITY_LIVE_REGION_ASSERTIVE -> "ASSERTIVE"
                    else -> view.accessibilityLiveRegion.toString()
                }
            )
        )
    }

    private fun MutableList<LayoutProperty>.addLayoutParamsProps(view: View) {
        view.layoutParams?.let { lp ->
            add(LayoutProperty("layout.width", sizeSpecToString(lp.width)))
            add(LayoutProperty("layout.height", sizeSpecToString(lp.height)))
            if (lp is MarginLayoutParams) {
                add(
                    LayoutProperty(
                        name = "layout.margins",
                        value = "${lp.leftMargin},${lp.topMargin},${lp.rightMargin},${lp.bottomMargin}"
                    )
                )
            }
        }
    }

    private fun MutableList<LayoutProperty>.addBackgroundAndForeground(view: View) {
        view.background?.let { add(LayoutProperty("background", it.javaClass.simpleName)) }
        view.foreground?.let { add(LayoutProperty("foreground", it.javaClass.simpleName)) }
    }

    private fun MutableList<LayoutProperty>.addScrollAbility(view: View) {
        add(
            LayoutProperty(
                name = "canScrollHorizontally",
                value = view.canScrollHorizontally(1).or(view.canScrollHorizontally(-1)).toString()
            )
        )
        add(
            LayoutProperty(
                name = "canScrollVertically",
                value = view.canScrollVertically(1).or(view.canScrollVertically(-1)).toString()
            )
        )
    }

    private fun MutableList<LayoutProperty>.addContentAndTag(view: View) {
        view.contentDescription?.let { add(LayoutProperty(name = "contentDescription", value = it.toString())) }
        view.tag?.let { add(LayoutProperty(name = "tag", value = it.toString())) }
    }

    private fun MutableList<LayoutProperty>.addTypeSpecificProps(view: View) {
        when (view) {
            is TextView -> addTextViewProps(view)
            is CompoundButton -> add(LayoutProperty("checked", view.isChecked.toString()))
            is ProgressBar -> addProgressBarProps(view)
            is ImageView -> add(LayoutProperty("scaleType", view.scaleType?.name ?: "null"))
            is RatingBar -> addRatingBarProps(view)
            is SeekBar -> addSeekBarProps(view)
        }
    }

    private fun MutableList<LayoutProperty>.addTextViewProps(view: TextView) {
        add(LayoutProperty(name = "text", value = view.text?.toString() ?: ""))
        view.hint?.let { add(LayoutProperty("hint", it.toString())) }
        add(LayoutProperty(name = "text.length", value = view.text?.length?.toString() ?: "0"))
        add(LayoutProperty(name = "ellipsize", value = view.ellipsize?.name ?: "none"))
        add(LayoutProperty(name = "lines", value = view.lineCount.toString()))
        add(
            LayoutProperty(
                name = "maxLines",
                value = if (view.maxLines == Integer.MAX_VALUE) "unlimited" else view.maxLines.toString()
            )
        )
        add(LayoutProperty(name = "minLines", value = view.minLines.toString()))
        add(LayoutProperty(name = "inputType", value = view.inputType.toString()))
    }

    private fun MutableList<LayoutProperty>.addProgressBarProps(view: ProgressBar) {
        add(LayoutProperty(name = "progress", value = view.progress.toString()))
        add(
            LayoutProperty(
                name = "min",
                value = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) view.min.toString() else "0"
            )
        )
        add(LayoutProperty(name = "max", value = view.max.toString()))
    }

    private fun MutableList<LayoutProperty>.addRatingBarProps(view: RatingBar) {
        add(LayoutProperty("rating", view.rating.toString()))
        add(LayoutProperty("numStars", view.numStars.toString()))
        add(LayoutProperty("stepSize", view.stepSize.toString()))
    }

    private fun MutableList<LayoutProperty>.addSeekBarProps(view: SeekBar) {
        add(LayoutProperty("progress", view.progress.toString()))
        add(LayoutProperty("max", view.max.toString()))
    }

    private fun sizeSpecToString(v: Int): String = when (v) {
        ViewGroup.LayoutParams.MATCH_PARENT -> "match_parent"
        ViewGroup.LayoutParams.WRAP_CONTENT -> "wrap_content"
        else -> v.toString()
    }

    private fun semanticsProperties(node: SemanticsNode): List<LayoutProperty> {
        val cfg = node.config
        val props = mutableListOf<LayoutProperty>()

        addCoreSemantics(cfg, props)
        addLiveAndTraversalSemantics(cfg, props)
        addCollectionAndScrollSemantics(cfg, props)
        addFlagsAndSubstitution(cfg, props)
        addDialogPopupAndIndex(cfg, props)
        addBounds(node, props)
        addActions(cfg, props)

        return props
    }

    private fun addCoreSemantics(cfg: SemanticsConfiguration, props: MutableList<LayoutProperty>) {
        addSemanticsLabels(cfg, props)
        addSemanticsRoleAndState(cfg, props)
        addSemanticsHeadingAndPane(cfg, props)
        addSemanticsFocusEditAndError(cfg, props)
    }

    private fun addSemanticsLabels(cfg: SemanticsConfiguration, props: MutableList<LayoutProperty>) {
        cfg.getOrNull(SemanticsProperties.TestTag)?.let { props += LayoutProperty("testTag", it) }
        cfg.getOrNull(SemanticsProperties.ContentDescription)?.let {
            props += LayoutProperty("contentDescription", it.joinToString())
        }
        cfg.getOrNull(SemanticsProperties.Text)?.let { list ->
            props += LayoutProperty("text", list.joinToString { it.text })
        }
    }

    private fun addSemanticsRoleAndState(cfg: SemanticsConfiguration, props: MutableList<LayoutProperty>) {
        cfg.getOrNull(SemanticsProperties.Role)?.let { props += LayoutProperty("role", it.toString()) }
        cfg.getOrNull(SemanticsProperties.StateDescription)?.let { props += LayoutProperty("stateDescription", it) }
        cfg.getOrNull(SemanticsProperties.Selected)?.let { props += LayoutProperty("selected", it.toString()) }
        cfg.getOrNull(SemanticsProperties.ToggleableState)?.let {
            props += LayoutProperty("toggleableState", it.toString())
        }
        cfg.getOrNull(SemanticsProperties.Password)?.let { props += LayoutProperty("password", it.toString()) }
    }

    private fun addSemanticsHeadingAndPane(cfg: SemanticsConfiguration, props: MutableList<LayoutProperty>) {
        (cfg.getAnyOrNull("Heading") ?: cfg.getAnyOrNull("AccessibilityHeading"))?.let {
            props += LayoutProperty("heading", it.toString())
        }
        cfg.getOrNull(SemanticsProperties.PaneTitle)?.let { props += LayoutProperty("paneTitle", it) }
    }

    private fun addSemanticsFocusEditAndError(cfg: SemanticsConfiguration, props: MutableList<LayoutProperty>) {
        cfg.getOrNull(SemanticsProperties.Focused)?.let { props += LayoutProperty("focused", it.toString()) }
        cfg.getOrNull(SemanticsProperties.IsEditable)?.let { props += LayoutProperty("isEditable", it.toString()) }
        cfg.getOrNull(SemanticsProperties.ImeAction)?.let { props += LayoutProperty("imeAction", it.toString()) }
        cfg.getOrNull(SemanticsProperties.Error)?.let { props += LayoutProperty("error", it) }
    }

    private fun addLiveAndTraversalSemantics(cfg: SemanticsConfiguration, props: MutableList<LayoutProperty>) {
        cfg.getOrNull(SemanticsProperties.LiveRegion)?.let { props += LayoutProperty("liveRegion", it.toString()) }
        cfg.getOrNull(SemanticsProperties.ContentType)?.let { props += LayoutProperty("contentType", it.toString()) }
        cfg.getOrNull(SemanticsProperties.ContentDataType)?.let {
            props += LayoutProperty("contentDataType", it.toString())
        }
        cfg.getOrNull(SemanticsProperties.TraversalIndex)?.let {
            props += LayoutProperty("traversalIndex", it.toString())
        }
    }

    private fun addCollectionAndScrollSemantics(cfg: SemanticsConfiguration, props: MutableList<LayoutProperty>) {
        cfg.getOrNull(SemanticsProperties.CollectionInfo)?.let {
            props += LayoutProperty("collectionInfo", it.toString())
        }
        cfg.getOrNull(SemanticsProperties.CollectionItemInfo)?.let {
            props += LayoutProperty("collectionItemInfo", it.toString())
        }
        cfg.getOrNull(SemanticsProperties.HorizontalScrollAxisRange)?.let {
            props += LayoutProperty("hScrollRange", it.toString())
        }
        cfg.getOrNull(SemanticsProperties.VerticalScrollAxisRange)?.let {
            props += LayoutProperty("vScrollRange", it.toString())
        }
    }

    private fun addFlagsAndSubstitution(cfg: SemanticsConfiguration, props: MutableList<LayoutProperty>) {
        if (cfg.getAnyOrNull("Disabled") != null) props += LayoutProperty("enabled", "false")
        if (cfg.getAnyOrNull("HideFromAccessibility") != null) props += LayoutProperty("hideFromAccessibility", "true")
        cfg.getOrNull(SemanticsProperties.IsTraversalGroup)?.let {
            props += LayoutProperty("isTraversalGroup", it.toString())
        }
        @Suppress("DEPRECATION")
        cfg.getOrNull(SemanticsProperties.IsContainer)?.let {
            props += LayoutProperty("isContainer(deprecated)", it.toString())
        }
        cfg.getOrNull(SemanticsProperties.TextSubstitution)?.let {
            props += LayoutProperty("textSubstitution", it.toString())
        }
        cfg.getOrNull(SemanticsProperties.IsShowingTextSubstitution)?.let {
            props += LayoutProperty("isShowingTextSubstitution", it.toString())
        }
    }

    private fun addDialogPopupAndIndex(cfg: SemanticsConfiguration, props: MutableList<LayoutProperty>) {
        if (cfg.getAnyOrNull("IndexForKey") != null) props += LayoutProperty("indexForKey", "present")
        cfg.getOrNull(SemanticsProperties.MaxTextLength)?.let {
            props += LayoutProperty("maxTextLength", it.toString())
        }
        if (cfg.getAnyOrNull("IsDialog") != null) props += LayoutProperty(name = "isDialog", value = "true")
        if (cfg.getAnyOrNull("IsPopup") != null) props += LayoutProperty(name = "isPopup", value = "true")
    }

    private fun addBounds(node: SemanticsNode, props: MutableList<LayoutProperty>) {
        val r = node.boundsInRoot
        props += LayoutProperty(
            name = "boundsInRoot",
            value = "${r.left.toInt()},${r.top.toInt()},${r.width.toInt()},${r.height.toInt()}"
        )
    }

    private fun addActions(cfg: SemanticsConfiguration, props: MutableList<LayoutProperty>) {
        val actions = mutableListOf<String>()
        fun addAction(name: String, label: String = name) {
            if (cfg.hasActionByName(name)) actions += label
        }
        listOf(
            "OnClick" to "onClick",
            "OnLongClick" to "onLongClick",
            "ScrollBy" to "scrollBy",
            "ScrollByOffset" to "scrollByOffset",
            "ScrollToIndex" to "scrollToIndex",
            "SetText" to "setText",
            "SetSelection" to "setSelection",
            "SetTextSubstitution" to "setTextSubstitution",
            "ShowTextSubstitution" to "showTextSubstitution",
            "ClearTextSubstitution" to "clearTextSubstitution",
            "OnAutofillText" to "onAutofillText",
            "SetProgress" to "setProgress",
            "InsertTextAtCursor" to "insertTextAtCursor",
            "OnImeAction" to "onImeAction",
            "CopyText" to "copyText",
            "CutText" to "cutText",
            "PasteText" to "pasteText",
            "Expand" to "expand",
            "Collapse" to "collapse",
            "Dismiss" to "dismiss",
            "RequestFocus" to "requestFocus",
            "PageUp" to "pageUp",
            "PageDown" to "pageDown",
            "PageLeft" to "pageLeft",
            "PageRight" to "pageRight",
            "GetTextLayoutResult" to "getTextLayoutResult",
            "GetScrollViewportLength" to "getScrollViewportLength",
        ).forEach { (name, label) -> addAction(name, label) }

        if (actions.isNotEmpty()) props += LayoutProperty("actions", actions.joinToString())
        cfg.getOrNull(SemanticsActions.CustomActions)?.let { list ->
            if (list.isNotEmpty()) props += LayoutProperty("customActions", list.joinToString { it.label })
        }
    }

    private fun SemanticsConfiguration.getAnyOrNull(fieldName: String): Any? {
        return runCatching {
            val f = SemanticsProperties::class.java.getDeclaredField(fieldName)

            @Suppress("UNCHECKED_CAST")
            val key = f.get(null) as SemanticsPropertyKey<Any?>
            this.getOrNull(key)
        }.getOrNull()
    }

    private fun SemanticsConfiguration.hasActionByName(fieldName: String): Boolean {
        return runCatching {
            val f = SemanticsActions::class.java.getDeclaredField(fieldName)

            @Suppress("UNCHECKED_CAST")
            val key = f.get(null) as SemanticsPropertyKey<Any?>
            this.getOrNull(key) != null
        }.getOrDefault(false)
    }

    private fun buildSnapshot(view: View): LayoutNodeSnapshot {
        val id = LayoutNodeId(System.identityHashCode(view).toString())
        views[id.raw] = view

        val typeName = view.javaClass.name
        val displayName = view.javaClass.simpleName.ifBlank { typeName.substringAfterLast('.') }

        val location = IntArray(2).also { view.getLocationOnScreen(it) }
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
            isVisible = view.isVisible,
            testTag = view.tag?.toString(),
            children = viewChildren + semanticChildren
        )
    }

    private fun semanticsSnapshots(view: View): List<LayoutNodeSnapshot> {
        if (view is ViewRootForTest) {
            return try {
                val owner = view.semanticsOwner
                val ownerId = System.identityHashCode(owner)
                if (!processedSemanticsOwners.add(ownerId)) return emptyList()

                owner.rootSemanticsNode.children.mapNotNull { buildSemanticsSnapshotOrNull(it) }
            } catch (_: Throwable) {
                emptyList()
            }
        }
        return emptyList()
    }

    private fun buildSemanticsSnapshotOrNull(node: SemanticsNode): LayoutNodeSnapshot? {
        val n = skipWrapperSemantics(node)

        val role = n.config.getOrNull(SemanticsProperties.Role)
        val roleLabel = role?.toString()
        val text = extractTextLabel(n)

        val display = when {
            roleLabel != null && !text.isNullOrBlank() -> "$roleLabel ($text)"
            roleLabel != null -> roleLabel
            !text.isNullOrBlank() -> text
            else -> COMPOSE_LABEL
        }

        val children = n.children.mapNotNull { buildSemanticsSnapshotOrNull(it) }
        if (display == COMPOSE_LABEL && children.isEmpty()) return null

        val r = n.boundsInRoot
        val rect = LayoutRect(r.left.toInt(), r.top.toInt(), r.width.toInt(), r.height.toInt())

        val id = LayoutNodeId("s${n.id}")
        semantics[id.raw] = n

        return LayoutNodeSnapshot(
            id = id,
            typeName = COMPOSE_LABEL,
            displayName = display,
            bounds = rect,
            isVisible = null,
            testTag = n.config.getOrNull(SemanticsProperties.TestTag),
            children = children
        )
    }

    private tailrec fun skipWrapperSemantics(node: SemanticsNode): SemanticsNode {
        val cfg = node.config
        val hasLabel =
            cfg.getOrNull(SemanticsProperties.TestTag) != null ||
                cfg.getOrNull(SemanticsProperties.ContentDescription) != null ||
                cfg.getOrNull(SemanticsProperties.Text) != null ||
                cfg.getOrNull(SemanticsProperties.Role) != null

        return if (!hasLabel && node.children.size == 1) skipWrapperSemantics(node.children.first()) else node
    }

    @Suppress("ReturnCount")
    private fun extractTextLabel(node: SemanticsNode): String? {
        node.config.getOrNull(SemanticsProperties.Text)?.let { list ->
            val s = list.joinToString { it.text }.trim()
            if (s.isNotEmpty()) return s
        }
        node.config.getOrNull(SemanticsProperties.ContentDescription)?.let {
            val s = it.joinToString().trim()
            if (s.isNotEmpty()) return s
        }
        for (child in node.children) {
            val s = extractTextLabel(child)
            if (!s.isNullOrBlank()) return s
        }
        return null
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
                list.lastOrNull()
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

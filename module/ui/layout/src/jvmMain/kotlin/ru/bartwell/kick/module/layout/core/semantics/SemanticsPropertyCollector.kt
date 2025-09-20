package ru.bartwell.kick.module.layout.core.semantics

import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsConfiguration
import androidx.compose.ui.semantics.SemanticsNode
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.SemanticsPropertyKey
import androidx.compose.ui.semantics.getOrNull
import ru.bartwell.kick.module.layout.core.common.PropertyNames
import ru.bartwell.kick.module.layout.core.common.add
import ru.bartwell.kick.module.layout.core.data.LayoutProperty

internal object SemanticsPropertyCollector {
    fun collect(node: SemanticsNode): List<LayoutProperty> = buildList {
        val cfg = node.config
        basics(cfg, this)
        flags(cfg, this)
        coords(node, this)
        actions(cfg, this)
        customActions(cfg, this)
    }

    fun extractTextLabel(node: SemanticsNode): String? {
        val direct = node.config.getOrNull(SemanticsProperties.Text)?.joinToString { it.text }?.trim()
        val content = node.config.getOrNull(SemanticsProperties.ContentDescription)?.joinToString()?.trim()
        val fromChildren = node.children.asSequence().mapNotNull {
            extractTextLabel(
                it
            )
        }.firstOrNull { it.isNotBlank() }
        return direct?.takeIf { it.isNotEmpty() } ?: content?.takeIf { it.isNotEmpty() } ?: fromChildren
    }

    fun hasAnyLabel(node: SemanticsNode): Boolean {
        val cfg = node.config
        return cfg.getOrNull(SemanticsProperties.TestTag) != null ||
            cfg.getOrNull(SemanticsProperties.ContentDescription) != null ||
            cfg.getOrNull(SemanticsProperties.Text) != null ||
            cfg.getOrNull(SemanticsProperties.Role) != null
    }

    private fun basics(cfg: SemanticsConfiguration, props: MutableList<LayoutProperty>) {
        cfg.getOrNull(SemanticsProperties.TestTag)?.let { props.add(PropertyNames.TEST_TAG, it) }
        cfg.getOrNull(SemanticsProperties.ContentDescription)?.let {
            props.add(PropertyNames.CONTENT_DESCRIPTION, it.joinToString())
        }
        cfg.getOrNull(
            SemanticsProperties.Text
        )?.let { list -> props.add(PropertyNames.TEXT, list.joinToString { it.text }) }
        cfg.getOrNull(SemanticsProperties.Role)?.let { props.add(PropertyNames.ROLE, it.toString()) }
        cfg.getOrNull(SemanticsProperties.StateDescription)?.let { props.add(PropertyNames.STATE_DESCRIPTION, it) }
        cfg.getOrNull(SemanticsProperties.Selected)?.let { props.add(PropertyNames.SELECTED, it.toString()) }
        cfg.getOrNull(
            SemanticsProperties.ToggleableState
        )?.let { props.add(PropertyNames.TOGGLEABLE_STATE, it.toString()) }
        cfg.getOrNull(SemanticsProperties.Password)?.let { props.add(PropertyNames.PASSWORD, it.toString()) }
    }

    private fun flags(cfg: SemanticsConfiguration, props: MutableList<LayoutProperty>) {
        attributes(cfg, props)
        states(cfg, props)
    }

    private fun attributes(cfg: SemanticsConfiguration, props: MutableList<LayoutProperty>) {
        (cfg.getAnyOrNull("Heading") ?: cfg.getAnyOrNull("AccessibilityHeading"))?.let {
            props.add(PropertyNames.HEADING, it.toString())
        }
        cfg.getOrNull(SemanticsProperties.PaneTitle)?.let { props.add(PropertyNames.PANE_TITLE, it) }
        cfg.getOrNull(SemanticsProperties.Focused)?.let { props.add(PropertyNames.FOCUSED, it.toString()) }
        cfg.getOrNull(SemanticsProperties.IsEditable)?.let { props.add(PropertyNames.IS_EDITABLE, it.toString()) }
        cfg.getOrNull(SemanticsProperties.ImeAction)?.let { props.add(PropertyNames.IME_ACTION, it.toString()) }
        cfg.getOrNull(SemanticsProperties.Error)?.let { props.add(PropertyNames.ERROR, it) }
        cfg.getOrNull(SemanticsProperties.LiveRegion)?.let { props.add(PropertyNames.LIVE_REGION, it.toString()) }
        cfg.getOrNull(SemanticsProperties.ContentType)?.let { props.add(PropertyNames.CONTENT_TYPE, it.toString()) }
        cfg.getOrNull(
            SemanticsProperties.ContentDataType
        )?.let { props.add(PropertyNames.CONTENT_DATA_TYPE, it.toString()) }
        cfg.getOrNull(
            SemanticsProperties.TraversalIndex
        )?.let { props.add(PropertyNames.TRAVERSAL_INDEX, it.toString()) }
        collectionAttributes(cfg, props)
    }

    private fun collectionAttributes(cfg: SemanticsConfiguration, props: MutableList<LayoutProperty>) {
        cfg.getOrNull(
            SemanticsProperties.CollectionInfo
        )?.let { props.add(PropertyNames.COLLECTION_INFO, it.toString()) }
        cfg.getOrNull(SemanticsProperties.CollectionItemInfo)?.let {
            props.add(PropertyNames.COLLECTION_ITEM_INFO, it.toString())
        }
        cfg.getOrNull(SemanticsProperties.HorizontalScrollAxisRange)?.let {
            props.add(PropertyNames.H_SCROLL_RANGE, it.toString())
        }
        cfg.getOrNull(SemanticsProperties.VerticalScrollAxisRange)?.let {
            props.add(PropertyNames.V_SCROLL_RANGE, it.toString())
        }
    }

    private fun states(cfg: SemanticsConfiguration, props: MutableList<LayoutProperty>) {
        if (cfg.getAnyOrNull("Disabled") != null) props.add(PropertyNames.ENABLED, "false")
        if (cfg.getAnyOrNull("HideFromAccessibility") != null) props.add(PropertyNames.HIDE_FROM_ACCESSIBILITY, "true")
        cfg.getOrNull(
            SemanticsProperties.IsTraversalGroup
        )?.let { props.add(PropertyNames.IS_TRAVERSAL_GROUP, it.toString()) }
        @Suppress("DEPRECATION")
        cfg.getOrNull(
            SemanticsProperties.IsContainer
        )?.let { props.add(PropertyNames.IS_CONTAINER_DEPRECATED, it.toString()) }
        cfg.getOrNull(
            SemanticsProperties.TextSubstitution
        )?.let { props.add(PropertyNames.TEXT_SUBSTITUTION, it.toString()) }
        cfg.getOrNull(SemanticsProperties.IsShowingTextSubstitution)?.let {
            props.add(PropertyNames.IS_SHOWING_TEXT_SUBSTITUTION, it.toString())
        }
        if (cfg.getAnyOrNull("IndexForKey") != null) props.add(PropertyNames.INDEX_FOR_KEY, "present")
        cfg.getOrNull(
            SemanticsProperties.MaxTextLength
        )?.let { props.add(PropertyNames.MAX_TEXT_LENGTH, it.toString()) }
        if (cfg.getAnyOrNull("IsDialog") != null) props.add(PropertyNames.IS_DIALOG, "true")
        if (cfg.getAnyOrNull("IsPopup") != null) props.add(PropertyNames.IS_POPUP, "true")
    }

    private fun coords(node: SemanticsNode, props: MutableList<LayoutProperty>) {
        val r = node.boundsInRoot
        props.add(
            PropertyNames.BOUNDS_IN_ROOT,
            "${r.left.toInt()},${r.top.toInt()},${r.width.toInt()},${r.height.toInt()}"
        )
    }

    private fun actions(cfg: SemanticsConfiguration, props: MutableList<LayoutProperty>) {
        val actions = mutableListOf<String>()
        fun addAction(name: String, label: String = name) { if (cfg.hasActionByName(name)) actions += label }
        addAction("OnClick", "onClick")
        addAction("OnLongClick", "onLongClick")
        addAction("ScrollBy", "scrollBy")
        addAction("ScrollByOffset", "scrollByOffset")
        addAction("ScrollToIndex", "scrollToIndex")
        addAction("SetText", "setText")
        addAction("SetSelection", "setSelection")
        addAction("SetTextSubstitution", "setTextSubstitution")
        addAction("ShowTextSubstitution", "showTextSubstitution")
        addAction("ClearTextSubstitution", "clearTextSubstitution")
        addAction("OnAutofillText", "onAutofillText")
        addAction("SetProgress", "setProgress")
        addAction("InsertTextAtCursor", "insertTextAtCursor")
        addAction("OnImeAction", "onImeAction")
        addAction("CopyText", "copyText")
        addAction("CutText", "cutText")
        addAction("PasteText", "pasteText")
        addAction("Expand", "expand")
        addAction("Collapse", "collapse")
        addAction("Dismiss", "dismiss")
        addAction("RequestFocus", "requestFocus")
        addAction("PageUp", "pageUp")
        addAction("PageDown", "pageDown")
        addAction("PageLeft", "pageLeft")
        addAction("PageRight", "pageRight")
        addAction("GetTextLayoutResult", "getTextLayoutResult")
        addAction("GetScrollViewportLength", "getScrollViewportLength")
        if (actions.isNotEmpty()) props.add(PropertyNames.ACTIONS, actions.joinToString())
    }

    private fun customActions(cfg: SemanticsConfiguration, props: MutableList<LayoutProperty>) {
        cfg.getOrNull(SemanticsActions.CustomActions)?.let { list ->
            if (list.isNotEmpty()) props.add(PropertyNames.CUSTOM_ACTIONS, list.joinToString { it.label })
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
}

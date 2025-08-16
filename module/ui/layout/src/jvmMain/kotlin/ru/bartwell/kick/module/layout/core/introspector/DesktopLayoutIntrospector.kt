// module/ui/layout/src/jvmMain/kotlin/ru/bartwell/kick/module/layout/core/introspector/DesktopLayoutIntrospector.kt
package ru.bartwell.kick.module.layout.core.introspector

import androidx.compose.ui.node.RootForTest
import androidx.compose.ui.semantics.*
import ru.bartwell.kick.module.layout.core.data.*
import java.awt.*
import java.lang.reflect.Proxy
import javax.accessibility.AccessibleRole
import javax.swing.*
import javax.swing.text.JTextComponent

private class DesktopLayoutIntrospector : LayoutIntrospector {

    private val components = mutableMapOf<String, Component>()
    private val semantics = mutableMapOf<String, SemanticsNode>()
    private val processedSemanticsRoots = mutableSetOf<Int>()

    override suspend fun captureHierarchy(): LayoutNodeSnapshot? {
        return runCatching {
            val window = activeWindow() ?: return null
            components.clear()
            semantics.clear()
            processedSemanticsRoots.clear()
            buildSnapshot(window)
        }.getOrNull()
    }

    override suspend fun propertiesOf(id: LayoutNodeId): List<LayoutProperty> {
        components[id.raw]?.let { c ->
            val props = mutableListOf<LayoutProperty>()
            props += LayoutProperty("classFqn", c.javaClass.name)
            props += LayoutProperty("class", c.javaClass.shortName())
            val loc = runCatching { c.locationOnScreen }.getOrNull() ?: Point(0, 0)
            props += LayoutProperty("bounds", "${loc.x},${loc.y},${c.width},${c.height}")
            props += LayoutProperty("visible", c.isVisible.toString())
            props += LayoutProperty("showing", c.isShowing.toString())
            props += LayoutProperty("enabled", c.isEnabled.toString())
            props += LayoutProperty("focusable", c.isFocusable.toString())
            props += LayoutProperty("focused", (KeyboardFocusManager.getCurrentKeyboardFocusManager().focusOwner === c).toString())
            c.parent?.let { p ->
                props += LayoutProperty("zOrderInParent", runCatching {
                    (p as? Container)?.getComponentZOrder(c)
                }.getOrNull()?.toString() ?: "n/a")
                props += LayoutProperty("parentClass", p.javaClass.shortName())
            }
            props += LayoutProperty("preferredSize", c.preferredSize.toSizeString())
            props += LayoutProperty("minimumSize", c.minimumSize.toSizeString())
            props += LayoutProperty("maximumSize", c.maximumSize.toSizeString())
            props += LayoutProperty("cursor", runCatching { c.cursor?.name ?: c.cursor?.type?.toString() }.getOrNull() ?: "n/a")
            (c as? JComponent)?.let { jc ->
                props += LayoutProperty("opaque", jc.isOpaque.toString())
                props += LayoutProperty("doubleBuffered", jc.isDoubleBuffered.toString())
                jc.toolTipText?.let { props += LayoutProperty("toolTip", it) }
                jc.border?.let { props += LayoutProperty("border", it.javaClass.shortName()) }
                jc.insets?.let { props += LayoutProperty("insets", it.toInsetsString()) }
                jc.background?.let { props += LayoutProperty("background", it.toHex()) }
                jc.foreground?.let { props += LayoutProperty("foreground", it.toHex()) }
                jc.font?.let { f -> props += LayoutProperty("font", "${f.family}, ${f.size}, style=${f.style}") }
                // client properties
                props.addJComponentClientProperties(jc)
            }
            c.componentOrientation?.let { props += LayoutProperty("orientation", if (it.isLeftToRight) "LTR" else "RTL") }
            c.name?.let { props += LayoutProperty("name", it) }

            // Swing specifics
            props.addSwingDetails(c)

            // Accessibility
            c.accessibleContext?.let { ac ->
                ac.accessibleName?.let { props += LayoutProperty("a11y.name", it) }
                ac.accessibleDescription?.let { props += LayoutProperty("a11y.description", it) }
                runCatching { ac.accessibleRole?.toDisplayString() }.getOrNull()?.let { props += LayoutProperty("a11y.role", it) }
                runCatching { ac.accessibleStateSet?.toString() }.getOrNull()?.let { props += LayoutProperty("a11y.states", it) }
            }
            return props
        }

        semantics[id.raw]?.let { node ->
            return node.collectSemanticsProps()
        }

        return emptyList()
    }

    // ===== HIERARCHY =====

    private fun buildSnapshot(component: Component): LayoutNodeSnapshot {
        val id = LayoutNodeId(System.identityHashCode(component).toString())
        components[id.raw] = component

        val typeShort = component.javaClass.shortName()
        val displayName = typeShort // только имя класса для AWT/Swing
        val location = runCatching { component.locationOnScreen }.getOrNull() ?: Point(0, 0)
        val rect = LayoutRect(location.x, location.y, component.width, component.height)

        val childComponents = (component as? Container)?.components?.map { buildSnapshot(it) } ?: emptyList()
        val semanticChildren = semanticsSnapshots(component)

        val children = childComponents + semanticChildren
        return LayoutNodeSnapshot(
            id = id,
            typeName = component.javaClass.name, // FQN для поиска/свойств
            displayName = displayName,
            bounds = rect,
            isVisible = component.isVisible,
            testTag = component.name,
            children = children
        )
    }

    /**
     * Вешаем Semantics один раз — на «самый глубокий» AWT-хост данного RootForTest.
     */
    private fun semanticsSnapshots(component: Component): List<LayoutNodeSnapshot> {
        return runCatching {
            val root = findRootForTest(component) ?: return emptyList()
            if ((component as? Container)?.components?.any { child -> findRootForTest(child) === root } == true) {
                return emptyList()
            }

            val rootId = System.identityHashCode(root)
            if (!processedSemanticsRoots.add(rootId)) return emptyList()

            root.semanticsOwner.rootSemanticsNode.children.mapNotNull { buildSemanticsSnapshotOrNull(it) }
        }.getOrElse { emptyList() }
    }

    private fun buildSemanticsSnapshotOrNull(node: SemanticsNode): LayoutNodeSnapshot? {
        val n = skipWrapperSemantics(node)

        val role = n.config.getOrNull(SemanticsProperties.Role)
        val roleLabel = role?.toString() // в твоей версии Role.toString уже даёт читаемое имя
        val text = extractTextLabel(n) // Text -> ContentDescription -> поиск в детях
        val display = when {
            roleLabel != null && !text.isNullOrBlank() -> "$roleLabel ($text)"
            roleLabel != null -> roleLabel
            !text.isNullOrBlank() -> text
            else -> "Compose"
        }

        val childrenList = n.children.mapNotNull { buildSemanticsSnapshotOrNull(it) }
        if (display == "Compose" && childrenList.isEmpty()) return null

        val rect = n.boundsInRoot
        val layoutRect = LayoutRect(rect.left.toInt(), rect.top.toInt(), rect.width.toInt(), rect.height.toInt())

        val id = LayoutNodeId("s${n.id}")
        semantics[id.raw] = n

        return LayoutNodeSnapshot(
            id = id,
            typeName = "Compose",
            displayName = display,
            bounds = layoutRect,
            isVisible = null,
            testTag = n.config.getOrNull(SemanticsProperties.TestTag),
            children = childrenList
        )
    }

    private tailrec fun skipWrapperSemantics(node: SemanticsNode): SemanticsNode {
        val hasLabel =
            node.config.getOrNull(SemanticsProperties.TestTag) != null ||
                node.config.getOrNull(SemanticsProperties.ContentDescription) != null ||
                node.config.getOrNull(SemanticsProperties.Text) != null ||
                node.config.getOrNull(SemanticsProperties.Role) != null

        return if (!hasLabel && node.children.size == 1) skipWrapperSemantics(node.children.first()) else node
    }

    // ===== Compose label/text helpers =====

    private fun extractTextLabel(node: SemanticsNode): String? {
        // 1) Прямо на узле
        node.config.getOrNull(SemanticsProperties.Text)?.let { list ->
            val s = list.joinToString { it.text }.trim()
            if (s.isNotEmpty()) return s
        }
        node.config.getOrNull(SemanticsProperties.ContentDescription)?.let {
            val s = it.joinToString().trim()
            if (s.isNotEmpty()) return s
        }
        // 2) Поиск в детях
        for (child in node.children) {
            val s = extractTextLabel(child)
            if (!s.isNullOrBlank()) return s
        }
        return null
    }

    // ===== UTIL =====

    private fun Class<*>.shortName(): String =
        simpleName.takeIf { it.isNotBlank() } ?: name.substringAfterLast('.')

    private fun Dimension?.toSizeString(): String = if (this == null) "n/a" else "${width}x$height"

    private fun Insets.toInsetsString(): String = "$left,$top,$right,$bottom"

    private fun Color.toHex(): String = "#%02X%02X%02X%02X".format(alpha, red, green, blue)

    private fun AccessibleRole.toDisplayString(): String = toString()

    private fun MutableList<LayoutProperty>.addIf(name: String, value: String?) {
        if (!value.isNullOrEmpty()) this += LayoutProperty(name, value)
    }

    // ===== Swing details =====

    private fun MutableList<LayoutProperty>.addSwingDetails(c: Component) {
        when (c) {
            is AbstractButton -> {
                addIf("text", c.text)
                addIf("actionCommand", c.actionCommand)
                this += LayoutProperty("selected", (c as? JToggleButton)?.isSelected?.toString() ?: "false")
                this += LayoutProperty("armed", c.model.isArmed.toString())
                this += LayoutProperty("pressed", c.model.isPressed.toString())
                this += LayoutProperty("rollover", c.model.isRollover.toString())
                this += LayoutProperty("mnemonic", if (c.mnemonic == 0) "none" else c.mnemonic.toString())
            }
            is JLabel -> {
                addIf("text", c.text)
                c.icon?.let { this += LayoutProperty("icon", it.javaClass.shortName()) }
                this += LayoutProperty("horizontalAlignment", alignToString(c.horizontalAlignment))
                this += LayoutProperty("verticalAlignment", alignToString(c.verticalAlignment))
            }
            is JTextComponent -> {
                this += LayoutProperty("textLength", (c.document?.length ?: 0).toString())
                this += LayoutProperty("editable", c.isEditable.toString())
                this += LayoutProperty("caretPosition", c.caretPosition.toString())
                this += LayoutProperty("selection", "${c.selectionStart}..${c.selectionEnd}")
                (c as? JTextArea)?.let { ta ->
                    this += LayoutProperty("rows", ta.rows.toString())
                    this += LayoutProperty("columns", ta.columns.toString())
                    this += LayoutProperty("lineWrap", ta.lineWrap.toString())
                }
                (c as? JTextField)?.let { tf ->
                    this += LayoutProperty("columns", tf.columns.toString())
                }
            }
            is JProgressBar -> {
                this += LayoutProperty("value", c.value.toString())
                this += LayoutProperty("min", c.minimum.toString())
                this += LayoutProperty("max", c.maximum.toString())
                this += LayoutProperty("indeterminate", c.isIndeterminate.toString())
                this += LayoutProperty("orientation", if (c.orientation == SwingConstants.VERTICAL) "VERTICAL" else "HORIZONTAL")
            }
            is JSlider -> {
                this += LayoutProperty("value", c.value.toString())
                this += LayoutProperty("min", c.minimum.toString())
                this += LayoutProperty("max", c.maximum.toString())
                this += LayoutProperty("majorTick", c.majorTickSpacing.toString())
                this += LayoutProperty("minorTick", c.minorTickSpacing.toString())
                this += LayoutProperty("orientation", if (c.orientation == SwingConstants.VERTICAL) "VERTICAL" else "HORIZONTAL")
            }
            is JScrollBar -> {
                this += LayoutProperty("value", c.value.toString())
                this += LayoutProperty("min", c.minimum.toString())
                this += LayoutProperty("max", c.maximum.toString())
                this += LayoutProperty("extent", c.model.extent.toString())
                this += LayoutProperty("orientation", if (c.orientation == Adjustable.VERTICAL) "VERTICAL" else "HORIZONTAL")
            }
            is JList<*> -> {
                this += LayoutProperty("items", c.model.size.toString())
                this += LayoutProperty("selectedIndices", c.selectedIndices.joinToString(prefix = "[", postfix = "]"))
            }
            is JTable -> {
                this += LayoutProperty("rows", c.rowCount.toString())
                this += LayoutProperty("columns", c.columnCount.toString())
                this += LayoutProperty("selected", "r=${c.selectedRow}, c=${c.selectedColumn}")
                this += LayoutProperty("autoCreateRowSorter", c.autoCreateRowSorter.toString())
            }
            is JTree -> {
                this += LayoutProperty("rows", c.rowCount.toString())
                this += LayoutProperty("selectionCount", c.selectionCount.toString())
                this += LayoutProperty("rootVisible", c.isRootVisible.toString())
                this += LayoutProperty("showsRootHandles", c.showsRootHandles.toString())
            }
            is JComboBox<*> -> {
                this += LayoutProperty("itemCount", c.itemCount.toString())
                this += LayoutProperty("selectedIndex", c.selectedIndex.toString())
                this += LayoutProperty("selectedItem", c.selectedItem?.toString() ?: "null")
                this += LayoutProperty("editable", c.isEditable.toString())
            }
        }
    }

    private fun alignToString(a: Int): String = when (a) {
        SwingConstants.LEFT -> "LEFT"
        SwingConstants.RIGHT -> "RIGHT"
        SwingConstants.CENTER -> "CENTER"
        SwingConstants.TOP -> "TOP"
        SwingConstants.BOTTOM -> "BOTTOM"
        else -> a.toString()
    }

    // ===== JComponent client properties =====

    private fun MutableList<LayoutProperty>.addJComponentClientProperties(jc: JComponent) {
        val table = runCatching {
            val f = JComponent::class.java.getDeclaredField("clientProperties")
            f.isAccessible = true
            f.get(jc)
        }.getOrNull() ?: return

        val arrayTable = runCatching { Class.forName("javax.swing.ArrayTable") }.getOrNull() ?: return

        val getKeys = runCatching { arrayTable.getDeclaredMethod("getKeys", Any::class.java) }.getOrNull() ?: return
        getKeys.isAccessible = true

        val keysArray = (runCatching { getKeys.invoke(null, table) }.getOrNull() as? Array<*>) ?: return
        if (keysArray.isEmpty()) return

        val getMethod = runCatching { arrayTable.getDeclaredMethod("get", Any::class.java, Any::class.java) }.getOrNull()
            ?: return
        getMethod.isAccessible = true

        keysArray.forEach { k ->
            val v = runCatching { getMethod.invoke(null, table, k) }.getOrNull()
            this += LayoutProperty("client.$k", v?.toString() ?: "null")
        }
    }

    // ===== Compose semantics: properties/actions =====

    private fun SemanticsNode.collectSemanticsProps(): List<LayoutProperty> {
        val cfg = this.config
        val props = mutableListOf<LayoutProperty>()

        // основные
        cfg.getOrNull(SemanticsProperties.TestTag)?.let { props += LayoutProperty("testTag", it) }
        cfg.getOrNull(
            SemanticsProperties.ContentDescription
        )?.let { props += LayoutProperty("contentDescription", it.joinToString()) }
        cfg.getOrNull(
            SemanticsProperties.Text
        )?.let { list -> props += LayoutProperty("text", list.joinToString { it.text }) }
        cfg.getOrNull(SemanticsProperties.Role)?.let { props += LayoutProperty("role", it.toString()) }
        cfg.getOrNull(SemanticsProperties.StateDescription)?.let { props += LayoutProperty("stateDescription", it) }
        cfg.getOrNull(SemanticsProperties.Selected)?.let { props += LayoutProperty("selected", it.toString()) }
        cfg.getOrNull(
            SemanticsProperties.ToggleableState
        )?.let { props += LayoutProperty("toggleableState", it.toString()) }
        cfg.getOrNull(SemanticsProperties.Password)?.let { props += LayoutProperty("password", it.toString()) }

        // флаги/доп.свойства
        (cfg.getAnyOrNull("Heading") ?: cfg.getAnyOrNull("AccessibilityHeading"))?.let {
            props += LayoutProperty("heading", it.toString())
        }
        cfg.getOrNull(SemanticsProperties.PaneTitle)?.let { props += LayoutProperty("paneTitle", it) }
        cfg.getOrNull(SemanticsProperties.Focused)?.let { props += LayoutProperty("focused", it.toString()) }
        cfg.getOrNull(SemanticsProperties.IsEditable)?.let { props += LayoutProperty("isEditable", it.toString()) }
        cfg.getOrNull(SemanticsProperties.ImeAction)?.let { props += LayoutProperty("imeAction", it.toString()) }
        cfg.getOrNull(SemanticsProperties.Error)?.let { props += LayoutProperty("error", it) }
        cfg.getOrNull(SemanticsProperties.LiveRegion)?.let { props += LayoutProperty("liveRegion", it.toString()) }
        cfg.getOrNull(SemanticsProperties.ContentType)?.let { props += LayoutProperty("contentType", it.toString()) }
        cfg.getOrNull(
            SemanticsProperties.ContentDataType
        )?.let { props += LayoutProperty("contentDataType", it.toString()) }
        cfg.getOrNull(
            SemanticsProperties.TraversalIndex
        )?.let { props += LayoutProperty("traversalIndex", it.toString()) }
        cfg.getOrNull(
            SemanticsProperties.CollectionInfo
        )?.let { props += LayoutProperty("collectionInfo", it.toString()) }
        cfg.getOrNull(
            SemanticsProperties.CollectionItemInfo
        )?.let { props += LayoutProperty("collectionItemInfo", it.toString()) }
        cfg.getOrNull(
            SemanticsProperties.HorizontalScrollAxisRange
        )?.let { props += LayoutProperty("hScrollRange", it.toString()) }
        cfg.getOrNull(
            SemanticsProperties.VerticalScrollAxisRange
        )?.let { props += LayoutProperty("vScrollRange", it.toString()) }
        if (cfg.getAnyOrNull("Disabled") != null) props += LayoutProperty("enabled", "false")
        if (cfg.getAnyOrNull("HideFromAccessibility") != null) props += LayoutProperty("hideFromAccessibility", "true")
        cfg.getOrNull(
            SemanticsProperties.IsTraversalGroup
        )?.let { props += LayoutProperty("isTraversalGroup", it.toString()) }
        @Suppress("DEPRECATION")
        cfg.getOrNull(
            SemanticsProperties.IsContainer
        )?.let { props += LayoutProperty("isContainer(deprecated)", it.toString()) }
        cfg.getOrNull(
            SemanticsProperties.TextSubstitution
        )?.let { props += LayoutProperty("textSubstitution", it.toString()) }
        cfg.getOrNull(
            SemanticsProperties.IsShowingTextSubstitution
        )?.let { props += LayoutProperty("isShowingTextSubstitution", it.toString()) }
        if (cfg.getAnyOrNull("IndexForKey") != null) props += LayoutProperty("indexForKey", "present")
        cfg.getOrNull(
            SemanticsProperties.MaxTextLength
        )?.let { props += LayoutProperty("maxTextLength", it.toString()) }
        if (cfg.getAnyOrNull("IsDialog") != null) props += LayoutProperty("isDialog", "true")
        if (cfg.getAnyOrNull("IsPopup") != null) props += LayoutProperty("isPopup", "true")

        // координаты
        val r = this.boundsInRoot
        props += LayoutProperty("boundsInRoot", "${r.left.toInt()},${r.top.toInt()},${r.width.toInt()},${r.height.toInt()}")

        // actions
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
        if (actions.isNotEmpty()) props += LayoutProperty("actions", actions.joinToString())

        // custom actions (метки)
        cfg.getOrNull(SemanticsActions.CustomActions)?.let { list ->
            if (list.isNotEmpty()) props += LayoutProperty("customActions", list.joinToString { it.label })
        }

        return props
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

    // ===== Window / RootForTest =====

    private fun activeWindow(): Window? {
        val manager = KeyboardFocusManager.getCurrentKeyboardFocusManager()
        return manager.activeWindow ?: Window.getWindows().firstOrNull { it.isVisible }
    }

    private fun findRootForTest(obj: Any?): RootForTest? {
        if (obj == null) return null
        if (obj is RootForTest) return obj

        var result: RootForTest? = null
        if (tryAttachRootForTest(obj) { result = it as? RootForTest }) return result

        runCatching { obj.javaClass.getDeclaredField("rootForTest").apply { isAccessible = true }.get(obj) }
            .getOrNull()?.let { return findRootForTest(it) }

        runCatching { obj.javaClass.getDeclaredMethod("getRootForTest").apply { isAccessible = true }.invoke(obj) }
            .getOrNull()?.let { return findRootForTest(it) }

        listOf("scene", "currentScene").forEach { name ->
            val scene = runCatching {
                obj.javaClass.getDeclaredField(
                    name
                ).apply { isAccessible = true }.get(obj)
            }.getOrNull()
            findRootForTest(scene)?.let { return it }
        }
        listOf("layer", "_layer", "skiaLayer", "composeLayer").forEach { name ->
            val layer = runCatching {
                obj.javaClass.getDeclaredField(
                    name
                ).apply { isAccessible = true }.get(obj)
            }.getOrNull()
            findRootForTest(layer)?.let { return it }
        }
        if (obj is Container) {
            obj.components.forEach { child ->
                findRootForTest(child)?.let { return it }
            }
        }
        return null
    }

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

public actual fun provideLayoutIntrospector(): LayoutIntrospector = DesktopLayoutIntrospector()

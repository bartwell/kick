package ru.bartwell.kick.module.layout.core.properties

import ru.bartwell.kick.module.layout.core.common.PropertyNames
import ru.bartwell.kick.module.layout.core.common.add
import ru.bartwell.kick.module.layout.core.common.addIfNotEmpty
import ru.bartwell.kick.module.layout.core.common.safeLocationOnScreen
import ru.bartwell.kick.module.layout.core.common.shortName
import ru.bartwell.kick.module.layout.core.common.toSizeString
import ru.bartwell.kick.module.layout.core.data.LayoutProperty
import java.awt.Component
import java.awt.KeyboardFocusManager

internal class SwingBaseCollector : PropertyCollector {
    override fun canCollect(c: Component) = true

    override fun collect(c: Component): List<LayoutProperty> = buildList {
        add(PropertyNames.CLASS_FQN, c.javaClass.name)
        add(PropertyNames.CLASS, c.javaClass.shortName())
        val loc = c.safeLocationOnScreen()
        add(PropertyNames.BOUNDS, "${loc.x},${loc.y},${c.width},${c.height}")
        add(PropertyNames.VISIBLE, c.isVisible.toString())
        add(PropertyNames.SHOWING, c.isShowing.toString())
        add(PropertyNames.ENABLED, c.isEnabled.toString())
        add(PropertyNames.FOCUSABLE, c.isFocusable.toString())
        add(PropertyNames.FOCUSED, (KeyboardFocusManager.getCurrentKeyboardFocusManager().focusOwner === c).toString())
        c.parent?.let { p ->
            add(
                PropertyNames.Z_ORDER_IN_PARENT,
                runCatching { p.getComponentZOrder(c) }.getOrNull()?.toString() ?: "n/a"
            )
            add(PropertyNames.PARENT_CLASS, p.javaClass.shortName())
        }
        add(PropertyNames.PREFERRED_SIZE, c.preferredSize.toSizeString())
        add(PropertyNames.MINIMUM_SIZE, c.minimumSize.toSizeString())
        add(PropertyNames.MAXIMUM_SIZE, c.maximumSize.toSizeString())
        add(PropertyNames.CURSOR, runCatching { c.cursor.name ?: c.cursor.type.toString() }.getOrNull() ?: "n/a")
        c.name?.let { add(PropertyNames.NAME, it) }
        c.componentOrientation?.let { add(PropertyNames.ORIENTATION, if (it.isLeftToRight) "LTR" else "RTL") }
        c.accessibleContext?.let { ac ->
            addIfNotEmpty(PropertyNames.A11Y_NAME, ac.accessibleName)
            addIfNotEmpty(PropertyNames.A11Y_DESCRIPTION, ac.accessibleDescription)
            runCatching { ac.accessibleRole?.toDisplayString() }.getOrNull()?.let { add(PropertyNames.A11Y_ROLE, it) }
            runCatching { ac.accessibleStateSet?.toString() }.getOrNull()?.let { add(PropertyNames.A11Y_STATES, it) }
        }
    }
}

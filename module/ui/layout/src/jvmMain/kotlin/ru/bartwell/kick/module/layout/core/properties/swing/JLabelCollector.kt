package ru.bartwell.kick.module.layout.core.properties.swing

import ru.bartwell.kick.module.layout.core.common.PropertyNames
import ru.bartwell.kick.module.layout.core.common.add
import ru.bartwell.kick.module.layout.core.common.addIfNotEmpty
import ru.bartwell.kick.module.layout.core.common.shortName
import ru.bartwell.kick.module.layout.core.data.LayoutProperty
import ru.bartwell.kick.module.layout.core.properties.PropertyCollector
import java.awt.Component
import javax.swing.JLabel
import javax.swing.SwingConstants

internal class JLabelCollector : PropertyCollector {
    override fun canCollect(c: Component) = c is JLabel
    override fun collect(c: Component): List<LayoutProperty> = buildList {
        val l = c as JLabel
        addIfNotEmpty(PropertyNames.TEXT, l.text)
        l.icon?.let { add(PropertyNames.ICON, it.javaClass.shortName()) }
        add(PropertyNames.HORIZONTAL_ALIGNMENT, alignToString(l.horizontalAlignment))
        add(PropertyNames.VERTICAL_ALIGNMENT, alignToString(l.verticalAlignment))
    }

    private fun alignToString(a: Int): String = when (a) {
        SwingConstants.LEFT -> "LEFT"
        SwingConstants.RIGHT -> "RIGHT"
        SwingConstants.CENTER -> "CENTER"
        SwingConstants.TOP -> "TOP"
        SwingConstants.BOTTOM -> "BOTTOM"
        else -> a.toString()
    }
}

package ru.bartwell.kick.module.layout.core.properties.swing

import ru.bartwell.kick.module.layout.core.common.PropertyNames
import ru.bartwell.kick.module.layout.core.common.add
import ru.bartwell.kick.module.layout.core.data.LayoutProperty
import ru.bartwell.kick.module.layout.core.properties.PropertyCollector
import java.awt.Adjustable
import java.awt.Component
import javax.swing.JScrollBar

internal class JScrollBarCollector : PropertyCollector {
    override fun canCollect(c: Component) = c is JScrollBar
    override fun collect(c: Component): List<LayoutProperty> = buildList {
        val sb = c as JScrollBar
        add(PropertyNames.VALUE, sb.value.toString())
        add(PropertyNames.MIN, sb.minimum.toString())
        add(PropertyNames.MAX, sb.maximum.toString())
        add(PropertyNames.EXTENT, sb.model.extent.toString())
        add(
            PropertyNames.ORIENTATION,
            if (sb.orientation == Adjustable.VERTICAL) PropertyNames.VERTICAL else PropertyNames.HORIZONTAL
        )
    }
}

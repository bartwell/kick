package ru.bartwell.kick.module.layout.core.properties.swing

import ru.bartwell.kick.module.layout.core.common.PropertyNames
import ru.bartwell.kick.module.layout.core.common.add
import ru.bartwell.kick.module.layout.core.data.LayoutProperty
import ru.bartwell.kick.module.layout.core.properties.PropertyCollector
import java.awt.Component
import javax.swing.JList

internal class JListCollector : PropertyCollector {
    override fun canCollect(c: Component) = c is JList<*>
    override fun collect(c: Component): List<LayoutProperty> = buildList {
        val l = c as JList<*>
        add(PropertyNames.ITEMS, l.model.size.toString())
        add(PropertyNames.SELECTED_INDICES, l.selectedIndices.joinToString(prefix = "[", postfix = "]"))
    }
}

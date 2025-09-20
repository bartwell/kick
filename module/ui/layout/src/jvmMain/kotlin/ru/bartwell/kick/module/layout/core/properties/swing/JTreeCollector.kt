package ru.bartwell.kick.module.layout.core.properties.swing

import ru.bartwell.kick.module.layout.core.common.PropertyNames
import ru.bartwell.kick.module.layout.core.common.add
import ru.bartwell.kick.module.layout.core.data.LayoutProperty
import ru.bartwell.kick.module.layout.core.properties.PropertyCollector
import java.awt.Component
import javax.swing.JTree

internal class JTreeCollector : PropertyCollector {
    override fun canCollect(c: Component) = c is JTree
    override fun collect(c: Component): List<LayoutProperty> = buildList {
        val t = c as JTree
        add(PropertyNames.ROWS, t.rowCount.toString())
        add(PropertyNames.SELECTION_COUNT, t.selectionCount.toString())
        add(PropertyNames.ROOT_VISIBLE, t.isRootVisible.toString())
        add(PropertyNames.SHOWS_ROOT_HANDLES, t.showsRootHandles.toString())
    }
}

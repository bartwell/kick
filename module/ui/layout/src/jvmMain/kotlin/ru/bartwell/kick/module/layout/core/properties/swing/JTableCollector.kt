package ru.bartwell.kick.module.layout.core.properties.swing

import ru.bartwell.kick.module.layout.core.common.PropertyNames
import ru.bartwell.kick.module.layout.core.common.add
import ru.bartwell.kick.module.layout.core.data.LayoutProperty
import ru.bartwell.kick.module.layout.core.properties.PropertyCollector
import java.awt.Component
import javax.swing.JTable

internal class JTableCollector : PropertyCollector {
    override fun canCollect(c: Component) = c is JTable
    override fun collect(c: Component): List<LayoutProperty> = buildList {
        val t = c as JTable
        add(PropertyNames.ROWS, t.rowCount.toString())
        add(PropertyNames.COLUMNS, t.columnCount.toString())
        add(PropertyNames.SELECTED, "r=${t.selectedRow}, c=${t.selectedColumn}")
        add(PropertyNames.AUTO_CREATE_ROW_SORTER, t.autoCreateRowSorter.toString())
    }
}

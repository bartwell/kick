package ru.bartwell.kick.module.layout.core.properties.swing

import ru.bartwell.kick.module.layout.core.common.PropertyNames
import ru.bartwell.kick.module.layout.core.common.add
import ru.bartwell.kick.module.layout.core.data.LayoutProperty
import ru.bartwell.kick.module.layout.core.properties.PropertyCollector
import java.awt.Component
import javax.swing.JComboBox

internal class JComboBoxCollector : PropertyCollector {
    override fun canCollect(c: Component) = c is JComboBox<*>
    override fun collect(c: Component): List<LayoutProperty> = buildList {
        val b = c as JComboBox<*>
        add(PropertyNames.ITEM_COUNT, b.itemCount.toString())
        add(PropertyNames.SELECTED_INDEX, b.selectedIndex.toString())
        add(PropertyNames.SELECTED_ITEM, b.selectedItem?.toString() ?: "null")
        add(PropertyNames.EDITABLE, b.isEditable.toString())
    }
}

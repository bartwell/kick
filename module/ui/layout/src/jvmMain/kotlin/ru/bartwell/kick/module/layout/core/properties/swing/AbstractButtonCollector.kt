package ru.bartwell.kick.module.layout.core.properties.swing

import ru.bartwell.kick.module.layout.core.common.PropertyNames
import ru.bartwell.kick.module.layout.core.common.add
import ru.bartwell.kick.module.layout.core.common.addIfNotEmpty
import ru.bartwell.kick.module.layout.core.data.LayoutProperty
import ru.bartwell.kick.module.layout.core.properties.PropertyCollector
import java.awt.Component
import javax.swing.AbstractButton
import javax.swing.JToggleButton

internal class AbstractButtonCollector : PropertyCollector {
    override fun canCollect(c: Component) = c is AbstractButton
    override fun collect(c: Component): List<LayoutProperty> = buildList {
        val b = c as AbstractButton
        addIfNotEmpty(PropertyNames.TEXT, b.text)
        addIfNotEmpty(PropertyNames.ACTION_COMMAND, b.actionCommand)
        add(PropertyNames.SELECTED, (b as? JToggleButton)?.isSelected?.toString() ?: "false")
        add(PropertyNames.ARMED, b.model.isArmed.toString())
        add(PropertyNames.PRESSED, b.model.isPressed.toString())
        add(PropertyNames.ROLLOVER, b.model.isRollover.toString())
        add(PropertyNames.MNEMONIC, if (b.mnemonic == 0) "none" else b.mnemonic.toString())
    }
}

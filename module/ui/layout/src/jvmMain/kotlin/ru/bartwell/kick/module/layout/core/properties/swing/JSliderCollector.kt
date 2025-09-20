package ru.bartwell.kick.module.layout.core.properties.swing

import ru.bartwell.kick.module.layout.core.common.PropertyNames
import ru.bartwell.kick.module.layout.core.common.add
import ru.bartwell.kick.module.layout.core.data.LayoutProperty
import ru.bartwell.kick.module.layout.core.properties.PropertyCollector
import java.awt.Component
import javax.swing.JSlider
import javax.swing.SwingConstants

internal class JSliderCollector : PropertyCollector {
    override fun canCollect(c: Component) = c is JSlider
    override fun collect(c: Component): List<LayoutProperty> = buildList {
        val s = c as JSlider
        add(PropertyNames.VALUE, s.value.toString())
        add(PropertyNames.MIN, s.minimum.toString())
        add(PropertyNames.MAX, s.maximum.toString())
        add(PropertyNames.MAJOR_TICK, s.majorTickSpacing.toString())
        add(PropertyNames.MINOR_TICK, s.minorTickSpacing.toString())
        add(
            PropertyNames.ORIENTATION,
            if (s.orientation == SwingConstants.VERTICAL) PropertyNames.VERTICAL else PropertyNames.HORIZONTAL
        )
    }
}

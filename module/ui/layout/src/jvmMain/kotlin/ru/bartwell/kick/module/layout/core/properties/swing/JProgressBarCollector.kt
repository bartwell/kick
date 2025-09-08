package ru.bartwell.kick.module.layout.core.properties.swing

import ru.bartwell.kick.module.layout.core.common.PropertyNames
import ru.bartwell.kick.module.layout.core.common.add
import ru.bartwell.kick.module.layout.core.data.LayoutProperty
import ru.bartwell.kick.module.layout.core.properties.PropertyCollector
import java.awt.Component
import javax.swing.JProgressBar
import javax.swing.SwingConstants

internal class JProgressBarCollector : PropertyCollector {
    override fun canCollect(c: Component) = c is JProgressBar
    override fun collect(c: Component): List<LayoutProperty> = buildList {
        val pb = c as JProgressBar
        add(PropertyNames.VALUE, pb.value.toString())
        add(PropertyNames.MIN, pb.minimum.toString())
        add(PropertyNames.MAX, pb.maximum.toString())
        add(PropertyNames.INDETERMINATE, pb.isIndeterminate.toString())
        add(
            PropertyNames.ORIENTATION,
            if (pb.orientation == SwingConstants.VERTICAL) PropertyNames.VERTICAL else PropertyNames.HORIZONTAL
        )
    }
}

package ru.bartwell.kick.module.layout.core.properties

import ru.bartwell.kick.module.layout.core.common.PropertyNames
import ru.bartwell.kick.module.layout.core.common.ReflectionUtils
import ru.bartwell.kick.module.layout.core.common.add
import ru.bartwell.kick.module.layout.core.common.shortName
import ru.bartwell.kick.module.layout.core.common.toHex
import ru.bartwell.kick.module.layout.core.common.toInsetsString
import ru.bartwell.kick.module.layout.core.data.LayoutProperty
import java.awt.Component
import javax.swing.JComponent

internal class SwingJComponentCollector : PropertyCollector {
    override fun canCollect(c: Component) = c is JComponent
    override fun collect(c: Component): List<LayoutProperty> = buildList {
        val jc = c as JComponent
        add(PropertyNames.OPAQUE, jc.isOpaque.toString())
        add(PropertyNames.DOUBLE_BUFFERED, jc.isDoubleBuffered.toString())
        jc.toolTipText?.let { add(PropertyNames.TOOL_TIP, it) }
        jc.border?.let { add(PropertyNames.BORDER, it.javaClass.shortName()) }
        jc.insets?.let { add(PropertyNames.INSETS, it.toInsetsString()) }
        jc.background?.let { add(PropertyNames.BACKGROUND, it.toHex()) }
        jc.foreground?.let { add(PropertyNames.FOREGROUND, it.toHex()) }
        jc.font?.let { f -> add(PropertyNames.FONT, "${f.family}, ${f.size}, style=${f.style}") }
        ReflectionUtils.readJClientProperties(jc).forEach { (k, v) ->
            add("client.$k", v ?: "null")
        }
    }
}

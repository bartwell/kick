package ru.bartwell.kick.module.layout.core.properties

import ru.bartwell.kick.module.layout.core.data.LayoutProperty
import java.awt.Component

internal interface PropertyCollector {
    fun canCollect(c: Component): Boolean
    fun collect(c: Component): List<LayoutProperty>
}

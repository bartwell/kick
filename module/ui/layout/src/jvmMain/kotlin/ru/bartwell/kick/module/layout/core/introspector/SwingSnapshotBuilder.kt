package ru.bartwell.kick.module.layout.core.introspector

import ru.bartwell.kick.module.layout.core.common.NodeRegistry
import ru.bartwell.kick.module.layout.core.common.safeLocationOnScreen
import ru.bartwell.kick.module.layout.core.common.shortName
import ru.bartwell.kick.module.layout.core.data.LayoutNodeSnapshot
import ru.bartwell.kick.module.layout.core.data.LayoutProperty
import ru.bartwell.kick.module.layout.core.data.LayoutRect
import ru.bartwell.kick.module.layout.core.properties.PropertyCollector
import ru.bartwell.kick.module.layout.core.properties.SwingBaseCollector
import ru.bartwell.kick.module.layout.core.properties.SwingJComponentCollector
import ru.bartwell.kick.module.layout.core.properties.swing.AbstractButtonCollector
import ru.bartwell.kick.module.layout.core.properties.swing.JComboBoxCollector
import ru.bartwell.kick.module.layout.core.properties.swing.JLabelCollector
import ru.bartwell.kick.module.layout.core.properties.swing.JListCollector
import ru.bartwell.kick.module.layout.core.properties.swing.JProgressBarCollector
import ru.bartwell.kick.module.layout.core.properties.swing.JScrollBarCollector
import ru.bartwell.kick.module.layout.core.properties.swing.JSliderCollector
import ru.bartwell.kick.module.layout.core.properties.swing.JTableCollector
import ru.bartwell.kick.module.layout.core.properties.swing.JTextComponentCollector
import ru.bartwell.kick.module.layout.core.properties.swing.JTreeCollector
import java.awt.Component
import java.awt.Container

internal class SwingSnapshotBuilder(
    private val registry: NodeRegistry,
    private val collectors: List<PropertyCollector> = defaultCollectors
) {
    fun build(root: Component, semanticsForHost: (Component) -> List<LayoutNodeSnapshot>): LayoutNodeSnapshot {
        return buildNode(root, semanticsForHost)
    }

    private fun buildNode(component: Component, semanticsForHost: (Component) -> List<LayoutNodeSnapshot>): LayoutNodeSnapshot {
        val id = registry.register(component)
        val typeShort = component.javaClass.shortName()
        val loc = component.safeLocationOnScreen()
        val rect = LayoutRect(loc.x, loc.y, component.width, component.height)

        val swingChildren = (component as? Container)?.components?.map { buildNode(it, semanticsForHost) }.orEmpty()
        val semanticsChildren = semanticsForHost(component)
        val children = swingChildren + semanticsChildren

        return LayoutNodeSnapshot(
            id = id,
            typeName = component.javaClass.name,
            displayName = typeShort,
            bounds = rect,
            isVisible = component.isVisible,
            testTag = component.name,
            children = children
        )
    }
}

private val defaultCollectors: List<PropertyCollector> = listOf(
    SwingBaseCollector(),
    SwingJComponentCollector(),
    AbstractButtonCollector(),
    JLabelCollector(),
    JTextComponentCollector(),
    JProgressBarCollector(),
    JSliderCollector(),
    JScrollBarCollector(),
    JListCollector(),
    JTableCollector(),
    JTreeCollector(),
    JComboBoxCollector(),
)

internal fun NodeRegistry.collectSwingProps(c: Component): List<LayoutProperty> =
    defaultCollectors.filter { it.canCollect(c) }.flatMap { it.collect(c) }

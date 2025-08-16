package ru.bartwell.kick.module.layout.core.introspector

import ru.bartwell.kick.module.layout.core.data.LayoutNodeId
import ru.bartwell.kick.module.layout.core.data.LayoutNodeSnapshot
import ru.bartwell.kick.module.layout.core.data.LayoutProperty

internal interface LayoutRepository {
    suspend fun captureHierarchy(): LayoutNodeSnapshot?
    suspend fun propertiesOf(id: LayoutNodeId): List<LayoutProperty>
}

internal class DefaultLayoutRepository(
    private val introspector: LayoutIntrospector = provideLayoutIntrospector(),
) : LayoutRepository {
    override suspend fun captureHierarchy(): LayoutNodeSnapshot? = introspector.captureHierarchy()
    override suspend fun propertiesOf(id: LayoutNodeId): List<LayoutProperty> = introspector.propertiesOf(id)
}

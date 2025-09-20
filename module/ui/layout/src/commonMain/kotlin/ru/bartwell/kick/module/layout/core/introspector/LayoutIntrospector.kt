package ru.bartwell.kick.module.layout.core.introspector

import ru.bartwell.kick.module.layout.core.data.LayoutNodeId
import ru.bartwell.kick.module.layout.core.data.LayoutNodeSnapshot
import ru.bartwell.kick.module.layout.core.data.LayoutProperty

public interface LayoutIntrospector {
    public suspend fun captureHierarchy(): LayoutNodeSnapshot?
    public suspend fun propertiesOf(id: LayoutNodeId): List<LayoutProperty>
}

public expect fun provideLayoutIntrospector(): LayoutIntrospector

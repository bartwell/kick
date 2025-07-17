package ru.bartwell.kick.core.component

import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.value.Value
import ru.bartwell.kick.core.data.Module

public interface RootComponent {
    public val stack: Value<ChildStack<*, Child<*>>>
    public var currentModule: Module?
}

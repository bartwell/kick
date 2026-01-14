package ru.bartwell.kick.core.component

import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.value.Value
import ru.bartwell.kick.core.data.Module

public interface RootComponent {
    public val stack: Value<ChildStack<*, Child<*>>>
    public var currentModule: Module?

    /**
     * Callback to be invoked when the user tries to navigate back from the root screen.
     * Should be set by the UI layer to handle closing the Kick window.
     */
    public var onCloseRequested: (() -> Unit)?
}

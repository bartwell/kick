package ru.bartwell.kick.runtime.core.component

import com.arkivanov.decompose.router.stack.StackNavigation

/**
 * A wrapper around [StackNavigation] that intercepts navigation operations
 * and calls [onCloseRequested] instead of popping when at root level.
 *
 * This is useful when Kick is launched with a startScreen - pressing back
 * at the root level should close Kick instead of doing nothing.
 */
internal class CloseAwareStackNavigation<C : Any>(
    private val delegate: StackNavigation<C>,
    private val shouldCloseInsteadOfPop: () -> Boolean,
    private val onCloseRequested: () -> Unit,
) : StackNavigation<C> by delegate {

    override fun navigate(
        transformer: (stack: List<C>) -> List<C>,
        onComplete: (newStack: List<C>, oldStack: List<C>) -> Unit
    ) {
        if (shouldCloseInsteadOfPop()) {
            delegate.navigate(
                transformer = { currentStack ->
                    // If we're at root (1 item), intercept pop and close instead
                    // Decompose protects against empty stacks, so we check manually
                    val wouldBeEmpty = currentStack.size == 1
                    if (wouldBeEmpty) {
                        onCloseRequested()
                        currentStack // Keep unchanged
                    } else {
                        transformer(currentStack)
                    }
                },
                onComplete = onComplete
            )
        } else {
            delegate.navigate(transformer, onComplete)
        }
    }
}

package ru.bartwell.kick.runtime.core.component

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.stack.animation.slide
import com.arkivanov.decompose.extensions.compose.stack.animation.stackAnimation
import ru.bartwell.kick.core.component.RootComponent
import ru.bartwell.kick.runtime.core.component.child.ModulesListChild
import ru.bartwell.kick.runtime.feature.table.presentation.ModulesListContent

@Composable
internal fun RootContent(
    component: RootComponent,
    modifier: Modifier = Modifier,
) {
    Children(
        stack = component.stack,
        modifier = modifier,
        animation = stackAnimation(slide()),
    ) {
        val child = it.instance
        if (child is ModulesListChild) {
            ModulesListContent(
                component = child.component,
                modifier = Modifier.fillMaxSize(),
            )
        } else {
            component.currentModule?.Content(it.instance)
        }
    }
}

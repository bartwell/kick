package ru.bartwell.kick.runtime.core.component

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.stack.animation.slide
import com.arkivanov.decompose.extensions.compose.stack.animation.stackAnimation
import ru.bartwell.kick.core.component.RootComponent
import ru.bartwell.kick.core.presentation.AppUiEnvironment
import ru.bartwell.kick.core.presentation.LocalAppUiEnvironment
import ru.bartwell.kick.runtime.core.component.child.ModulesListChild
import ru.bartwell.kick.runtime.core.component.child.StubChild
import ru.bartwell.kick.runtime.feature.list.presentation.ModulesListContent
import ru.bartwell.kick.runtime.feature.list.presentation.screenCloser
import ru.bartwell.kick.runtime.feature.stub.presentation.StubContent

@Composable
internal fun RootContent(
    component: RootComponent,
    modifier: Modifier = Modifier,
) {
    val environment = AppUiEnvironment(screenCloser = screenCloser())
    CompositionLocalProvider(LocalAppUiEnvironment provides environment) {
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
            } else if (child is StubChild) {
                StubContent(
                    component = child.component,
                    modifier = Modifier.fillMaxSize(),
                )
            } else {
                component.currentModule?.Content(it.instance)
            }
        }
    }
}

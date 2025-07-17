package ru.bartwell.kick.core.data

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.StackNavigation
import kotlinx.serialization.modules.PolymorphicModuleBuilder
import ru.bartwell.kick.core.component.Child
import ru.bartwell.kick.core.component.Config

public interface Module {

    public val description: ModuleDescription
    public val startConfig: Config

    public fun getComponent(
        componentContext: ComponentContext,
        nav: StackNavigation<Config>,
        config: Config
    ): Child<*>?

    @Composable
    public fun Content(instance: Child<*>)

    public fun registerSubclasses(builder: PolymorphicModuleBuilder<Config>)
}

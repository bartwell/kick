package ru.bartwell.kick.module.overlay

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.StackNavigation
import kotlinx.serialization.modules.PolymorphicModuleBuilder
import ru.bartwell.kick.core.component.Child
import ru.bartwell.kick.core.component.Config
import ru.bartwell.kick.core.component.StubConfig
import ru.bartwell.kick.core.data.Module
import ru.bartwell.kick.core.data.ModuleDescription
import ru.bartwell.kick.core.data.PlatformContext
import ru.bartwell.kick.module.overlay.core.provider.OverlayProvider

@Suppress("UnusedPrivateProperty", "EmptyFunctionBlock", "UNUSED_PARAMETER")
public class OverlayModule(
    context: PlatformContext,
    providers: List<OverlayProvider> = emptyList(),
) : Module {
    override val description: ModuleDescription = ModuleDescription.OVERLAY
    override val startConfig: Config = StubConfig(description)

    override fun getComponent(
        componentContext: ComponentContext,
        nav: StackNavigation<Config>,
        config: Config,
    ): Child<*>? = null

    @Composable
    override fun Content(instance: Child<*>) {}

    override fun registerSubclasses(builder: PolymorphicModuleBuilder<Config>) {}
}

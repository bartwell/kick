package ru.bartwell.kick.module.ktor3

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
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours

@Suppress("UnusedPrivateProperty", "EmptyFunctionBlock", "unused")
public class Ktor3Module(
    platformContext: PlatformContext,
    private val expireDelay: Duration = 1.hours,
) : Module {

    override val description: ModuleDescription = ModuleDescription.KTOR3
    override val startConfig: Config = StubConfig

    override fun getComponent(
        componentContext: ComponentContext,
        nav: StackNavigation<Config>,
        config: Config,
    ): Child<*>? = null

    @Composable
    override fun Content(instance: Child<*>) {
    }

    override fun registerSubclasses(builder: PolymorphicModuleBuilder<Config>) {}
}

package ru.bartwell.kick.module.logging

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
import ru.bartwell.kick.module.logging.feature.table.util.LabelExtractor
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours

@Suppress("UnusedPrivateProperty", "EmptyFunctionBlock", "unused")
public class LoggingModule(
    platformContext: PlatformContext,
    private val expireDelay: Duration = 1.hours,
    private val labelExtractor: LabelExtractor? = null,
) : Module {

    override val description: ModuleDescription = ModuleDescription.LOGGING
    override val startConfig: Config = StubConfig(description)

    // Secondary constructors should be declared before methods
    public constructor(platformContext: PlatformContext) : this(
        platformContext = platformContext,
        expireDelay = 1.hours,
        labelExtractor = null,
    )

    public constructor(platformContext: PlatformContext, expireDelay: Duration) : this(
        platformContext = platformContext,
        expireDelay = expireDelay,
        labelExtractor = null,
    )

    public constructor(platformContext: PlatformContext, labelExtractor: LabelExtractor?) : this(
        platformContext = platformContext,
        expireDelay = 1.hours,
        labelExtractor = labelExtractor,
    )

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

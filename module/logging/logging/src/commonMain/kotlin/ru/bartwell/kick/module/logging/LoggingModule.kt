package ru.bartwell.kick.module.logging

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.pop
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.modules.PolymorphicModuleBuilder
import ru.bartwell.kick.core.component.Child
import ru.bartwell.kick.core.component.Config
import ru.bartwell.kick.core.data.Module
import ru.bartwell.kick.core.data.ModuleDescription
import ru.bartwell.kick.core.data.PlatformContext
import ru.bartwell.kick.core.util.DateUtils
import ru.bartwell.kick.module.logging.core.component.child.LogViewerChild
import ru.bartwell.kick.module.logging.core.component.config.LogViewerConfig
import ru.bartwell.kick.module.logging.core.persist.DatabaseBuilder
import ru.bartwell.kick.module.logging.core.util.DatabaseHolder
import ru.bartwell.kick.module.logging.feature.table.presentation.DefaultLogViewerComponent
import ru.bartwell.kick.module.logging.feature.table.presentation.LogViewerContent
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours

public class LoggingModule(
    platformContext: PlatformContext,
    private val expireDelay: Duration = 1.hours
) : Module {

    override val description: ModuleDescription = ModuleDescription.LOGGING
    override val startConfig: Config = LogViewerConfig
    private val database = DatabaseBuilder().createDatabase(platformContext)

    init {
        DatabaseHolder.database = database
        CoroutineScope(Dispatchers.Default).launch {
            database.getLogDao()
                .deleteOld(DateUtils.currentTimeMillis() - expireDelay.inWholeMilliseconds)
        }
    }

    override fun getComponent(
        componentContext: ComponentContext,
        nav: StackNavigation<Config>,
        config: Config,
    ): Child<*>? = if (config == LogViewerConfig) {
        LogViewerChild(
            DefaultLogViewerComponent(
                componentContext = componentContext,
                database = database,
                onFinished = { nav.pop() },
            )
        )
    } else {
        null
    }

    @Composable
    override fun Content(instance: Child<*>) {
        when (val child = instance) {
            is LogViewerChild -> LogViewerContent(
                component = child.component,
                modifier = Modifier.Companion.fillMaxSize(),
            )
        }
    }

    override fun registerSubclasses(builder: PolymorphicModuleBuilder<Config>) {
        builder.subclass(LogViewerConfig::class, LogViewerConfig.serializer())
    }
}

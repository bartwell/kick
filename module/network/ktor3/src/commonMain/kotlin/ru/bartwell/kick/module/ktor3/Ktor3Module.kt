package ru.bartwell.kick.module.ktor3

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.pushNew
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import kotlinx.serialization.modules.PolymorphicModuleBuilder
import ru.bartwell.kick.core.component.Child
import ru.bartwell.kick.core.component.Config
import ru.bartwell.kick.core.component.config.RequestDetailsConfig
import ru.bartwell.kick.core.component.config.RequestsListConfig
import ru.bartwell.kick.core.data.Module
import ru.bartwell.kick.core.data.ModuleDescription
import ru.bartwell.kick.core.data.PlatformContext
import ru.bartwell.kick.core.util.DateUtils
import ru.bartwell.kick.module.ktor3.core.component.child.RequestDetailsChild
import ru.bartwell.kick.module.ktor3.core.component.child.RequestsListChild
import ru.bartwell.kick.module.ktor3.core.persist.DatabaseBuilder
import ru.bartwell.kick.module.ktor3.core.util.DatabaseHolder
import ru.bartwell.kick.module.ktor3.feature.detail.presentation.DefaultRequestDetailsComponent
import ru.bartwell.kick.module.ktor3.feature.detail.presentation.RequestDetailsContent
import ru.bartwell.kick.module.ktor3.feature.list.presentation.DefaultRequestsListComponent
import ru.bartwell.kick.module.ktor3.feature.list.presentation.RequestsListContent
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours

public class Ktor3Module(
    platformContext: PlatformContext,
    private val expireDelay: Duration = 1.hours,
) : Module {

    override val description: ModuleDescription = ModuleDescription.KTOR3
    override val startConfig: Config = RequestsListConfig
    private val database = DatabaseBuilder().createBuilder(platformContext)
        .fallbackToDestructiveMigration(dropAllTables = true)
        .setDriver(BundledSQLiteDriver())
        .setQueryCoroutineContext(Dispatchers.Default)
        .build()

    init {
        DatabaseHolder.database = database
        CoroutineScope(Dispatchers.IO).launch {
            database.getRequestDao().deleteOld(
                DateUtils.currentTimeMillis() - expireDelay.inWholeMilliseconds
            )
        }
    }

    override fun getComponent(
        componentContext: ComponentContext,
        nav: StackNavigation<Config>,
        config: Config,
    ): Child<*>? = when (config) {
        is RequestsListConfig -> RequestsListChild(
            DefaultRequestsListComponent(
                componentContext = componentContext,
                database = database,
                onFinished = { nav.pop() },
                onRequestClick = { requestId -> nav.pushNew(RequestDetailsConfig(requestId)) },
            )
        )

        is RequestDetailsConfig -> RequestDetailsChild(
            DefaultRequestDetailsComponent(
                componentContext = componentContext,
                database = database,
                requestId = config.requestId,
                onFinished = { nav.pop() },
            )
        )

        else -> null
    }

    @Composable
    override fun Content(instance: Child<*>) {
        when (val child = instance) {
            is RequestsListChild -> RequestsListContent(
                component = child.component,
                modifier = Modifier.Companion.fillMaxSize(),
            )

            is RequestDetailsChild -> RequestDetailsContent(
                component = child.component,
                modifier = Modifier.Companion.fillMaxSize(),
            )
        }
    }

    override fun registerSubclasses(builder: PolymorphicModuleBuilder<Config>) {
        builder.subclass(RequestDetailsConfig::class, RequestDetailsConfig.serializer())
        builder.subclass(RequestsListConfig::class, RequestsListConfig.serializer())
    }
}

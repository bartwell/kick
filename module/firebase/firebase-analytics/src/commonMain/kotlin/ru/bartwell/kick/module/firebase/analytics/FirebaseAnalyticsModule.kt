package ru.bartwell.kick.module.firebase.analytics

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.pushNew
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.modules.PolymorphicModuleBuilder
import ru.bartwell.kick.core.component.Child
import ru.bartwell.kick.core.component.Config
import ru.bartwell.kick.core.data.Module
import ru.bartwell.kick.core.data.ModuleDescription
import ru.bartwell.kick.core.data.PlatformContext
import ru.bartwell.kick.module.firebase.analytics.core.component.child.FirebaseAnalyticsChild
import ru.bartwell.kick.module.firebase.analytics.core.component.child.FirebaseAnalyticsPropertiesChild
import ru.bartwell.kick.module.firebase.analytics.core.component.config.FirebaseAnalyticsConfig
import ru.bartwell.kick.module.firebase.analytics.core.component.config.FirebaseAnalyticsPropertiesConfig
import ru.bartwell.kick.module.firebase.analytics.core.overlay.FirebaseFloatingWindowHost
import ru.bartwell.kick.module.firebase.analytics.core.persist.DatabaseBuilder
import ru.bartwell.kick.module.firebase.analytics.core.persist.FirebaseAnalyticsDatabase
import ru.bartwell.kick.module.firebase.analytics.core.persist.FirebaseFloatingWindowSettings
import ru.bartwell.kick.module.firebase.analytics.core.util.DatabaseHolder
import ru.bartwell.kick.module.firebase.analytics.core.util.FirebaseFloatingWindowState
import ru.bartwell.kick.module.firebase.analytics.feature.main.presentation.DefaultFirebaseAnalyticsComponent
import ru.bartwell.kick.module.firebase.analytics.feature.main.presentation.FirebaseAnalyticsContent
import ru.bartwell.kick.module.firebase.analytics.feature.properties.presentation.DefaultFirebaseAnalyticsPropertiesComponent
import ru.bartwell.kick.module.firebase.analytics.feature.properties.presentation.FirebaseAnalyticsPropertiesContent

public class FirebaseAnalyticsModule(
    platformContext: PlatformContext,
) : Module {

    override val description: ModuleDescription = ModuleDescription.FIREBASE_ANALYTICS
    override val startConfig: Config = FirebaseAnalyticsConfig
    private val database: FirebaseAnalyticsDatabase = DatabaseBuilder().createDatabase(platformContext)

    init {
        FirebaseFloatingWindowSettings(platformContext)
        FirebaseFloatingWindowHost.init(platformContext)
        FirebaseFloatingWindowState.initialize()
        DatabaseHolder.database = database
        CoroutineScope(Dispatchers.Default).launch {
            database.getPropertyDao().deleteAll()
        }
    }

    override fun getComponent(
        componentContext: ComponentContext,
        nav: StackNavigation<Config>,
        config: Config,
    ): Child<*>? = when (config) {
        FirebaseAnalyticsConfig -> FirebaseAnalyticsChild(
            DefaultFirebaseAnalyticsComponent(
                componentContext = componentContext,
                database = database,
                onFinished = { nav.pop() },
                onPropertiesClickCallback = { nav.pushNew(FirebaseAnalyticsPropertiesConfig) },
            )
        )

        FirebaseAnalyticsPropertiesConfig -> FirebaseAnalyticsPropertiesChild(
            DefaultFirebaseAnalyticsPropertiesComponent(
                componentContext = componentContext,
                database = database,
                onFinished = { nav.pop() },
            )
        )

        else -> null
    }

    @Composable
    override fun Content(instance: Child<*>) {
        when (val child = instance) {
            is FirebaseAnalyticsChild -> FirebaseAnalyticsContent(
                component = child.component,
                modifier = Modifier.fillMaxSize(),
            )

            is FirebaseAnalyticsPropertiesChild -> FirebaseAnalyticsPropertiesContent(
                component = child.component,
                modifier = Modifier.fillMaxSize(),
            )
        }
    }

    override fun registerSubclasses(builder: PolymorphicModuleBuilder<Config>) {
        builder.subclass(FirebaseAnalyticsConfig::class, FirebaseAnalyticsConfig.serializer())
        builder.subclass(FirebaseAnalyticsPropertiesConfig::class, FirebaseAnalyticsPropertiesConfig.serializer())
    }
}

package ru.bartwell.kick.module.firebase.cloudmessaging

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.pushNew
import kotlinx.serialization.modules.PolymorphicModuleBuilder
import ru.bartwell.kick.core.component.Child
import ru.bartwell.kick.core.component.Config
import ru.bartwell.kick.core.data.Module
import ru.bartwell.kick.core.data.ModuleDescription
import ru.bartwell.kick.core.data.PlatformContext
import ru.bartwell.kick.module.firebase.cloudmessaging.core.component.child.FirebaseCloudMessagingChild
import ru.bartwell.kick.module.firebase.cloudmessaging.core.component.child.FirebaseCloudMessagingDetailsChild
import ru.bartwell.kick.module.firebase.cloudmessaging.core.component.child.FirebaseCloudMessagingHistoryChild
import ru.bartwell.kick.module.firebase.cloudmessaging.core.component.config.FirebaseCloudMessagingConfig
import ru.bartwell.kick.module.firebase.cloudmessaging.core.component.config.FirebaseCloudMessagingDetailsConfig
import ru.bartwell.kick.module.firebase.cloudmessaging.core.component.config.FirebaseCloudMessagingHistoryConfig
import ru.bartwell.kick.module.firebase.cloudmessaging.core.persist.DatabaseBuilder
import ru.bartwell.kick.module.firebase.cloudmessaging.core.persist.FirebaseCloudMessagingDatabase
import ru.bartwell.kick.module.firebase.cloudmessaging.core.util.DatabaseHolder
import ru.bartwell.kick.module.firebase.cloudmessaging.feature.detail.presentation.DefaultFirebaseCloudMessagingDetailsComponent
import ru.bartwell.kick.module.firebase.cloudmessaging.feature.detail.presentation.FirebaseCloudMessagingDetailsContent
import ru.bartwell.kick.module.firebase.cloudmessaging.feature.history.presentation.DefaultFirebaseCloudMessagingHistoryComponent
import ru.bartwell.kick.module.firebase.cloudmessaging.feature.history.presentation.FirebaseCloudMessagingHistoryContent
import ru.bartwell.kick.module.firebase.cloudmessaging.feature.main.presentation.DefaultFirebaseCloudMessagingComponent
import ru.bartwell.kick.module.firebase.cloudmessaging.feature.main.presentation.FirebaseCloudMessagingContent

public class FirebaseCloudMessagingModule(
    context: PlatformContext,
) : Module {

    override val description: ModuleDescription = ModuleDescription.FIREBASE_CLOUD_MESSAGING
    override val startConfig: Config = FirebaseCloudMessagingConfig
    private val database: FirebaseCloudMessagingDatabase = DatabaseBuilder().createDatabase(context)

    init {
        DatabaseHolder.database = database
    }

    override fun getComponent(
        componentContext: ComponentContext,
        nav: StackNavigation<Config>,
        config: Config,
    ): Child<*>? = when (config) {
        FirebaseCloudMessagingConfig -> FirebaseCloudMessagingChild(
            DefaultFirebaseCloudMessagingComponent(
                componentContext = componentContext,
                onFinished = { nav.pop() },
                onHistoryClickCallback = { nav.pushNew(FirebaseCloudMessagingHistoryConfig) },
            )
        )

        FirebaseCloudMessagingHistoryConfig -> FirebaseCloudMessagingHistoryChild(
            DefaultFirebaseCloudMessagingHistoryComponent(
                componentContext = componentContext,
                database = database,
                onFinished = { nav.pop() },
                onMessageClick = { message -> nav.pushNew(FirebaseCloudMessagingDetailsConfig(message)) },
            )
        )

        is FirebaseCloudMessagingDetailsConfig -> FirebaseCloudMessagingDetailsChild(
            DefaultFirebaseCloudMessagingDetailsComponent(
                componentContext = componentContext,
                message = config.message,
                onFinished = { nav.pop() },
            )
        )

        else -> null
    }

    @Composable
    override fun Content(instance: Child<*>) {
        when (val child = instance) {
            is FirebaseCloudMessagingChild -> FirebaseCloudMessagingContent(
                component = child.component,
                modifier = Modifier.fillMaxSize(),
            )
            is FirebaseCloudMessagingHistoryChild -> FirebaseCloudMessagingHistoryContent(
                component = child.component,
                modifier = Modifier.fillMaxSize(),
            )
            is FirebaseCloudMessagingDetailsChild -> FirebaseCloudMessagingDetailsContent(
                component = child.component,
                modifier = Modifier.fillMaxSize(),
            )
        }
    }

    override fun registerSubclasses(builder: PolymorphicModuleBuilder<Config>) {
        builder.subclass(FirebaseCloudMessagingConfig::class, FirebaseCloudMessagingConfig.serializer())
        builder.subclass(FirebaseCloudMessagingHistoryConfig::class, FirebaseCloudMessagingHistoryConfig.serializer())
        builder.subclass(FirebaseCloudMessagingDetailsConfig::class, FirebaseCloudMessagingDetailsConfig.serializer())
    }
}

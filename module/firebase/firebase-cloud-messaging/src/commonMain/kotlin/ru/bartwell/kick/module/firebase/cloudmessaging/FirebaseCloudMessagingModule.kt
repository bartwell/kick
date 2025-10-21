package ru.bartwell.kick.module.firebase.cloudmessaging

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.pop
import kotlinx.serialization.modules.PolymorphicModuleBuilder
import ru.bartwell.kick.core.component.Child
import ru.bartwell.kick.core.component.Config
import ru.bartwell.kick.core.data.Module
import ru.bartwell.kick.core.data.ModuleDescription
import ru.bartwell.kick.core.data.PlatformContext
import ru.bartwell.kick.module.firebase.cloudmessaging.core.component.child.FirebaseCloudMessagingChild
import ru.bartwell.kick.module.firebase.cloudmessaging.core.component.config.FirebaseCloudMessagingConfig
import ru.bartwell.kick.module.firebase.cloudmessaging.feature.presentation.DefaultFirebaseCloudMessagingComponent
import ru.bartwell.kick.module.firebase.cloudmessaging.feature.presentation.FirebaseCloudMessagingComponent
import ru.bartwell.kick.module.firebase.cloudmessaging.feature.presentation.FirebaseCloudMessagingContent

public class FirebaseCloudMessagingModule(
    @Suppress("UNUSED_PARAMETER")
    context: PlatformContext,
) : Module {

    override val description: ModuleDescription = ModuleDescription.FIREBASE_CLOUD_MESSAGING
    override val startConfig: Config = FirebaseCloudMessagingConfig

    override fun getComponent(
        componentContext: ComponentContext,
        nav: StackNavigation<Config>,
        config: Config,
    ): Child<*>? = if (config == FirebaseCloudMessagingConfig) {
        FirebaseCloudMessagingChild(
            DefaultFirebaseCloudMessagingComponent(
                componentContext = componentContext,
                onFinished = { nav.pop() },
            )
        )
    } else {
        null
    }

    @Composable
    override fun Content(instance: Child<*>) {
        when (val child = instance) {
            is FirebaseCloudMessagingChild -> FirebaseCloudMessagingContent(
                component = child.component,
                modifier = Modifier.fillMaxSize(),
            )
        }
    }

    override fun registerSubclasses(builder: PolymorphicModuleBuilder<Config>) {
        builder.subclass(FirebaseCloudMessagingConfig::class, FirebaseCloudMessagingConfig.serializer())
    }
}

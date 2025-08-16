package ru.bartwell.kick.module.layout

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.pushNew
import kotlinx.serialization.modules.PolymorphicModuleBuilder
import ru.bartwell.kick.Kick
import ru.bartwell.kick.core.component.Child
import ru.bartwell.kick.core.component.Config
import ru.bartwell.kick.core.data.Module
import ru.bartwell.kick.core.data.ModuleDescription
import ru.bartwell.kick.core.data.PlatformContext
import ru.bartwell.kick.core.data.StartScreen
import ru.bartwell.kick.module.layout.core.component.child.LayoutChild
import ru.bartwell.kick.module.layout.core.component.child.LayoutHierarchyChild
import ru.bartwell.kick.module.layout.core.component.child.LayoutPropertiesChild
import ru.bartwell.kick.module.layout.core.component.config.LayoutConfig
import ru.bartwell.kick.module.layout.core.component.config.LayoutHierarchyConfig
import ru.bartwell.kick.module.layout.core.component.config.LayoutPropertiesConfig
import ru.bartwell.kick.module.layout.core.introspector.DefaultLayoutRepository
import ru.bartwell.kick.module.layout.core.introspector.LayoutRepository
import ru.bartwell.kick.module.layout.core.persists.LayoutSettings
import ru.bartwell.kick.module.layout.core.trigger.LayoutTriggerController
import ru.bartwell.kick.module.layout.feature.hierarchy.presentation.DefaultLayoutHierarchyComponent
import ru.bartwell.kick.module.layout.feature.hierarchy.presentation.LayoutHierarchyContent
import ru.bartwell.kick.module.layout.feature.properties.presentation.DefaultLayoutPropertiesComponent
import ru.bartwell.kick.module.layout.feature.properties.presentation.LayoutPropertiesContent
import ru.bartwell.kick.module.layout.feature.settings.presentation.DefaultLayoutComponent
import ru.bartwell.kick.module.layout.feature.settings.presentation.LayoutContent

public class LayoutModule(private val context: PlatformContext) : Module {
    override val description: ModuleDescription = ModuleDescription.LAYOUT
    override val startConfig: Config = LayoutConfig

    // ⬇️ Один общий репозиторий на модуль
    private val repository: LayoutRepository = DefaultLayoutRepository()

    private val triggerController = LayoutTriggerController(context) {
        Kick.launch(
            context = context,
            startScreen = StartScreen(LayoutHierarchyConfig, ModuleDescription.LAYOUT),
        )
    }

    init {
        LayoutSettings(context)
        if (LayoutSettings.isEnabled()) {
            triggerController.start(true)
        }
    }

    override fun getComponent(
        componentContext: ComponentContext,
        nav: StackNavigation<Config>,
        config: Config,
    ): Child<*>? {
        return when (config) {
            LayoutConfig -> LayoutChild(
                DefaultLayoutComponent(
                    componentContext = componentContext,
                    onEnabledChangeCallback = { _, enabled ->
                        if (enabled) triggerController.start(true) else triggerController.stop()
                    },
                    onBackCallback = { nav.pop() },
                )
            )
            LayoutHierarchyConfig -> LayoutHierarchyChild(
                DefaultLayoutHierarchyComponent(
                    componentContext = componentContext,
                    repository = repository, // ⬅️ общий
                    onNodeSelectedCallback = { id -> nav.pushNew(LayoutPropertiesConfig(id)) },
                )
            )
            is LayoutPropertiesConfig -> LayoutPropertiesChild(
                DefaultLayoutPropertiesComponent(
                    componentContext = componentContext,
                    nodeId = config.nodeId,
                    repository = repository, // ⬅️ общий
                    onFinished = { nav.pop() },
                )
            )
            else -> null
        }
    }

    @Composable
    override fun Content(instance: Child<*>) {
        when (val child = instance) {
            is LayoutChild -> LayoutContent(child.component, Modifier.fillMaxSize())
            is LayoutHierarchyChild -> LayoutHierarchyContent(child.component, Modifier.fillMaxSize())
            is LayoutPropertiesChild -> LayoutPropertiesContent(child.component, Modifier.fillMaxSize())
        }
    }

    override fun registerSubclasses(builder: PolymorphicModuleBuilder<Config>) {
        builder.subclass(LayoutConfig::class, LayoutConfig.serializer())
        builder.subclass(LayoutHierarchyConfig::class, LayoutHierarchyConfig.serializer())
        builder.subclass(LayoutPropertiesConfig::class, LayoutPropertiesConfig.serializer())
    }
}

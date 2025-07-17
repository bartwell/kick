package ru.bartwell.kick.runtime.core.component

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pushNew
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.statekeeper.ExperimentalStateKeeperApi
import com.arkivanov.essenty.statekeeper.polymorphicSerializer
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import ru.bartwell.kick.core.component.Child
import ru.bartwell.kick.core.component.Config
import ru.bartwell.kick.core.component.Resumable
import ru.bartwell.kick.core.component.RootComponent
import ru.bartwell.kick.core.data.Module
import ru.bartwell.kick.runtime.core.component.child.ModulesListChild
import ru.bartwell.kick.runtime.core.component.config.ModulesListConfig
import ru.bartwell.kick.runtime.feature.table.presentation.DefaultModulesListComponent

internal class DefaultRootComponent(
    componentContext: ComponentContext,
    private val modules: List<Module>,
) : RootComponent, ComponentContext by componentContext {

    private val nav = StackNavigation<Config>()
    override var currentModule: Module? = null

    private val configModule = SerializersModule {
        polymorphic(Config::class) {
            subclass(ModulesListConfig::class, ModulesListConfig.serializer())
            modules.forEach { module ->
                module.registerSubclasses(this)
            }
        }
    }

    @OptIn(ExperimentalSerializationApi::class, ExperimentalStateKeeperApi::class)
    override val stack: Value<ChildStack<*, Child<*>>> = childStack(
        source = nav,
        serializer = polymorphicSerializer(configModule),
        initialConfiguration = ModulesListConfig,
        handleBackButton = true,
        childFactory = ::child,
    )

    init {
        stack.subscribe { childStack ->
            val component = childStack.active.instance.component
            if (component is Resumable) {
                component.onResume()
            }
        }
    }

    private fun child(
        config: Config,
        componentContext: ComponentContext
    ): Child<*> {
        if (config == ModulesListConfig) {
            return ModulesListChild(
                DefaultModulesListComponent(
                    componentContext = componentContext,
                    listItemClicked = { description ->
                        modules.firstOrNull { module -> module.description == description }
                            ?.let { module ->
                                currentModule = module
                                nav.pushNew(module.startConfig)
                            }
                    }
                )
            )
        } else {
            val component = currentModule?.getComponent(componentContext = componentContext, nav = nav, config = config)
            if (component != null) {
                return component
            }
            error("Unknown config")
        }
    }
}

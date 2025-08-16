package ru.bartwell.kick.runtime.core.component

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
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
import ru.bartwell.kick.core.component.StubConfig
import ru.bartwell.kick.core.data.Module
import ru.bartwell.kick.core.data.StartScreen
import ru.bartwell.kick.runtime.core.component.child.ModulesListChild
import ru.bartwell.kick.runtime.core.component.child.StubChild
import ru.bartwell.kick.runtime.core.component.config.ModulesListConfig
import ru.bartwell.kick.runtime.feature.list.presentation.DefaultModulesListComponent
import ru.bartwell.kick.runtime.feature.stub.presentation.DefaultStubComponent

internal class DefaultRootComponent(
    componentContext: ComponentContext,
    private val modules: List<Module>,
    private val startScreen: StartScreen? = null,
) : RootComponent, ComponentContext by componentContext {

    private val nav = StackNavigation<Config>()
    override var currentModule: Module? = startScreen?.moduleDescription?.let { desc ->
        modules.firstOrNull { module -> module.description == desc }
    }

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
        initialConfiguration = startScreen?.config ?: ModulesListConfig,
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
        return when (config) {
            ModulesListConfig -> {
                ModulesListChild(
                    DefaultModulesListComponent(
                        componentContext = componentContext,
                        modules = modules,
                        listItemClicked = { description ->
                            modules.firstOrNull { module -> module.description == description }
                                ?.let { module ->
                                    currentModule = module
                                    nav.pushNew(module.startConfig)
                                }
                                ?: run {
                                    nav.pushNew(StubConfig(description))
                                }
                        }
                    )
                )
            }

            is StubConfig -> {
                StubChild(
                    component = DefaultStubComponent(
                        componentContext = componentContext,
                        onFinished = { nav.pop() },
                        moduleDescription = config.moduleDescription,
                    )
                )
            }

            else -> {
                val component = currentModule?.getComponent(
                    componentContext = componentContext,
                    nav = nav,
                    config = config,
                )
                component ?: error("Unknown config")
            }
        }
    }
}

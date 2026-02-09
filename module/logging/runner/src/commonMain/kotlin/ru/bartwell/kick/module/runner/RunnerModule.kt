package ru.bartwell.kick.module.runner

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
import ru.bartwell.kick.module.runner.core.component.child.RunnerListChild
import ru.bartwell.kick.module.runner.core.component.child.RunnerParamsChild
import ru.bartwell.kick.module.runner.core.component.child.RunnerResultChild
import ru.bartwell.kick.module.runner.core.component.config.RunnerListConfig
import ru.bartwell.kick.module.runner.core.component.config.RunnerParamsConfig
import ru.bartwell.kick.module.runner.core.component.config.RunnerResultConfig
import ru.bartwell.kick.module.runner.feature.list.presentation.DefaultRunnerListComponent
import ru.bartwell.kick.module.runner.feature.list.presentation.RunnerListContent
import ru.bartwell.kick.module.runner.feature.params.presentation.DefaultRunnerParamsComponent
import ru.bartwell.kick.module.runner.feature.params.presentation.RunnerParamsContent
import ru.bartwell.kick.module.runner.feature.result.presentation.DefaultRunnerResultComponent
import ru.bartwell.kick.module.runner.feature.result.presentation.RunnerResultContent

public class RunnerModule : Module {

    override val description: ModuleDescription = ModuleDescription.RUNNER
    override val startConfig: Config = RunnerListConfig

    override fun getComponent(
        componentContext: ComponentContext,
        nav: StackNavigation<Config>,
        config: Config,
    ): Child<*>? = when (config) {
        is RunnerListConfig -> RunnerListChild(
            DefaultRunnerListComponent(
                componentContext = componentContext,
                onFinished = { nav.pop() },
                onCallReady = { callId -> nav.pushNew(RunnerResultConfig(callId)) },
                onCallRequiresParams = { callId -> nav.pushNew(RunnerParamsConfig(callId)) },
            )
        )

        is RunnerParamsConfig -> RunnerParamsChild(
            DefaultRunnerParamsComponent(
                componentContext = componentContext,
                callId = config.callId,
                onFinished = { nav.pop() },
                onReadyToShowResult = { callId -> nav.pushNew(RunnerResultConfig(callId)) },
            )
        )

        is RunnerResultConfig -> RunnerResultChild(
            DefaultRunnerResultComponent(
                componentContext = componentContext,
                callId = config.callId,
                onFinished = { nav.pop() },
            )
        )

        else -> null
    }

    @Composable
    override fun Content(instance: Child<*>) {
        when (val child = instance) {
            is RunnerListChild -> RunnerListContent(
                component = child.component,
                modifier = Modifier.fillMaxSize(),
            )

            is RunnerParamsChild -> RunnerParamsContent(
                component = child.component,
                modifier = Modifier.fillMaxSize(),
            )

            is RunnerResultChild -> RunnerResultContent(
                component = child.component,
                modifier = Modifier.fillMaxSize(),
            )
        }
    }

    override fun registerSubclasses(builder: PolymorphicModuleBuilder<Config>) {
        builder.subclass(RunnerListConfig::class, RunnerListConfig.serializer())
        builder.subclass(RunnerParamsConfig::class, RunnerParamsConfig.serializer())
        builder.subclass(RunnerResultConfig::class, RunnerResultConfig.serializer())
    }
}

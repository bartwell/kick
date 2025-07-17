package ru.bartwell.kick.module.sqlite.runtime

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.StackNavigation
import kotlinx.serialization.modules.PolymorphicModuleBuilder
import ru.bartwell.kick.core.DatabaseWrapper
import ru.bartwell.kick.core.component.Child
import ru.bartwell.kick.core.component.Config
import ru.bartwell.kick.core.component.StubConfig
import ru.bartwell.kick.core.data.Module
import ru.bartwell.kick.core.data.ModuleDescription

@Suppress("UnusedPrivateProperty", "EmptyFunctionBlock", "unused")
public class SqliteModule(databaseWrapper: DatabaseWrapper) : Module {

    override val description: ModuleDescription = when (databaseWrapper.type) {
        DatabaseWrapper.Type.SQL_DELIGHT -> ModuleDescription.SQL_DELIGHT
        DatabaseWrapper.Type.ROOM -> ModuleDescription.ROOM
    }
    override val startConfig: Config = StubConfig

    override fun getComponent(
        componentContext: ComponentContext,
        nav: StackNavigation<Config>,
        config: Config,
    ): Child<*>? = null

    @Composable
    override fun Content(instance: Child<*>) {}

    override fun registerSubclasses(builder: PolymorphicModuleBuilder<Config>) {}
}

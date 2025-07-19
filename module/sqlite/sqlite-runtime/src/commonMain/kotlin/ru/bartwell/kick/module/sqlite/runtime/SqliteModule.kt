package ru.bartwell.kick.module.sqlite.runtime

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
import ru.bartwell.kick.module.sqlite.core.DatabaseWrapper
import ru.bartwell.kick.module.sqlite.core.data.Column
import ru.bartwell.kick.module.sqlite.runtime.core.component.child.InsertChild
import ru.bartwell.kick.module.sqlite.runtime.core.component.child.QueryChild
import ru.bartwell.kick.module.sqlite.runtime.core.component.child.StructureChild
import ru.bartwell.kick.module.sqlite.runtime.core.component.child.TablesListChild
import ru.bartwell.kick.module.sqlite.runtime.core.component.child.UpdateChild
import ru.bartwell.kick.module.sqlite.runtime.core.component.child.ViewerChild
import ru.bartwell.kick.module.sqlite.runtime.core.component.config.InsertConfig
import ru.bartwell.kick.module.sqlite.runtime.core.component.config.QueryConfig
import ru.bartwell.kick.module.sqlite.runtime.core.component.config.StructureConfig
import ru.bartwell.kick.module.sqlite.runtime.core.component.config.TablesListConfig
import ru.bartwell.kick.module.sqlite.runtime.core.component.config.UpdateConfig
import ru.bartwell.kick.module.sqlite.runtime.core.component.config.ViewerConfig
import ru.bartwell.kick.module.sqlite.runtime.feature.insert.presentation.DefaultInsertComponent
import ru.bartwell.kick.module.sqlite.runtime.feature.insert.presentation.InsertContent
import ru.bartwell.kick.module.sqlite.runtime.feature.query.presentation.DefaultQueryComponent
import ru.bartwell.kick.module.sqlite.runtime.feature.query.presentation.QueryContent
import ru.bartwell.kick.module.sqlite.runtime.feature.structure.presentation.DefaultStructureComponent
import ru.bartwell.kick.module.sqlite.runtime.feature.structure.presentation.StructureContent
import ru.bartwell.kick.module.sqlite.runtime.feature.table.presentation.DefaultTablesListComponent
import ru.bartwell.kick.module.sqlite.runtime.feature.table.presentation.TablesListContent
import ru.bartwell.kick.module.sqlite.runtime.feature.update.presentation.DefaultUpdateComponent
import ru.bartwell.kick.module.sqlite.runtime.feature.update.presentation.UpdateContent
import ru.bartwell.kick.module.sqlite.runtime.feature.viewer.presentation.DefaultViewerComponent
import ru.bartwell.kick.module.sqlite.runtime.feature.viewer.presentation.ViewerComponentCallback
import ru.bartwell.kick.module.sqlite.runtime.feature.viewer.presentation.ViewerContent

public class SqliteModule(private val databaseWrapper: DatabaseWrapper) : Module {

    override val description: ModuleDescription = when (databaseWrapper.type) {
        DatabaseWrapper.Type.SQL_DELIGHT -> ModuleDescription.SQL_DELIGHT
        DatabaseWrapper.Type.ROOM -> ModuleDescription.ROOM
    }
    override val startConfig: Config = TablesListConfig

    override fun getComponent(
        componentContext: ComponentContext,
        nav: StackNavigation<Config>,
        config: Config,
    ): Child<*>? = when (config) {
        TablesListConfig -> TablesListChild(
            DefaultTablesListComponent(
                componentContext = componentContext,
                databaseWrapper = databaseWrapper,
                onFinished = { nav.pop() },
                queryClicked = { nav.pushNew(QueryConfig) },
                listItemClicked = { table ->
                    nav.pushNew(ViewerConfig(table))
                }
            )
        )

        QueryConfig -> QueryChild(
            DefaultQueryComponent(
                componentContext = componentContext,
                databaseWrapper = databaseWrapper,
                onFinished = { nav.pop() },
            )
        )

        is ViewerConfig -> ViewerChild(
            DefaultViewerComponent(
                componentContext = componentContext,
                databaseWrapper = databaseWrapper,
                table = config.table,
                callback = object : ViewerComponentCallback {
                    override fun onFinished() = nav.pop()
                    override fun structureClick(table: String) = nav.pushNew(StructureConfig(table))
                    override fun cellClick(table: String, column: Column, rowId: Long) {
                        nav.pushNew(UpdateConfig(table, column, rowId))
                    }

                    override fun insertClick(table: String, columns: List<Column>) {
                        nav.pushNew(InsertConfig(table, columns))
                    }
                },
            )
        )

        is UpdateConfig -> UpdateChild(
            DefaultUpdateComponent(
                componentContext = componentContext,
                databaseWrapper = databaseWrapper,
                table = config.table,
                column = config.column,
                rowId = config.rowId,
                onFinished = { nav.pop() },
            )
        )

        is InsertConfig -> InsertChild(
            DefaultInsertComponent(
                componentContext = componentContext,
                databaseWrapper = databaseWrapper,
                table = config.table,
                columns = config.columns,
                onFinished = { nav.pop() },
            )
        )

        is StructureConfig -> StructureChild(
            DefaultStructureComponent(
                componentContext = componentContext,
                databaseWrapper = databaseWrapper,
                table = config.table,
                onFinished = { nav.pop() },
            )
        )

        else -> null
    }

    @Composable
    override fun Content(instance: Child<*>) {
        when (val child = instance) {
            is ViewerChild -> ViewerContent(
                component = child.component,
                modifier = Modifier.Companion.fillMaxSize(),
            )

            is TablesListChild -> TablesListContent(
                component = child.component,
                modifier = Modifier.Companion.fillMaxSize(),
            )

            is QueryChild -> QueryContent(
                component = child.component,
                modifier = Modifier.Companion.fillMaxSize(),
            )

            is UpdateChild -> UpdateContent(
                component = child.component,
                modifier = Modifier.Companion.fillMaxSize(),
            )

            is InsertChild -> InsertContent(
                component = child.component,
                modifier = Modifier.Companion.fillMaxSize(),
            )

            is StructureChild -> StructureContent(
                component = child.component,
                modifier = Modifier.Companion.fillMaxSize(),
            )
        }
    }

    override fun registerSubclasses(builder: PolymorphicModuleBuilder<Config>) {
        builder.subclass(InsertConfig::class, InsertConfig.serializer())
        builder.subclass(QueryConfig::class, QueryConfig.serializer())
        builder.subclass(StructureConfig::class, StructureConfig.serializer())
        builder.subclass(TablesListConfig::class, TablesListConfig.serializer())
        builder.subclass(UpdateConfig::class, UpdateConfig.serializer())
        builder.subclass(ViewerConfig::class, ViewerConfig.serializer())
    }
}

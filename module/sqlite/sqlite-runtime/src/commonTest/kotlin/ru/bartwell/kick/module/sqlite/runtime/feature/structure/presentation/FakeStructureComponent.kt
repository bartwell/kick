package ru.bartwell.kick.module.sqlite.runtime.feature.structure.presentation

import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import ru.bartwell.kick.module.sqlite.core.data.Column
import ru.bartwell.kick.module.sqlite.core.data.ColumnType

internal class FakeStructureComponent : StructureComponent {
    private val _model = MutableValue(
        StructureState(
            table = "items",
            columns = listOf(
                Column("_id", ColumnType.INTEGER, true, ""),
                Column("text", ColumnType.TEXT, false, null),
            )
        )
    )
    override val model: Value<StructureState> get() = _model
    override fun onBackPressed() = Unit
}

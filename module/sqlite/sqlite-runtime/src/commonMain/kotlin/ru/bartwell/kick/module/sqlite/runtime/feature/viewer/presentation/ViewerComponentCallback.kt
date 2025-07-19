package ru.bartwell.kick.module.sqlite.runtime.feature.viewer.presentation

import ru.bartwell.kick.module.sqlite.core.data.Column

internal interface ViewerComponentCallback {
    fun onFinished()
    fun cellClick(table: String, column: Column, rowId: Long)
    fun structureClick(table: String)
    fun insertClick(table: String, columns: List<Column>)
}

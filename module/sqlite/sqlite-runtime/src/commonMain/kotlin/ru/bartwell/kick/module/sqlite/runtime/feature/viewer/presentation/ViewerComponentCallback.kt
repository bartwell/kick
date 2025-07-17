package ru.bartwell.kick.feature.viewer.presentation

import ru.bartwell.kick.core.data.Column

internal interface ViewerComponentCallback {
    fun onFinished()
    fun cellClick(table: String, column: Column, rowId: Long)
    fun structureClick(table: String)
    fun insertClick(table: String, columns: List<Column>)
}

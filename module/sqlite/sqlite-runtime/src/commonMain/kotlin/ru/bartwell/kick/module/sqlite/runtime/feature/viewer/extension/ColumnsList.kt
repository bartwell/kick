package ru.bartwell.kick.feature.viewer.extension

import ru.bartwell.kick.core.data.Column

internal fun List<Column>.removeBuiltIn() = filter { !it.isRowId }

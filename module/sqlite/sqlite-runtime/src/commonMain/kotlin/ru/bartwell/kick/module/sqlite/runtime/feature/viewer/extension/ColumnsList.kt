package ru.bartwell.kick.module.sqlite.runtime.feature.viewer.extension

import ru.bartwell.kick.module.sqlite.core.data.Column

internal fun List<Column>.removeBuiltIn() = filter { !it.isRowId }

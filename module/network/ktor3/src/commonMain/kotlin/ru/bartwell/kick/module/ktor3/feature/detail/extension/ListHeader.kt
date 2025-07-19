package ru.bartwell.kick.module.ktor3.feature.detail.extension

import ru.bartwell.kick.module.ktor3.feature.list.data.Header

internal fun List<Header>.hasJsonContentType() = find { it.key.equals("Content-Type", ignoreCase = true) }
    ?.value
    ?.contains("application/json", ignoreCase = true) == true

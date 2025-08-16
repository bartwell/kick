package ru.bartwell.kick.module.layout.feature.properties.extension

import ru.bartwell.kick.core.data.PlatformContext

internal expect fun PlatformContext.copyToClipboard(text: String)

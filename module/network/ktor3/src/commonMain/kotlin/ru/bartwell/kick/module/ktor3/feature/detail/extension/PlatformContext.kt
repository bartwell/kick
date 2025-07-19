package ru.bartwell.kick.module.ktor3.feature.detail.extension

import ru.bartwell.kick.core.data.PlatformContext

internal expect fun PlatformContext.copyToClipboard(text: String)

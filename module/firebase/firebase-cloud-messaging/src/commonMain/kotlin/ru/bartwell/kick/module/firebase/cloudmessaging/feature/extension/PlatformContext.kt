package ru.bartwell.kick.module.firebase.cloudmessaging.feature.extension

import ru.bartwell.kick.core.data.PlatformContext

internal expect fun PlatformContext.copyToClipboard(label: String, text: String)

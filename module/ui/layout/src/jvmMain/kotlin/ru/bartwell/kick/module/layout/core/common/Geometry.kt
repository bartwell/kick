package ru.bartwell.kick.module.layout.core.common

import ru.bartwell.kick.module.layout.core.data.LayoutProperty
import java.awt.Color
import java.awt.Component
import java.awt.Dimension
import java.awt.Insets
import java.awt.Point

internal fun Component.safeLocationOnScreen(): Point =
    runCatching { locationOnScreen }.getOrNull() ?: Point(0, 0)

internal fun Class<*>.shortName(): String =
    simpleName.takeIf { it.isNotBlank() } ?: name.substringAfterLast('.')

internal fun Dimension?.toSizeString(): String =
    if (this == null) "n/a" else "${width}x$height"

internal fun Insets.toInsetsString(): String = "$left,$top,$right,$bottom"

internal fun Color.toHex(): String = "#%02X%02X%02X%02X".format(alpha, red, green, blue)

internal fun MutableList<LayoutProperty>.add(name: String, value: String) {
    this += LayoutProperty(name, value)
}

internal fun MutableList<LayoutProperty>.addIfNotEmpty(name: String, value: String?) {
    if (!value.isNullOrEmpty()) this += LayoutProperty(name, value)
}

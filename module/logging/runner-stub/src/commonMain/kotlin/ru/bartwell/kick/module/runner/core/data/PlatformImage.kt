package ru.bartwell.kick.module.runner.core.data

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter

public class PlatformImage internal constructor(
    internal val painterProvider: () -> Painter,
) {
    public companion object
}

@Composable
public fun PlatformImage.Content(modifier: Modifier = Modifier) {
    Image(
        painter = painterProvider(),
        contentDescription = null,
        modifier = modifier,
    )
}

public fun PlatformImage.Companion.fromImageBitmap(image: ImageBitmap?): PlatformImage? =
    image?.let { PlatformImage { BitmapPainter(it) } }

public fun PlatformImage.Companion.fromNative(native: Any?): PlatformImage? = native as? PlatformImage

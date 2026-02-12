package ru.bartwell.kick.module.runner.core.data

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap

public class PlatformImage internal constructor() {
    public companion object
}

@Composable
@Suppress("EmptyFunctionBlock", "UnusedParameter")
public fun PlatformImage.Content(modifier: Modifier = Modifier) {
}

@Suppress("UnusedParameter", "FunctionOnlyReturningConstant")
public fun PlatformImage.Companion.fromImageBitmap(image: ImageBitmap?): PlatformImage? = null

@Suppress("UnusedParameter", "FunctionOnlyReturningConstant")
public fun PlatformImage.Companion.fromNative(native: Any?): PlatformImage? = null

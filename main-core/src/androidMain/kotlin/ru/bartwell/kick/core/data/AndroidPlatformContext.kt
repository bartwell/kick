package ru.bartwell.kick.core.data

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

public class AndroidPlatformContext(internal val context: Context) : PlatformContext

public fun Context.toPlatformContext(): PlatformContext = AndroidPlatformContext(this)

@Composable
public actual fun platformContext(): PlatformContext = LocalContext.current.toPlatformContext()

public fun PlatformContext.get(): Context {
    this as AndroidPlatformContext
    return context
}

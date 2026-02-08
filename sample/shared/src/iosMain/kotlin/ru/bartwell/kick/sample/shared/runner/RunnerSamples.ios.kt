package ru.bartwell.kick.sample.shared.runner

import androidx.compose.ui.graphics.toImageBitmap
import platform.CoreGraphics.CGContextFillRect
import platform.CoreGraphics.CGContextSetFillColorWithColor
import platform.CoreGraphics.CGRectMake
import platform.UIKit.UIColor
import platform.UIKit.UIGraphicsBeginImageContextWithOptions
import platform.UIKit.UIGraphicsEndImageContext
import platform.UIKit.UIGraphicsGetImageFromCurrentImageContext
import ru.bartwell.kick.module.runner.core.data.PlatformImage
import ru.bartwell.kick.module.runner.core.data.fromImageBitmap

internal actual fun createSamplePlatformImage(): PlatformImage? {
    val size = 96.0
    UIGraphicsBeginImageContextWithOptions(CGRectMake(0.0, 0.0, size, size).size, false, 0.0)
    val ctx = platform.UIKit.UIGraphicsGetCurrentContext()
    if (ctx != null) {
        CGContextSetFillColorWithColor(ctx, UIColor.systemPinkColor.CGColor)
        CGContextFillRect(ctx, CGRectMake(0.0, 0.0, size, size))
    }
    val image = UIGraphicsGetImageFromCurrentImageContext()
    UIGraphicsEndImageContext()
    return PlatformImage.fromImageBitmap(image?.toImageBitmap())
}

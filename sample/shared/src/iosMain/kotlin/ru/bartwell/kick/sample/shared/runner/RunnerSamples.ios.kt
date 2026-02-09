package ru.bartwell.kick.sample.shared.runner

import platform.CoreGraphics.CGContextFillRect
import platform.CoreGraphics.CGContextSetFillColorWithColor
import platform.CoreGraphics.CGRectMake
import platform.CoreGraphics.CGSizeMake
import platform.UIKit.UIColor
import platform.UIKit.UIGraphicsBeginImageContextWithOptions
import platform.UIKit.UIGraphicsEndImageContext
import platform.UIKit.UIGraphicsGetImageFromCurrentImageContext
import ru.bartwell.kick.module.runner.core.data.PlatformImage
import ru.bartwell.kick.module.runner.core.data.fromNative

private const val SAMPLE_IMAGE_SIZE = 96.0

@OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)
internal actual fun createSamplePlatformImage(): PlatformImage? {
    UIGraphicsBeginImageContextWithOptions(
        CGSizeMake(SAMPLE_IMAGE_SIZE, SAMPLE_IMAGE_SIZE),
        false,
        0.0
    )
    val ctx = platform.UIKit.UIGraphicsGetCurrentContext()
    if (ctx != null) {
        CGContextSetFillColorWithColor(ctx, UIColor.purpleColor().CGColor)
        CGContextFillRect(ctx, CGRectMake(0.0, 0.0, SAMPLE_IMAGE_SIZE, SAMPLE_IMAGE_SIZE))
    }
    val image = UIGraphicsGetImageFromCurrentImageContext()
    UIGraphicsEndImageContext()
    return PlatformImage.fromNative(image)
}

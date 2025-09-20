package ru.bartwell.kick.module.overlay.core.overlay

import kotlinx.cinterop.CValue
import kotlinx.cinterop.ExperimentalForeignApi
import platform.CoreGraphics.CGPoint
import platform.CoreGraphics.CGRect
import platform.CoreGraphics.CGRectContainsPoint
import platform.UIKit.UIEvent
import platform.UIKit.UIView
import platform.UIKit.UIWindow
import platform.UIKit.UIWindowScene

@OptIn(ExperimentalForeignApi::class)
internal class PassThroughWindow : UIWindow {

    var panel: UIView? = null

    constructor(frame: CValue<CGRect>) : super(frame)
    constructor(windowScene: UIWindowScene) : super(windowScene = windowScene)

    override fun pointInside(point: CValue<CGPoint>, withEvent: UIEvent?): Boolean {
        val p = panel ?: return false
        val rectInWindow = p.convertRect(p.bounds, toView = null)
        return CGRectContainsPoint(rectInWindow, point)
    }
}

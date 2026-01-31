package ru.bartwell.kick.module.firebase.analytics.core.overlay

import kotlinx.cinterop.CValue
import kotlinx.cinterop.ExperimentalForeignApi
import platform.CoreGraphics.CGPoint
import platform.CoreGraphics.CGRect
import platform.UIKit.UIEvent
import platform.UIKit.UIView
import platform.UIKit.UIWindow
import platform.UIKit.UIWindowScene

@OptIn(ExperimentalForeignApi::class)
internal class FirebasePassThroughWindow : UIWindow {

    var panel: UIView? = null

    constructor(frame: CValue<CGRect>) : super(frame)
    constructor(windowScene: UIWindowScene) : super(windowScene = windowScene)

    override fun pointInside(point: CValue<CGPoint>, withEvent: UIEvent?): Boolean {
        val panelView = panel ?: return false
        val rectInWindow = panelView.convertRect(panelView.bounds, toView = null)
        return platform.CoreGraphics.CGRectContainsPoint(rectInWindow, point)
    }
}

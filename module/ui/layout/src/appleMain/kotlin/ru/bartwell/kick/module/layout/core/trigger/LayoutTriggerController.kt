package ru.bartwell.kick.module.layout.core.trigger

import kotlinx.cinterop.ExperimentalForeignApi
import platform.CoreGraphics.CGRectMake
import platform.Foundation.NSNotification
import platform.Foundation.NSNotificationCenter
import platform.Foundation.NSOperationQueue
import platform.UIKit.*
import platform.darwin.NSObjectProtocol
import ru.bartwell.kick.core.data.PlatformContext

@OptIn(ExperimentalForeignApi::class)
public actual class LayoutTriggerController actual constructor(
    @Suppress("UNUSED_PARAMETER") context: PlatformContext,
    private val onTrigger: () -> Unit,
) {
    private var shakeView: ShakeView? = null
    private var windowObserver: NSObjectProtocol? = null

    public actual fun start(enabled: Boolean) {
        if (!enabled || shakeView != null) return

        topWindow()?.let { w ->
            attachToWindow(w)
            return
        }

        if (windowObserver == null) {
            windowObserver = NSNotificationCenter.defaultCenter.addObserverForName(
                name = UIWindowDidBecomeKeyNotification,
                `object` = null,
                queue = NSOperationQueue.mainQueue
            ) { note: NSNotification? ->
                val w = (note?.`object` as? UIWindow) ?: topWindow()
                if (w != null && shakeView == null) {
                    attachToWindow(w)
                }
                windowObserver?.let { NSNotificationCenter.defaultCenter.removeObserver(it) }
                windowObserver = null
            }
        }
    }

    public actual fun stop() {
        windowObserver?.let { NSNotificationCenter.defaultCenter.removeObserver(it) }
        windowObserver = null

        shakeView?.let { v ->
            v.resignFirstResponder()
            v.removeFromSuperview()
        }
        shakeView = null
    }

    private fun attachToWindow(window: UIWindow) {
        val view = ShakeView { onTrigger() }.apply {
            setUserInteractionEnabled(false)
            setFrame(window.bounds)
            setAutoresizingMask(UIViewAutoresizingFlexibleWidth or UIViewAutoresizingFlexibleHeight)
        }
        window.addSubview(view)
        shakeView = view
    }
}

private fun topWindow(): UIWindow? {
    val app = UIApplication.sharedApplication
    app.keyWindow?.let { return it }
    val wins = (app.windows as? List<*>)?.filterIsInstance<UIWindow>()
    return wins?.firstOrNull()
}

@OptIn(ExperimentalForeignApi::class)
private class ShakeView(private val onShake: () -> Unit) :
    UIView(frame = CGRectMake(0.0, 0.0, 0.0, 0.0)) {

    override fun canBecomeFirstResponder(): Boolean = true

    override fun didMoveToWindow() {
        super.didMoveToWindow()
        becomeFirstResponder()
    }

    override fun motionEnded(motion: UIEventSubtype, withEvent: UIEvent?) {
        if (motion == UIEventSubtypeMotionShake) onShake()
    }
}

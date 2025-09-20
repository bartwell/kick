package ru.bartwell.kick.module.overlay.core.overlay

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import platform.CoreGraphics.CGPointMake
import platform.CoreGraphics.CGRectMake
import platform.CoreGraphics.CGSizeMake
import platform.Foundation.NSSelectorFromString
import platform.UIKit.NSLineBreakByWordWrapping
import platform.UIKit.UIApplication
import platform.UIKit.UIButton
import platform.UIKit.UIButtonTypeSystem
import platform.UIKit.UIColor
import platform.UIKit.UIControlEventTouchUpInside
import platform.UIKit.UIControlStateNormal
import platform.UIKit.UIEdgeInsetsMake
import platform.UIKit.UIFont
import platform.UIKit.UIFontWeightRegular
import platform.UIKit.UIImage
import platform.UIKit.UILabel
import platform.UIKit.UIPanGestureRecognizer
import platform.UIKit.UISceneActivationStateForegroundActive
import platform.UIKit.UIScreen
import platform.UIKit.UIView
import platform.UIKit.UIViewAutoresizingFlexibleBottomMargin
import platform.UIKit.UIViewAutoresizingFlexibleHeight
import platform.UIKit.UIViewAutoresizingFlexibleLeftMargin
import platform.UIKit.UIViewAutoresizingFlexibleRightMargin
import platform.UIKit.UIViewAutoresizingFlexibleTopMargin
import platform.UIKit.UIViewAutoresizingFlexibleWidth
import platform.UIKit.UIViewController
import platform.UIKit.UIWindowLevelAlert
import platform.UIKit.UIWindowScene
import platform.UIKit.bounds
import platform.UIKit.frame
import platform.UIKit.setContentEdgeInsets
import platform.darwin.dispatch_async
import platform.darwin.dispatch_get_main_queue
import ru.bartwell.kick.core.data.PlatformContext
import ru.bartwell.kick.module.overlay.core.persists.OverlaySettings
import ru.bartwell.kick.module.overlay.core.store.OverlayStore
import kotlin.math.max
import kotlin.math.min

@OptIn(ExperimentalForeignApi::class)
public actual object KickOverlay {
    private var overlayWindow: PassThroughWindow? = null
    private var panel: UIView? = null
    private var label: UILabel? = null
    private var scope: CoroutineScope? = null
    private var panTarget: PanTarget? = null
    private var closeTarget: ButtonTarget? = null

    private const val PANEL_WIDTH: Double = 280.0
    private const val PANEL_MIN_HEIGHT: Double = 44.0
    private const val PANEL_MAX_HEIGHT: Double = 360.0
    private const val H_PADDING: Double = 6.0
    private const val CLOSE_SIZE: Double = 20.0
    private const val CLOSE_MARGIN: Double = 4.0
    private const val CORNER: Double = 8.0
    private const val BORDER_WIDTH: Double = 1.0


    public actual fun init(context: PlatformContext): Unit = Unit

    public actual fun show(context: PlatformContext): Unit = dispatch_async(dispatch_get_main_queue()) {
        OverlaySettings.setEnabled(true)

        overlayWindow?.let {
            it.setHidden(false)
            it.makeKeyAndVisible()
            return@dispatch_async
        }

        val scene: UIWindowScene? = activeForegroundScene()
        val w: PassThroughWindow =
            if (scene != null) PassThroughWindow(windowScene = scene) else PassThroughWindow(frame = UIScreen.mainScreen.bounds)
        w.setFrame(UIScreen.mainScreen.bounds)
        w.setWindowLevel(UIWindowLevelAlert)
        w.setBackgroundColor(UIColor.clearColor)

        val root = UIView(frame = w.bounds).apply {
            setBackgroundColor(UIColor.clearColor)
            setUserInteractionEnabled(true)
            setAutoresizingMask(UIViewAutoresizingFlexibleWidth or UIViewAutoresizingFlexibleHeight)
        }
        val vc = UIViewController().apply { setView(root) }

        val p = UIView(frame = CGRectMake(50.0, 200.0, PANEL_WIDTH, PANEL_MIN_HEIGHT)).apply {
            setBackgroundColor(UIColor.blackColor.colorWithAlphaComponent(0.82))
            layer.cornerRadius = CORNER
            layer.borderWidth = BORDER_WIDTH
            layer.borderColor = UIColor.whiteColor.colorWithAlphaComponent(0.35).CGColor
            setClipsToBounds(true)
            setUserInteractionEnabled(true)
            setAutoresizingMask(
                UIViewAutoresizingFlexibleLeftMargin or
                        UIViewAutoresizingFlexibleRightMargin or
                        UIViewAutoresizingFlexibleTopMargin or
                        UIViewAutoresizingFlexibleBottomMargin
            )
        }

        val closeBtn = UIButton.buttonWithType(UIButtonTypeSystem) as UIButton
        run {
            closeBtn.setTintColor(UIColor.whiteColor.colorWithAlphaComponent(0.85))
            closeBtn.setTitle("", forState = UIControlStateNormal)
            closeBtn.setImage(UIImage.systemImageNamed("xmark"), forState = UIControlStateNormal)
            closeBtn.setContentEdgeInsets(UIEdgeInsetsMake(0.0, 0.0, 0.0, 0.0))
            closeBtn.setAutoresizingMask(UIViewAutoresizingFlexibleLeftMargin)
        }

        val lbl = UILabel(frame = CGRectMake(0.0, 0.0, 0.0, 0.0)).apply {
            setTextColor(UIColor.whiteColor)
            setFont(UIFont.monospacedSystemFontOfSize(12.0, UIFontWeightRegular))
            setNumberOfLines(0)
            setLineBreakMode(NSLineBreakByWordWrapping)
            setUserInteractionEnabled(false)
            setAutoresizingMask(UIViewAutoresizingFlexibleWidth or UIViewAutoresizingFlexibleHeight)
        }

        val close = ButtonTarget { hide() }.also { closeTarget = it }
        closeBtn.addTarget(close, NSSelectorFromString("invoke:"), UIControlEventTouchUpInside)

        val pan = PanTarget { dx, dy ->
            val c = p.center
            val cx = c.useContents { x }
            val cy = c.useContents { y }
            p.setCenter(CGPointMake(cx + dx, cy + dy))
        }.also { panTarget = it }
        val panGR = UIPanGestureRecognizer(target = pan, action = NSSelectorFromString("onPan:"))
        panGR.setCancelsTouchesInView(false)
        p.addGestureRecognizer(panGR)

        p.addSubview(lbl)
        p.addSubview(closeBtn)
        root.addSubview(p)

        w.setRootViewController(vc)
        w.panel = p
        w.setHidden(false)
        w.makeKeyAndVisible()

        overlayWindow = w
        panel = p
        label = lbl

        relayout(p, lbl, closeBtn, text = "")

        scope = MainScope().also { sc ->
            sc.launch {
                OverlayStore.items.collect { list ->
                    val text = buildString {
                        list.forEach { (k, v) -> append(k).append(": ").append(v).append('\n') }
                    }
                    label?.setText(text)
                    panel?.let { pn -> label?.let { lb -> relayout(pn, lb, closeBtn, text) } }
                }
            }
        }
    }

    public actual fun hide(): Unit = dispatch_async(dispatch_get_main_queue()) {
        OverlaySettings.setEnabled(false)
        scope?.cancel()
        scope = null
        panel?.removeFromSuperview()
        overlayWindow?.apply {
            setRootViewController(null)
            setHidden(true)
        }
        overlayWindow = null
        panel = null
        label = null
        panTarget = null
        closeTarget = null
    }

    private fun activeForegroundScene(): UIWindowScene? {
        val set = UIApplication.sharedApplication.connectedScenes ?: return null
        val list = set.toList() as? List<*> ?: return null
        for (obj in list) {
            val scene = obj as? UIWindowScene ?: continue
            if (scene.activationState == UISceneActivationStateForegroundActive) return scene
        }
        return null
    }

    @OptIn(ExperimentalForeignApi::class)
    private fun relayout(panel: UIView, lbl: UILabel, closeBtn: UIButton, text: String): Unit {
        val textX = H_PADDING
        val textY = CLOSE_MARGIN
        val textW = PANEL_WIDTH - (H_PADDING + CLOSE_MARGIN + CLOSE_SIZE + H_PADDING)

        val measured = lbl.sizeThatFits(CGSizeMake(textW, Double.MAX_VALUE))
        val textH = measured.useContents { height }

        val contentH = min(PANEL_MAX_HEIGHT - textY, max(0.0, textH))
        val panelH = max(PANEL_MIN_HEIGHT, textY + contentH)

        val old = panel.frame
        val x = old.useContents { origin.x }
        val y = old.useContents { origin.y }
        panel.setFrame(CGRectMake(x, y, PANEL_WIDTH, panelH))

        lbl.setFrame(CGRectMake(textX, textY, textW, panelH - textY))
        closeBtn.setFrame(CGRectMake(PANEL_WIDTH - CLOSE_MARGIN - CLOSE_SIZE, CLOSE_MARGIN, CLOSE_SIZE, CLOSE_SIZE))
    }
}





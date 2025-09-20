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
import platform.Foundation.NSNotification
import platform.Foundation.NSNotificationCenter
import platform.Foundation.NSOperationQueue
import platform.Foundation.NSSelectorFromString
import platform.UIKit.NSLineBreakByWordWrapping
import platform.UIKit.UIApplication
import platform.UIKit.UIApplicationDidBecomeActiveNotification
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
import platform.UIKit.UIWindowDidBecomeKeyNotification
import platform.UIKit.UIWindowLevelAlert
import platform.UIKit.UIWindowScene
import platform.UIKit.frame
import platform.UIKit.setContentEdgeInsets
import platform.darwin.dispatch_async
import platform.darwin.dispatch_get_main_queue
import ru.bartwell.kick.core.data.PlatformContext
import ru.bartwell.kick.module.overlay.core.persists.OverlaySettings
import ru.bartwell.kick.module.overlay.core.store.OverlayStore
import kotlin.math.max
import kotlin.math.min

private const val INITIAL_X = 50.0
private const val INITIAL_Y = 200.0
private const val PANEL_WIDTH: Double = 280.0
private const val PANEL_MIN_HEIGHT: Double = 44.0
private const val PANEL_MAX_HEIGHT: Double = 360.0
private const val H_PADDING: Double = 6.0
private const val CLOSE_SIZE: Double = 20.0
private const val CLOSE_MARGIN: Double = 4.0
private const val CORNER: Double = 8.0
private const val BORDER_WIDTH: Double = 1.0
private const val BACKGROUND_ALPHA: Double = 0.82
private const val BORDER_ALPHA: Double = 0.35
private const val FONT_SIZE: Double = 12.0

@OptIn(ExperimentalForeignApi::class)
public actual object KickOverlay {
    private var overlayWindow: PassThroughWindow? = null
    private var panel: UIView? = null
    private var label: UILabel? = null
    private var scope: CoroutineScope? = null
    private var panTarget: PanTarget? = null
    private var closeTarget: ButtonTarget? = null
    private var windowObserver: platform.darwin.NSObjectProtocol? = null
    private var appActiveObserver: platform.darwin.NSObjectProtocol? = null

    public actual fun init(context: PlatformContext) {
        dispatch_async(dispatch_get_main_queue()) {
            if (appActiveObserver == null) {
                appActiveObserver = NSNotificationCenter.defaultCenter.addObserverForName(
                    name = UIApplicationDidBecomeActiveNotification,
                    `object` = null,
                    queue = NSOperationQueue.mainQueue
                ) { _: NSNotification? ->
                    if (OverlaySettings.isEnabled()) {
                        show(context)
                    }
                }
            }
        }
    }

    public actual fun show(context: PlatformContext) {
        dispatch_async(dispatch_get_main_queue()) {
            OverlaySettings.setEnabled(true)

            overlayWindow?.let {
                it.setHidden(false)
                it.makeKeyAndVisible()
                return@dispatch_async
            }

            val scene: UIWindowScene? = activeForegroundScene()
            if (scene == null) {
                if (windowObserver == null) {
                    windowObserver = NSNotificationCenter.defaultCenter.addObserverForName(
                        name = UIWindowDidBecomeKeyNotification,
                        `object` = null,
                        queue = NSOperationQueue.mainQueue
                    ) { _: NSNotification? ->
                        if (overlayWindow == null && OverlaySettings.isEnabled()) {
                            show(context)
                        }
                        windowObserver?.let { NSNotificationCenter.defaultCenter.removeObserver(it) }
                        windowObserver = null
                    }
                }
                return@dispatch_async
            }
            val window = PassThroughWindow(windowScene = scene)
            window.setFrame(UIScreen.mainScreen.bounds)
            window.setWindowLevel(UIWindowLevelAlert)
            window.setBackgroundColor(UIColor.clearColor)

            val root = UIView(frame = window.bounds).apply {
                setBackgroundColor(UIColor.clearColor)
                setUserInteractionEnabled(true)
                setAutoresizingMask(UIViewAutoresizingFlexibleWidth or UIViewAutoresizingFlexibleHeight)
            }
            val viewController = UIViewController().apply { setView(root) }

            val mainView = createMainView()
            val closeBtn = createCloseButton()
            val uILabel = createTextView()
            val panGR = createPanTarget(mainView)
            mainView.addGestureRecognizer(panGR)

            mainView.addSubview(uILabel)
            mainView.addSubview(closeBtn)
            root.addSubview(mainView)

            window.setRootViewController(viewController)
            window.panel = mainView
            window.setHidden(false)
            window.makeKeyAndVisible()

            overlayWindow = window
            panel = mainView
            label = uILabel

            relayout(mainView, uILabel, closeBtn)

            scope = MainScope().also { sc ->
                sc.launch {
                    OverlayStore.items.collect { list ->
                        val text = buildString {
                            list.forEach { (k, v) -> append(k).append(": ").append(v).append('\n') }
                        }
                        label?.setText(text)
                        panel?.let { pn -> label?.let { lb -> relayout(pn, lb, closeBtn) } }
                    }
                }
            }
        }
    }

    private fun createPanTarget(mainView: UIView): UIPanGestureRecognizer {
        val pan = PanTarget { dx, dy ->
            val c = mainView.center
            val cx = c.useContents { x }
            val cy = c.useContents { y }
            mainView.setCenter(CGPointMake(cx + dx, cy + dy))
        }.also { panTarget = it }
        val panGR = UIPanGestureRecognizer(target = pan, action = NSSelectorFromString("onPan:"))
        panGR.setCancelsTouchesInView(false)
        return panGR
    }

    private fun createTextView(): UILabel {
        return UILabel(frame = CGRectMake(0.0, 0.0, 0.0, 0.0)).apply {
            setTextColor(UIColor.whiteColor)
            setFont(UIFont.monospacedSystemFontOfSize(FONT_SIZE, UIFontWeightRegular))
            setNumberOfLines(0)
            setLineBreakMode(NSLineBreakByWordWrapping)
            setUserInteractionEnabled(false)
            setAutoresizingMask(UIViewAutoresizingFlexibleWidth or UIViewAutoresizingFlexibleHeight)
        }
    }

    private fun createCloseButton(): UIButton {
        val button = UIButton.buttonWithType(UIButtonTypeSystem)
        button.setTintColor(UIColor.whiteColor.colorWithAlphaComponent(BACKGROUND_ALPHA))
        button.setTitle("", forState = UIControlStateNormal)
        button.setImage(UIImage.systemImageNamed("xmark"), forState = UIControlStateNormal)
        button.setContentEdgeInsets(UIEdgeInsetsMake(0.0, 0.0, 0.0, 0.0))
        button.setAutoresizingMask(UIViewAutoresizingFlexibleLeftMargin)
        val close = ButtonTarget { hide() }.also { closeTarget = it }
        button.addTarget(close, NSSelectorFromString("invoke:"), UIControlEventTouchUpInside)
        return button
    }

    private fun createMainView(): UIView {
        return UIView(frame = CGRectMake(INITIAL_X, INITIAL_Y, PANEL_WIDTH, PANEL_MIN_HEIGHT)).apply {
            setBackgroundColor(UIColor.blackColor.colorWithAlphaComponent(BACKGROUND_ALPHA))
            layer.cornerRadius = CORNER
            layer.borderWidth = BORDER_WIDTH
            layer.borderColor = UIColor.whiteColor.colorWithAlphaComponent(BORDER_ALPHA).CGColor
            setClipsToBounds(true)
            setUserInteractionEnabled(true)
            setAutoresizingMask(
                UIViewAutoresizingFlexibleLeftMargin or
                        UIViewAutoresizingFlexibleRightMargin or
                        UIViewAutoresizingFlexibleTopMargin or
                        UIViewAutoresizingFlexibleBottomMargin
            )
        }
    }

    public actual fun hide() {
        dispatch_async(dispatch_get_main_queue()) {
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
            windowObserver?.let { NSNotificationCenter.defaultCenter.removeObserver(it) }
            windowObserver = null
            appActiveObserver?.let { NSNotificationCenter.defaultCenter.removeObserver(it) }
            appActiveObserver = null
        }
    }

    private fun activeForegroundScene(): UIWindowScene? {
        val set = UIApplication.sharedApplication.connectedScenes
        val list = set.toList()
        for (obj in list) {
            val scene = obj as? UIWindowScene ?: continue
            if (scene.activationState == UISceneActivationStateForegroundActive) return scene
        }
        return null
    }

    @OptIn(ExperimentalForeignApi::class)
    private fun relayout(panel: UIView, lbl: UILabel, closeBtn: UIButton) {
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

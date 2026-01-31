package ru.bartwell.kick.module.firebase.analytics.core.overlay

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collect
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
import platform.UIKit.UIScene
import platform.UIKit.UISceneActivationStateForegroundActive
import platform.UIKit.frame
import platform.UIKit.setContentEdgeInsets
import platform.darwin.dispatch_async
import platform.darwin.dispatch_get_main_queue
import ru.bartwell.kick.core.data.PlatformContext
import ru.bartwell.kick.module.firebase.analytics.core.persist.FirebaseFloatingWindowSettings
import ru.bartwell.kick.module.firebase.analytics.core.util.FirebaseFloatingWindowState
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
internal actual object FirebaseFloatingWindowHost {
    private var overlayWindow: FirebasePassThroughWindow? = null
    private var panel: UIView? = null
    private var label: UILabel? = null
    private var closeButton: UIButton? = null
    private var scope: CoroutineScope? = null
    private var panTarget: FirebasePanTarget? = null
    private var closeTarget: FirebaseButtonTarget? = null
    private var windowObserver: platform.darwin.NSObjectProtocol? = null
    private var appActiveObserver: platform.darwin.NSObjectProtocol? = null
    private var visible = false
    private var initialized = false

    actual fun init(context: PlatformContext) {
        dispatch_async(dispatch_get_main_queue()) {
            if (initialized) return@dispatch_async
            initialized = true
            appActiveObserver = NSNotificationCenter.defaultCenter.addObserverForName(
                name = UIApplicationDidBecomeActiveNotification,
                `object` = null,
                queue = NSOperationQueue.mainQueue
            ) { _: NSNotification? ->
                if (visible) {
                    show()
                }
            }
        }
    }

    actual fun setVisible(enabled: Boolean) {
        visible = enabled
        dispatch_async(dispatch_get_main_queue()) {
            if (enabled) {
                show()
            } else {
                hide()
            }
        }
    }

    private fun show() {
        val window = overlayWindow
        if (window != null) {
            window.setHidden(false)
            window.makeKeyAndVisible()
            panel?.let { applyStoredOrigin(it) }
            panel?.let { relayout(it, label, closeButton ?: return) }
            return
        }

        val scene: UIWindowScene? = activeForegroundScene()
        if (scene == null) {
            if (windowObserver == null) {
                windowObserver = NSNotificationCenter.defaultCenter.addObserverForName(
                    name = UIWindowDidBecomeKeyNotification,
                    `object` = null,
                    queue = NSOperationQueue.mainQueue
                ) { _: NSNotification? ->
                    if (overlayWindow == null && visible) {
                        show()
                    }
                    windowObserver?.let { NSNotificationCenter.defaultCenter.removeObserver(it) }
                    windowObserver = null
                }
            }
            return
        }

        val overlay = FirebasePassThroughWindow(windowScene = scene)
        overlay.setFrame(UIScreen.mainScreen.bounds)
        overlay.setWindowLevel(UIWindowLevelAlert)
        overlay.setBackgroundColor(UIColor.clearColor)

        val root = UIView(frame = overlay.bounds).apply {
            setBackgroundColor(UIColor.clearColor)
            setUserInteractionEnabled(true)
            setAutoresizingMask(UIViewAutoresizingFlexibleWidth or UIViewAutoresizingFlexibleHeight)
        }
        val viewController = UIViewController().apply { setView(root) }

        val originX = FirebaseFloatingWindowSettings.getPositionX().takeIf { !it.isNaN() } ?: INITIAL_X
        val originY = FirebaseFloatingWindowSettings.getPositionY().takeIf { !it.isNaN() } ?: INITIAL_Y
        val mainView = UIView(frame = CGRectMake(originX, originY, PANEL_WIDTH, PANEL_MIN_HEIGHT)).apply {
            setBackgroundColor(UIColor.whiteColor.colorWithAlphaComponent(BACKGROUND_ALPHA))
            setUserInteractionEnabled(true)
            layer?.setCornerRadius(CORNER)
            layer?.setBorderWidth(BORDER_WIDTH)
            layer?.setBorderColor(UIColor.blackColor.colorWithAlphaComponent(BORDER_ALPHA).CGColor)
            setAutoresizingMask(
                UIViewAutoresizingFlexibleWidth or UIViewAutoresizingFlexibleHeight or
                    UIViewAutoresizingFlexibleLeftMargin or UIViewAutoresizingFlexibleRightMargin or
                    UIViewAutoresizingFlexibleTopMargin or UIViewAutoresizingFlexibleBottomMargin
            )
        }

        val closeBtn = createCloseButton()
        val textLabel = createTextLabel()
        val panGR = createPanTarget(mainView)
        mainView.addGestureRecognizer(panGR)
        mainView.addSubview(textLabel)
        mainView.addSubview(closeBtn)

        root.addSubview(mainView)

        overlay.setRootViewController(viewController)
        overlay.panel = mainView
        overlay.setHidden(false)
        overlay.makeKeyAndVisible()

        overlayWindow = overlay
        panel = mainView
        label = textLabel
        panTarget = panGR.target as? FirebasePanTarget
        closeButton = closeBtn
        label?.let { relayout(mainView, it, closeBtn) }

        scope = MainScope().also { sc ->
            sc.launch {
                FirebaseFloatingWindowState.lines.collect { currentLines ->
                    label?.setText(currentLines.joinToString("\n"))
                    panel?.let { pn ->
                        label?.let { lb ->
                            relayout(pn, lb, closeBtn)
                        }
                    }
                }
            }
        }
    }

    private fun hide() {
        overlayWindow?.setHidden(true)
        overlayWindow?.let {
            it.resignKeyWindow()
            it.removeFromSuperview()
        }
        overlayWindow = null
        panel = null
        label = null
        closeButton = null
        scope?.cancel()
        scope = null
        panTarget = null
        closeTarget = null
    }

    private fun relayout(panel: UIView, label: UILabel, closeBtn: UIButton) {
        val contentWidth = min(PANEL_WIDTH, max(label.intrinsicContentSize.width + H_PADDING * 2, PANEL_MIN_HEIGHT))
        val contentHeight = min(
            PANEL_MAX_HEIGHT,
            max(label.intrinsicContentSize.height + CLOSE_MARGIN + CLOSE_SIZE, PANEL_MIN_HEIGHT)
        )
        val originX = min(panel.superview?.bounds?.width?.minus(contentWidth) ?: contentWidth, panel.frame.origin.x)
        val originY = min(panel.superview?.bounds?.height?.minus(contentHeight) ?: contentHeight, panel.frame.origin.y)
        panel.setFrame(CGRectMake(originX, originY, contentWidth, contentHeight))
        label.setFrame(CGRectMake(H_PADDING, CLOSE_MARGIN, contentWidth - H_PADDING * 2, contentHeight - CLOSE_MARGIN - H_PADDING))
        closeBtn.setFrame(CGRectMake(contentWidth - CLOSE_SIZE - CLOSE_MARGIN, CLOSE_MARGIN, CLOSE_SIZE, CLOSE_SIZE))
        FirebaseFloatingWindowSettings.setPosition(originX.toFloat(), originY.toFloat())
    }

    private fun applyStoredOrigin(panel: UIView) {
        val x = FirebaseFloatingWindowSettings.getPositionX()
        val y = FirebaseFloatingWindowSettings.getPositionY()
        if (!x.isNaN() && !y.isNaN()) {
            val frame = panel.frame
            panel.setFrame(CGRectMake(x.toDouble(), y.toDouble(), frame.size.width, frame.size.height))
        }
    }

    private fun createCloseButton(): UIButton {
        val button = UIButton.buttonWithType(UIButtonTypeSystem)
        button.setTintColor(UIColor.whiteColor.colorWithAlphaComponent(BACKGROUND_ALPHA))
        button.setTitle("", forState = UIControlStateNormal)
        button.setImage(UIImage.systemImageNamed("xmark"), forState = UIControlStateNormal)
        button.setContentEdgeInsets(UIEdgeInsetsMake(0.0, 0.0, 0.0, 0.0))
        button.setAutoresizingMask(UIViewAutoresizingFlexibleLeftMargin)
        closeTarget = FirebaseButtonTarget { FirebaseFloatingWindowState.setVisible(false) }
        button.addTarget(closeTarget, NSSelectorFromString("invoke:"), UIControlEventTouchUpInside)
        return button
    }

    private fun createTextLabel(): UILabel {
        return UILabel(frame = CGRectMake(0.0, 0.0, 0.0, 0.0)).apply {
            setTextColor(UIColor.blackColor)
            setFont(UIFont.monospacedSystemFontOfSize(FONT_SIZE, UIFontWeightRegular))
            setNumberOfLines(0)
            setLineBreakMode(NSLineBreakByWordWrapping)
            setUserInteractionEnabled(false)
            setAutoresizingMask(UIViewAutoresizingFlexibleWidth or UIViewAutoresizingFlexibleHeight)
        }
    }

    private fun createPanTarget(mainView: UIView): UIPanGestureRecognizer {
        val pan = FirebasePanTarget { dx, dy ->
            val center = mainView.center
            val nx = center.useContents { x } + dx
            val ny = center.useContents { y } + dy
            mainView.setCenter(CGPointMake(nx, ny))
            val origin = mainView.frame.origin
            FirebaseFloatingWindowSettings.setPosition(origin.useContents { x }.toFloat(), origin.useContents { y }.toFloat())
        }
        val panGR = UIPanGestureRecognizer(target = pan, action = NSSelectorFromString("onPan:"))
        panGR.setCancelsTouchesInView(false)
        return panGR
    }

    private fun activeForegroundScene(): UIWindowScene? {
        val scenes = UIApplication.sharedApplication.connectedScenes
        scenes.iterator().forEach { scene ->
            if (scene.activationState == UISceneActivationStateForegroundActive) {
                return scene as? UIWindowScene
            }
        }
        return null
    }
}

package ru.bartwell.kick.module.firebase.analytics.core.overlay

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.ObjCAction
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
import platform.UIKit.UIColor
import platform.UIKit.UIFont
import platform.UIKit.UIFontWeightRegular
import platform.UIKit.UILabel
import platform.UIKit.UIPanGestureRecognizer
import platform.UIKit.UIScreen
import platform.UIKit.UITapGestureRecognizer
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
import platform.UIKit.UIScene
import platform.UIKit.UISceneActivationStateForegroundActive
import platform.UIKit.UIWindowScene
import platform.UIKit.frame
import platform.darwin.dispatch_async
import platform.darwin.dispatch_get_main_queue
import platform.darwin.NSObject
import ru.bartwell.kick.Kick
import ru.bartwell.kick.core.data.PlatformContext
import ru.bartwell.kick.core.presentation.overlay.PanTarget
import ru.bartwell.kick.core.presentation.overlay.PassThroughWindow
import ru.bartwell.kick.core.data.StartScreen
import ru.bartwell.kick.core.data.ModuleDescription
import ru.bartwell.kick.module.firebase.analytics.core.component.config.FirebaseAnalyticsConfig
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
private const val EMPTY_TEXT: String = "No events"

@OptIn(ExperimentalForeignApi::class)
internal actual object FirebaseFloatingWindowHost {
    private var overlayWindow: PassThroughWindow? = null
    private var panel: UIView? = null
    private var label: UILabel? = null
    private var scope: CoroutineScope? = null
    private var panTarget: PanTarget? = null
    private var tapTarget: FirebaseTapTarget? = null
    private var windowObserver: platform.darwin.NSObjectProtocol? = null
    private var appActiveObserver: platform.darwin.NSObjectProtocol? = null
    private var visible = false
    private var initialized = false
    private var platformContext: PlatformContext? = null

    actual fun init(context: PlatformContext) {
        platformContext = context
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
            panel?.let { pnl -> label?.let { lbl -> relayout(pnl, lbl) } }
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

        val overlay = PassThroughWindow(windowScene = scene)
        overlay.setFrame(UIScreen.mainScreen.bounds)
        overlay.setWindowLevel(UIWindowLevelAlert)
        overlay.setBackgroundColor(UIColor.clearColor)

        val root = UIView(frame = overlay.bounds).apply {
            setBackgroundColor(UIColor.clearColor)
            setUserInteractionEnabled(true)
            setAutoresizingMask(UIViewAutoresizingFlexibleWidth or UIViewAutoresizingFlexibleHeight)
        }
        val viewController = UIViewController().apply { setView(root) }

        val originX = (FirebaseFloatingWindowSettings.getPositionX().takeIf { !it.isNaN() } ?: INITIAL_X.toFloat()).toDouble()
        val originY = (FirebaseFloatingWindowSettings.getPositionY().takeIf { !it.isNaN() } ?: INITIAL_Y.toFloat()).toDouble()
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

        val textLabel = createTextLabel()
        val panGR = createPanTarget(mainView)
        val tapGR = createTapTarget(mainView)
        mainView.addGestureRecognizer(panGR)
        mainView.addGestureRecognizer(tapGR)
        mainView.addSubview(textLabel)

        root.addSubview(mainView)

        overlay.setRootViewController(viewController)
        overlay.panel = mainView

        overlayWindow = overlay
        panel = mainView
        label = textLabel

        overlay.setHidden(false)
        overlay.makeKeyAndVisible()

        label?.let { relayout(mainView, it) }

        scope = MainScope().also { sc ->
            sc.launch {
                FirebaseFloatingWindowState.lines.collect { currentLines ->
                    val text = if (currentLines.isEmpty()) EMPTY_TEXT else currentLines.joinToString("\n")
                    label?.setText(text)
                    panel?.let { pn ->
                        label?.let { lb ->
                            relayout(pn, lb)
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
        scope?.cancel()
        scope = null
        panTarget = null
        tapTarget = null
    }

    private fun relayout(panel: UIView, label: UILabel) {
        val textWidth = PANEL_WIDTH - H_PADDING * 2
        val measured = label.sizeThatFits(CGSizeMake(textWidth, Double.MAX_VALUE))
        val textHeight = measured.useContents { height }
        val contentWidth = PANEL_WIDTH
        val contentHeight = min(
            PANEL_MAX_HEIGHT,
            max(PANEL_MIN_HEIGHT, textHeight + CLOSE_MARGIN + H_PADDING)
        )

        var originX = 0.0
        var originY = 0.0
        panel.frame.useContents {
            val availableWidth = panel.superview?.bounds?.useContents { size.width } ?: contentWidth
            val availableHeight = panel.superview?.bounds?.useContents { size.height } ?: contentHeight
            originX = min(availableWidth - contentWidth, origin.x)
            originY = min(availableHeight - contentHeight, origin.y)
        }

        panel.setFrame(CGRectMake(originX, originY, contentWidth, contentHeight))
        label.setFrame(CGRectMake(H_PADDING, CLOSE_MARGIN, textWidth, contentHeight - CLOSE_MARGIN - H_PADDING))
        FirebaseFloatingWindowSettings.setPosition(originX.toFloat(), originY.toFloat())
    }

    private fun applyStoredOrigin(panel: UIView) {
        val x = FirebaseFloatingWindowSettings.getPositionX()
        val y = FirebaseFloatingWindowSettings.getPositionY()
        if (!x.isNaN() && !y.isNaN()) {
            panel.frame.useContents {
                panel.setFrame(CGRectMake(x.toDouble(), y.toDouble(), size.width, size.height))
            }
        }
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
        val pan = PanTarget { dx, dy ->
            val center = mainView.center
            val nx = center.useContents { x } + dx
            val ny = center.useContents { y } + dy
            mainView.setCenter(CGPointMake(nx, ny))
            mainView.frame.useContents {
                FirebaseFloatingWindowSettings.setPosition(origin.x.toFloat(), origin.y.toFloat())
            }
        }
        panTarget = pan
        val panGR = UIPanGestureRecognizer(target = pan, action = NSSelectorFromString("onPan:"))
        panGR.setCancelsTouchesInView(false)
        return panGR
    }

    private fun createTapTarget(mainView: UIView): UITapGestureRecognizer {
        val tap = FirebaseTapTarget { openAnalyticsModule() }
        tapTarget = tap
        val tapGR = UITapGestureRecognizer(target = tap, action = NSSelectorFromString("onTap:"))
        tapGR.setCancelsTouchesInView(false)
        return tapGR
    }

    private fun openAnalyticsModule() {
        platformContext?.let { ctx ->
            Kick.launch(
                context = ctx,
                startScreen = StartScreen(FirebaseAnalyticsConfig, ModuleDescription.FIREBASE_ANALYTICS)
            )
        }
    }

    private fun activeForegroundScene(): UIWindowScene? {
        val scenes = UIApplication.sharedApplication.connectedScenes
        scenes.iterator().forEach { scene ->
            val windowScene = scene as? UIWindowScene ?: return@forEach
            if (windowScene.activationState == UISceneActivationStateForegroundActive) {
                return windowScene
            }
        }
        return null
    }
}

@OptIn(ExperimentalForeignApi::class)
internal class FirebaseTapTarget(private val onTap: () -> Unit) : NSObject() {
    @ObjCAction
    fun onTap(recognizer: UITapGestureRecognizer?) {
        onTap()
    }
}

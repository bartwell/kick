package ru.bartwell.kick.module.firebase.analytics.core.overlay

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.core.view.isVisible
import ru.bartwell.kick.Kick
import ru.bartwell.kick.core.data.ModuleDescription
import ru.bartwell.kick.core.data.PlatformContext
import ru.bartwell.kick.core.data.StartScreen
import ru.bartwell.kick.core.data.get
import ru.bartwell.kick.module.firebase.analytics.core.component.config.FirebaseAnalyticsConfig
import ru.bartwell.kick.core.presentation.overlay.DraggableContainer
import ru.bartwell.kick.module.firebase.analytics.core.persist.FirebaseFloatingWindowSettings
import java.lang.ref.WeakReference
import java.util.WeakHashMap

private const val TAG = "FirebaseAnalyticsFloatingWindow"
private const val INITIAL_X = 48f
private const val INITIAL_Y = 144f

internal actual object FirebaseFloatingWindowHost {
    private var callbacks: FloatingWindowCallbacks? = null
    private var platformContext: PlatformContext? = null

    actual fun init(context: PlatformContext) {
        platformContext = context
        val app = context.get().applicationContext as? Application ?: return
        if (callbacks != null) return
        callbacks = FloatingWindowCallbacks(app) { openAnalyticsModule() }
        app.registerActivityLifecycleCallbacks(callbacks)
    }

    actual fun setVisible(enabled: Boolean) {
        callbacks?.setVisible(enabled)
    }

    private fun openAnalyticsModule() {
        platformContext?.let { context ->
            Kick.launch(
                context = context,
                startScreen = StartScreen(FirebaseAnalyticsConfig, ModuleDescription.FIREBASE_ANALYTICS),
            )
        }
    }
}

private class FloatingWindowCallbacks(
    private val app: Application,
    private val onClick: () -> Unit,
) : Application.ActivityLifecycleCallbacks {

    private val overlays = WeakHashMap<Activity, DraggableContainer>()
    private var currentActivity: WeakReference<Activity> = WeakReference(null)
    private var visible = false

    fun setVisible(enabled: Boolean) {
        visible = enabled
        overlays.values.forEach { container ->
            container.isVisible = enabled
            applyStoredTranslation(container.dragTarget)
        }
        if (enabled) {
            currentActivity.get()?.let { attach(it) }
        }
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) = Unit
    override fun onActivityStarted(activity: Activity) {
        currentActivity = WeakReference(activity)
        if (visible) {
            attach(activity)
        }
    }

    override fun onActivityResumed(activity: Activity) {
        currentActivity = WeakReference(activity)
        overlays[activity]?.let { container ->
            container.isVisible = visible
            applyStoredTranslation(container.dragTarget)
        }
    }

    override fun onActivityPaused(activity: Activity) = Unit

    override fun onActivityStopped(activity: Activity) {
        overlays[activity]?.isVisible = false
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) = Unit

    override fun onActivityDestroyed(activity: Activity) {
        detach(activity)
    }

    private fun attach(activity: Activity) {
        if (!visible) return
        overlays[activity]?.let { container ->
            container.isVisible = true
            applyStoredTranslation(container.dragTarget)
            return
        }

        val root = (activity.window?.decorView as? ViewGroup) ?: return

        root.findViewWithTag<View>(TAG)?.let { existing ->
            existing.isVisible = true
            if (existing is DraggableContainer) {
                overlays[activity] = existing
                applyStoredTranslation(existing.dragTarget)
            }
            return
        }

        val container = DraggableContainer(activity) { x, y ->
            FirebaseFloatingWindowSettings.setPosition(x, y)
        }.apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT,
            )
            tag = TAG
            isClickable = true
        }

        val composeView = ComposeView(activity).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnDetachedFromWindow)
            setContent {
                MaterialTheme {
                    FirebaseFloatingWindowContent(onClick = onClick)
                }
            }
            isClickable = true
            translationX = FirebaseFloatingWindowSettings.getPositionX().takeIf { it.isValid() } ?: INITIAL_X
            translationY = FirebaseFloatingWindowSettings.getPositionY().takeIf { it.isValid() } ?: INITIAL_Y
        }

        container.dragTarget = composeView

        container.addView(
            composeView,
            FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT,
                Gravity.TOP or Gravity.START,
            )
        )

        root.addView(container)
        overlays[activity] = container
    }

    private fun detach(activity: Activity) {
        val root = activity.window?.decorView as? ViewGroup ?: return
        overlays.remove(activity)?.let { view ->
            runCatching { root.removeView(view) }
        }
    }

    private fun applyStoredTranslation(target: View?) {
        val x = FirebaseFloatingWindowSettings.getPositionX()
        val y = FirebaseFloatingWindowSettings.getPositionY()
        target?.let {
            if (x.isValid()) it.translationX = x
            if (y.isValid()) it.translationY = y
        }
    }
}

private fun Float.isValid(): Boolean = !this.isNaN() && this.isFinite()

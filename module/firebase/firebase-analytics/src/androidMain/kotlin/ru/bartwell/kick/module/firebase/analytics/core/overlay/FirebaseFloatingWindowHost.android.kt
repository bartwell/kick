package ru.bartwell.kick.module.firebase.analytics.core.overlay

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.core.view.isVisible
import ru.bartwell.kick.core.data.PlatformContext
import ru.bartwell.kick.core.data.get
import ru.bartwell.kick.module.firebase.analytics.core.util.FirebaseFloatingWindowState
import java.lang.ref.WeakReference
import java.util.WeakHashMap

private const val TAG = "FirebaseAnalyticsFloatingWindow"
private const val INITIAL_X = 48f
private const val INITIAL_Y = 144f

internal actual object FirebaseFloatingWindowHost {
    private var callbacks: FloatingWindowCallbacks? = null

    actual fun init(context: PlatformContext) {
        val app = context.get().applicationContext as? Application ?: return
        if (callbacks != null) return
        callbacks = FloatingWindowCallbacks(app)
        app.registerActivityLifecycleCallbacks(callbacks)
    }

    actual fun setVisible(enabled: Boolean) {
        callbacks?.setVisible(enabled)
    }
}

private class FloatingWindowCallbacks(
    private val app: Application,
) : Application.ActivityLifecycleCallbacks {

    private val overlays = WeakHashMap<Activity, FrameLayout>()
    private var currentActivity: WeakReference<Activity> = WeakReference(null)
    private var visible = false

    fun setVisible(enabled: Boolean) {
        visible = enabled
        overlays.values.forEach { it.isVisible = enabled }
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
        overlays[activity]?.isVisible = visible
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
        overlays[activity]?.let {
            it.isVisible = true
            return
        }

        val root = (activity.window?.decorView as? ViewGroup) ?: return

        root.findViewWithTag<View>(TAG)?.let { existing ->
            existing.isVisible = true
            if (existing is FrameLayout) {
                overlays[activity] = existing
            }
            return
        }

        val container = DraggableContainer(activity).apply {
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
                    FirebaseFloatingWindowContent(onClose = {
                        FirebaseFloatingWindowState.setVisible(false)
                    })
                }
            }
            isClickable = true
            translationX = INITIAL_X
            translationY = INITIAL_Y
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
}

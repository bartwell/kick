package ru.bartwell.kick.module.overlay.core.overlay

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
import ru.bartwell.kick.module.overlay.core.persists.OverlaySettings
import java.lang.ref.WeakReference
import java.util.WeakHashMap

private const val INITIAL_X = 50f
private const val INITIAL_Y = 200f

internal class InAppOverlayCallbacks : Application.ActivityLifecycleCallbacks {
    private val overlays = WeakHashMap<Activity, FrameLayout>()
    val currentActivity = WeakReference<Activity>(null)
    var dragTarget: View? = null

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) = Unit
    override fun onActivityStarted(activity: Activity) {
        if (OverlaySettings.isEnabled()) attach(activity)
    }

    override fun onActivityResumed(activity: Activity) {
        currentActivity.clear()
        currentActivity.enqueue()
        currentActivity.get()
        overlays[activity]?.isVisible = OverlaySettings.isEnabled()
    }

    override fun onActivityPaused(activity: Activity) = Unit
    override fun onActivityStopped(activity: Activity) {
        detach(activity)
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) = Unit
    override fun onActivityDestroyed(activity: Activity) = detach(activity)

    fun attach(activity: Activity) {
        overlays[activity]?.let {
            it.isVisible = true
            return
        }

        (activity.window?.decorView as? ViewGroup)?.let { root ->
            val tag = "KickOverlayInApp"

            root.findViewWithTag<View>(tag)?.let {
                it.isVisible = true
                overlays[activity] = it as FrameLayout
                return
            }

            val container = DraggableContainer(activity).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                this.tag = tag
                isClickable = true
            }

            val composeView = ComposeView(activity).apply {
                setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnDetachedFromWindow)
                setContent { MaterialTheme { OverlayWindow(onCloseClick = ::onCloseClicked) } }
                isClickable = true
                translationX = INITIAL_X
                translationY = INITIAL_Y
                setTag("KickOverlayPanel")
            }

            container.dragTarget = composeView

            container.addView(
                composeView,
                FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.WRAP_CONTENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT,
                    Gravity.TOP or Gravity.START
                )
            )

            root.addView(container)
            overlays[activity] = container
        }
    }

    fun detach(activity: Activity) {
        val root = activity.window?.decorView as? ViewGroup ?: return
        overlays.remove(activity)?.let { view ->
            runCatching { root.removeView(view) }
        }
    }

    fun detachFromAll() {
        overlays.keys.toList().forEach { detach(it) }
    }

    private fun onCloseClicked() {
        OverlaySettings.setEnabled(false)
        detachFromAll()
    }
}

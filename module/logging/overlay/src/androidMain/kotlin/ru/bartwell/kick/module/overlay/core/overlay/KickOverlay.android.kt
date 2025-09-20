package ru.bartwell.kick.module.overlay.core.overlay

import android.app.Application
import ru.bartwell.kick.core.data.PlatformContext
import ru.bartwell.kick.core.data.get
import ru.bartwell.kick.module.overlay.core.persists.OverlaySettings
import java.lang.ref.WeakReference

@Suppress("TooManyFunctions")
public actual object KickOverlay {
    private var installed = false
    private var appRef: WeakReference<Application>? = null
    private val callbacks = InAppOverlayCallbacks()

    public actual fun init(context: PlatformContext) {
        val app = context.get().applicationContext as? Application ?: return
        if (installed) return
        app.registerActivityLifecycleCallbacks(callbacks)
        appRef = WeakReference(app)
        installed = true
        if (OverlaySettings.isEnabled()) {
            callbacks.currentActivity.get()?.let { callbacks.attach(it) }
        }
    }

    public actual fun show(context: PlatformContext) {
        OverlaySettings.setEnabled(true)
        callbacks.currentActivity.get()?.let { callbacks.attach(it) }
    }

    public actual fun hide() {
        OverlaySettings.setEnabled(false)
        callbacks.detachFromAll()
    }
}

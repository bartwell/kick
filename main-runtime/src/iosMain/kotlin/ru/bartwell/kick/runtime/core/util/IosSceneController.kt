package ru.bartwell.kick.runtime.core.util

import androidx.compose.ui.window.ComposeUIViewController
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.arkivanov.essenty.lifecycle.create
import platform.UIKit.UIApplication
import platform.UIKit.UIModalPresentationFullScreen
import platform.UIKit.UINavigationController
import platform.UIKit.UITabBarController
import platform.UIKit.UIViewController
import ru.bartwell.kick.core.data.Module
import ru.bartwell.kick.core.data.StartScreen
import ru.bartwell.kick.runtime.App
import ru.bartwell.kick.runtime.core.component.DefaultRootComponent
import kotlin.experimental.ExperimentalNativeApi
import kotlin.native.ref.WeakReference

@OptIn(ExperimentalNativeApi::class)
internal object IosSceneController {

    private var _viewerViewControllerInstance: WeakReference<UIViewController>? = null
    private val viewerViewControllerInstance: UIViewController?
        get() = _viewerViewControllerInstance?.get()

    fun present(
        modules: List<Module>,
        startScreen: StartScreen?,
    ) {
        if (viewerViewControllerInstance != null) {
            return
        }
        val lifecycle = LifecycleRegistry()
        val componentContext = DefaultComponentContext(lifecycle)
        val rootComponent = DefaultRootComponent(
            componentContext = componentContext,
            modules = modules,
            startScreen = startScreen,
        )
        val uiViewController = ComposeUIViewController(configure = { enforceStrictPlistSanityCheck = false }) {
            App(rootComponent)
        }
        lifecycle.create()
        _viewerViewControllerInstance = WeakReference(uiViewController)
        uiViewController.modalPresentationStyle = UIModalPresentationFullScreen
        getTopMostViewController()?.presentViewController(uiViewController, animated = true, completion = null)
    }

    private fun getTopMostViewController(
        base: UIViewController? = UIApplication.sharedApplication.keyWindow?.rootViewController
    ): UIViewController? {
        if (base == null) {
            return null
        }
        return when (base) {
            is UINavigationController -> getTopMostViewController(base.visibleViewController)
            is UITabBarController -> base.selectedViewController?.let { getTopMostViewController(it) }
            else -> if (base.presentedViewController != null) {
                getTopMostViewController(base.presentedViewController)
            } else {
                base
            }
        }
    }

    fun dismiss() {
        viewerViewControllerInstance?.dismissViewControllerAnimated(true, completion = null)
        _viewerViewControllerInstance = null
    }
}

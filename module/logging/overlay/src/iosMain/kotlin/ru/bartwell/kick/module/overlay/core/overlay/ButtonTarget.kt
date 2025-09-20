package ru.bartwell.kick.module.overlay.core.overlay

import kotlinx.cinterop.ObjCAction
import platform.UIKit.UIButton
import platform.darwin.NSObject

internal class ButtonTarget(private val action: () -> Unit) : NSObject() {
    @Suppress("UnusedParameter")
    @ObjCAction
    fun invoke(sender: UIButton?) {
        action()
    }
}

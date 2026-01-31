package ru.bartwell.kick.module.firebase.analytics.core.overlay

import kotlinx.cinterop.ObjCAction
import platform.UIKit.UIButton
import platform.darwin.NSObject

internal class FirebaseButtonTarget(private val action: () -> Unit) : NSObject() {
    @Suppress("UnusedParameter")
    @ObjCAction
    fun invoke(sender: UIButton?) {
        action()
    }
}

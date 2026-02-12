package ru.bartwell.kick.core.presentation.overlay

import kotlinx.cinterop.ObjCAction
import platform.UIKit.UIButton
import platform.darwin.NSObject

@kotlinx.cinterop.BetaInteropApi
public class ButtonTarget public constructor(private val action: () -> Unit) : NSObject() {
    @Suppress("UnusedParameter")
    @ObjCAction
    public fun invoke(sender: UIButton?) {
        action()
    }
}

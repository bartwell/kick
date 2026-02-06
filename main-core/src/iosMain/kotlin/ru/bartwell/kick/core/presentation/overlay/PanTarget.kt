package ru.bartwell.kick.core.presentation.overlay

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.ObjCAction
import kotlinx.cinterop.useContents
import platform.CoreGraphics.CGPointMake
import platform.UIKit.UIPanGestureRecognizer
import platform.darwin.NSObject

@OptIn(ExperimentalForeignApi::class)
@kotlinx.cinterop.BetaInteropApi
public class PanTarget public constructor(
    private val onDelta: (dx: Double, dy: Double) -> Unit
) : NSObject() {
    @ObjCAction
    public fun onPan(gr: UIPanGestureRecognizer) {
        val v = gr.view ?: return
        val container = v.superview ?: return
        val t = gr.translationInView(container)
        val dx = t.useContents { x }
        val dy = t.useContents { y }
        onDelta(dx, dy)
        gr.setTranslation(CGPointMake(0.0, 0.0), container)
    }
}

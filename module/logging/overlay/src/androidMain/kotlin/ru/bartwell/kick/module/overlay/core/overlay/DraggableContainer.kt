package ru.bartwell.kick.module.overlay.core.overlay

import android.annotation.SuppressLint
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.widget.FrameLayout
import kotlin.math.abs

@Suppress("ReturnCount")
internal class DraggableContainer(
    context: android.content.Context
) : FrameLayout(context) {

    var dragTarget: View? = null

    private var downX = 0f
    private var downY = 0f
    private var lastX = 0f
    private var lastY = 0f
    private var dragging = false
    private val touchSlop = ViewConfiguration.get(context).scaledTouchSlop

    @Suppress("EmptyFunctionBlock")
    override fun requestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {}

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        val t = dragTarget ?: return false

        val hit = ev.x >= t.x && ev.x <= t.x + t.width &&
            ev.y >= t.y && ev.y <= t.y + t.height
        if (!hit) return false

        when (ev.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                downX = ev.rawX
                downY = ev.rawY
                dragging = false
                return false
            }
            MotionEvent.ACTION_MOVE -> {
                val dx = abs(ev.rawX - downX)
                val dy = abs(ev.rawY - downY)
                if (dx > touchSlop || dy > touchSlop) {
                    dragging = true
                    lastX = ev.rawX
                    lastY = ev.rawY
                    return true
                }
                return false
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                dragging = false
                return false
            }
        }
        return false
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val t = dragTarget ?: return false
        if (!dragging) return false

        when (event.actionMasked) {
            MotionEvent.ACTION_MOVE -> {
                val dx = event.rawX - lastX
                val dy = event.rawY - lastY
                lastX = event.rawX
                lastY = event.rawY
                t.translationX += dx
                t.translationY += dy
                return true
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                dragging = false
                return true
            }
        }
        return super.onTouchEvent(event)
    }
}

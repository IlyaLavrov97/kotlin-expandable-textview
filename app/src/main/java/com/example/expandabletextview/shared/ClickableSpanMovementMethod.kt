package com.example.expandabletextview.shared

import android.text.Selection
import android.text.Spannable
import android.text.method.BaseMovementMethod
import android.text.style.ClickableSpan
import android.view.MotionEvent
import android.view.View
import android.widget.TextView

object ClickableSpanMovementMethod : BaseMovementMethod() {
    override fun canSelectArbitrarily(): Boolean {
        return false
    }

    override fun onTouchEvent(
        widget: TextView,
        buffer: Spannable,
        event: MotionEvent
    ): Boolean {
        val action = event.actionMasked
        if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_DOWN) {
            var x = event.x.toInt()
            var y = event.y.toInt()
            x -= widget.totalPaddingLeft
            y -= widget.totalPaddingTop
            x += widget.scrollX
            y += widget.scrollY
            val line: Int = widget.layout.getLineForVertical(y)
            val off: Int = widget.layout.getOffsetForHorizontal(line, x.toFloat())
            val link =
                buffer.getSpans(off, off, ClickableSpan::class.java)
            when {
                link.isNotEmpty() -> {
                    if (action == MotionEvent.ACTION_UP) {
                        link[0].onClick(widget)
                    } else {
                        Selection.setSelection(
                            buffer, buffer.getSpanStart(link[0]),
                            buffer.getSpanEnd(link[0])
                        )
                    }
                    return true
                }
                action == MotionEvent.ACTION_UP -> {
                    (widget.parent as View).performClick()
                }
                else -> {
                    Selection.removeSelection(buffer)
                }
            }
        }
        return false
    }

    override fun initialize(widget: TextView, text: Spannable) {
        Selection.removeSelection(text)
    }
}
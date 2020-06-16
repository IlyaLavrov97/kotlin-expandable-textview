package com.example.expandabletextview.shared

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Color
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextPaint
import android.text.style.ClickableSpan
import android.util.AttributeSet
import android.view.View
import android.view.ViewTreeObserver
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import com.example.expandabletextview.R

class ExpandableTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatTextView(context, attrs, defStyleAttr) {

    private var textStateChangedListener: TextStateChangedListener? = null

    private lateinit var currentText: CharSequence
    private var state: TextState

    private var bufferType: BufferType? = null
    private var trimLength: Int
    private var trimCollapsedText: CharSequence
    private var trimExpandedText: CharSequence
    private val viewMoreSpan: ExpandableClickableSpan
    private var colorClickableText: Int
    private val showTrimExpandedText: Boolean
    private var trimMode: Int
    private var lineEndIndex = 0
    private var trimLines: Int

    init {
        val typedArray: TypedArray =
            context.obtainStyledAttributes(attrs, R.styleable.ExpandableTextView)
        trimLength =
            typedArray.getInt(R.styleable.ExpandableTextView_trimLength, DEFAULT_TRIM_LENGTH)
        val resourceIdTrimCollapsedText =
            typedArray.getResourceId(
                R.styleable.ExpandableTextView_trimCollapsedText,
                R.string.see_more
            )
        val resourceIdTrimExpandedText =
            typedArray.getResourceId(
                R.styleable.ExpandableTextView_trimExpandedText,
                R.string.see_less
            )
        trimCollapsedText = resources.getString(resourceIdTrimCollapsedText)
        trimExpandedText = resources.getString(resourceIdTrimExpandedText)
        trimLines =
            typedArray.getInt(R.styleable.ExpandableTextView_trimLines, DEFAULT_TRIM_LINES)
        colorClickableText = typedArray.getColor(
            R.styleable.ExpandableTextView_colorClickableText,
            ContextCompat.getColor(context, R.color.colorAccent)
        )
        showTrimExpandedText = typedArray.getBoolean(
            R.styleable.ExpandableTextView_showTrimExpandedText,
            DEFAULT_SHOW_TRIM_EXPANDED_TEXT
        )
        trimMode =
            typedArray.getInt(R.styleable.ExpandableTextView_trimMode, TRIM_MODE_LINES)
        typedArray.recycle()

        state = TextState.Expanded

        viewMoreSpan = ExpandableClickableSpan()
        onGlobalLayoutLineEndIndex()
        setText()
    }

    fun setText(text: CharSequence?, state: TextState) {
        this.state = state
        this.text = text
    }

    private fun setText() {
        super.setText(getDisplayableText(), bufferType)
        movementMethod = ClickableSpanMovementMethod
        highlightColor = Color.TRANSPARENT
    }

    override fun setText(text: CharSequence?, type: BufferType) {
        this.currentText = text ?: ""
        bufferType = type

        // we should refresh layout if new text has been set
        if (layout != null) {
            super.setText(currentText, bufferType)
            refreshLineEndIndex()
        }

        setText()
    }

    private fun getDisplayableText(): CharSequence {
        if (isLengthTrim() || isLineTrim()) {
            return when (state) {
                TextState.Expanded -> {
                    getExpandedText()
                }
                TextState.Collapsed -> {
                    getCollapsedText()
                }
            }
        }
        return currentText
    }

    private fun isLengthTrim(): Boolean =
        trimMode == TRIM_MODE_LENGTH && currentText.length > trimLength

    private fun isLineTrim(): Boolean =
        trimMode == TRIM_MODE_LINES && lineEndIndex > 0

    private fun getCollapsedText(): CharSequence {
        val trimEndIndex = when (trimMode) {
            TRIM_MODE_LINES -> {
                if (!needLineCollapse()) return currentText

                var resultEndIndex = lineEndIndex - 1
                if (layout.getLineWidth(trimLines - 1) > measuredWidth / 2) {
                    resultEndIndex =
                        lineEndIndex - (ELLIPSIZE.length + trimCollapsedText.length + 1)
                    for (i in resultEndIndex downTo 0) {
                        if (currentText[i] == ' ') {
                            resultEndIndex = i
                            break
                        }
                    }
                }
                resultEndIndex
            }
            TRIM_MODE_LENGTH -> {
                trimLength + 1
            }
            else -> {
                currentText.length
            }
        }

        val resultText = SpannableStringBuilder(currentText, 0, trimEndIndex)
            .append(ELLIPSIZE)
            .append(trimCollapsedText)
        return setClickableSpanToEnd(resultText, trimCollapsedText)
    }

    private fun needLineCollapse(): Boolean = layout.lineCount > trimLines

    private fun getExpandedText(): CharSequence {
        if (showTrimExpandedText) {
            val resultText = SpannableStringBuilder(
                currentText,
                0,
                currentText.length
            ).append(trimExpandedText)
            return setClickableSpanToEnd(resultText, trimExpandedText)
        }
        return currentText
    }

    private fun setClickableSpanToEnd(
        s: SpannableStringBuilder,
        trimText: CharSequence
    ): CharSequence {
        s.setSpan(
            viewMoreSpan,
            s.length - trimText.length,
            s.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        return s
    }

    fun setTrimLines(value: Int) {
        trimLines = value
    }

    fun setTrimLength(value: Int) {
        trimLength = value
    }

    fun setTextStateChangedListener(textStateChangedListener: TextStateChangedListener?) {
        this.textStateChangedListener = textStateChangedListener
    }

    private fun onGlobalLayoutLineEndIndex() {
        if (trimMode == TRIM_MODE_LINES) {
            viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
                override fun onPreDraw(): Boolean {
                    return layout?.let {
                        viewTreeObserver.removeOnPreDrawListener(this)
                        refreshLineEndIndex()
                        setText()
                        false
                    } ?: true
                }
            })
        }
    }

    private fun refreshLineEndIndex() {
        try {
            lineEndIndex = when (trimLines) {
                0 -> {
                    layout.getLineEnd(0)
                }
                in 1..lineCount -> {
                    layout.getLineEnd(trimLines - 1)
                }
                else -> {
                    INVALID_END_INDEX
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private inner class ExpandableClickableSpan : ClickableSpan() {
        override fun onClick(widget: View) {
            state = when (state) {
                TextState.Expanded -> TextState.Collapsed
                TextState.Collapsed -> TextState.Expanded
            }
            textStateChangedListener?.stateChanged(state)

            setText()
        }

        override fun updateDrawState(ds: TextPaint) {
            ds.color = colorClickableText
        }
    }

    companion object {
        private const val TRIM_MODE_LINES = 0
        private const val TRIM_MODE_LENGTH = 1
        private const val DEFAULT_TRIM_LENGTH = 240
        private const val DEFAULT_TRIM_LINES = 2
        private const val INVALID_END_INDEX = -1
        private const val DEFAULT_SHOW_TRIM_EXPANDED_TEXT = true
        private const val ELLIPSIZE = "... "
    }
}
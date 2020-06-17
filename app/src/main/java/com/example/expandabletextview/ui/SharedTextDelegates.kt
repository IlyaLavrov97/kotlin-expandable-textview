package com.example.expandabletextview.ui

import com.example.expandabletextview.R
import com.example.expandabletextview.shared.TextState
import com.example.expandabletextview.shared.TextStateChangedListener
import com.example.expandabletextview.vo.LengthTextVO
import com.example.expandabletextview.vo.LineTextVO
import com.example.expandabletextview.vo.TextVO
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateLayoutContainer
import kotlinx.android.synthetic.main.item_length_trim_textview.*
import kotlinx.android.synthetic.main.item_line_trim_textview.*

object SharedTextDelegates {
    fun lengthTrimAdapterDelegate(itemClickedListener: (TextVO) -> Unit) =
        adapterDelegateLayoutContainer<LengthTextVO, TextVO>(R.layout.item_length_trim_textview) {
            lengthContentText.setTextStateChangedListener(object : TextStateChangedListener {
                override fun stateChanged(newState: TextState) {
                    item.state = newState
                }
            })
            itemView.setOnClickListener {
                itemClickedListener(item)
            }
            bind {
                lengthInfoTextView.text =
                    context.getString(R.string.length_format).format(item.length)
                lengthContentText.setTrimLength(item.length)
                lengthContentText.setText(item.content, item.state)
            }
        }

    fun lineTrimAdapterDelegate(itemClickedListener: (TextVO) -> Unit) =
        adapterDelegateLayoutContainer<LineTextVO, TextVO>(R.layout.item_line_trim_textview) {
            lineContentText.setTextStateChangedListener(object : TextStateChangedListener {
                override fun stateChanged(newState: TextState) {
                    item.state = newState
                }
            })
            itemView.setOnClickListener {
                itemClickedListener(item)
            }
            bind {
                linesCountTextView.text =
                    context.getString(R.string.lines_count_format).format(item.lineCount)
                lineContentText.setTrimLines(item.lineCount)
                lineContentText.setText(item.content, item.state)
            }
        }
}
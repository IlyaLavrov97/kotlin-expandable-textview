package com.example.expandabletextview.vo

import com.example.expandabletextview.shared.TextState

data class LineTextVO(
    override val content: String,
    override var state: TextState = TextState.Collapsed,
    val lineCount: Int
) : TextVO()
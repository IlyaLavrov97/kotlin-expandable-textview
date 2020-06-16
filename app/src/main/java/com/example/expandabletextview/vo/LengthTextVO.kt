package com.example.expandabletextview.vo

import com.example.expandabletextview.shared.TextState

data class LengthTextVO(
    override val content: String,
    override var state: TextState = TextState.Collapsed,
    val length: Int
) : TextVO()
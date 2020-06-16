package com.example.expandabletextview.vo

import com.example.expandabletextview.shared.TextState

abstract class TextVO {
    abstract val content: String
    abstract var state: TextState
}
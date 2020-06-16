package com.example.expandabletextview

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.expandabletextview.shared.TextState
import com.example.expandabletextview.shared.TextStateChangedListener
import com.example.expandabletextview.vo.LengthTextVO
import com.example.expandabletextview.vo.LineTextVO
import com.example.expandabletextview.vo.TextVO
import com.hannesdorfmann.adapterdelegates4.ListDelegationAdapter
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateLayoutContainer
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.item_length_trim_textview.*
import kotlinx.android.synthetic.main.item_line_trim_textview.*
import kotlinx.android.synthetic.main.item_line_trim_textview.contentText

class MainActivity : AppCompatActivity() {

    private val delegationAdapter = ListDelegationAdapter(
        lengthTrimAdapterDelegate(::showDetails),
        lineTrimAdapterDelegate(::showDetails)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        delegationAdapter.items = mutableListOf(
            LengthTextVO(resources.getString(R.string.default_text), length = 50),
            LineTextVO(resources.getString(R.string.default_text), lineCount = 2),
            LengthTextVO(resources.getString(R.string.default_text), length = 70),
            LineTextVO(resources.getString(R.string.default_text), lineCount = 1),
            LengthTextVO(resources.getString(R.string.default_text), length = 100),
            LineTextVO(resources.getString(R.string.default_text), lineCount = 3),
            LengthTextVO(resources.getString(R.string.default_text), length = 200),
            LineTextVO(resources.getString(R.string.default_text), lineCount = 5),
            LengthTextVO(resources.getString(R.string.default_text), length = 150),
            LineTextVO(resources.getString(R.string.default_text), lineCount = 4),
            LengthTextVO(resources.getString(R.string.default_text), length = 112),
            LineTextVO(resources.getString(R.string.default_text), lineCount = 3)
        )

        mainRecyclerView.run {
            adapter = delegationAdapter
            layoutManager = LinearLayoutManager(this@MainActivity)
        }
    }

    private fun showDetails(textVO: TextVO) {
        Toast.makeText(this, textVO.content, Toast.LENGTH_SHORT).show()
    }

    private fun lengthTrimAdapterDelegate(itemClickedListener: (TextVO) -> Unit) =
        adapterDelegateLayoutContainer<LengthTextVO, TextVO>(R.layout.item_length_trim_textview) {
            contentText.setTextStateChangedListener(object : TextStateChangedListener {
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
                contentText.setTrimLength(item.length)
                contentText.setText(item.content, item.state)
            }
        }

    private fun lineTrimAdapterDelegate(itemClickedListener: (TextVO) -> Unit) =
        adapterDelegateLayoutContainer<LineTextVO, TextVO>(R.layout.item_line_trim_textview) {
            contentText.setTextStateChangedListener(object : TextStateChangedListener {
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
                contentText.setTrimLines(item.lineCount)
                contentText.setText(item.content, item.state)
            }
        }
}

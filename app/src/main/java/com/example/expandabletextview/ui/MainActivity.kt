package com.example.expandabletextview.ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.expandabletextview.R
import com.example.expandabletextview.ui.SharedTextDelegates.lengthTrimAdapterDelegate
import com.example.expandabletextview.ui.SharedTextDelegates.lineTrimAdapterDelegate
import com.example.expandabletextview.vo.LengthTextVO
import com.example.expandabletextview.vo.LineTextVO
import com.example.expandabletextview.vo.TextVO
import com.hannesdorfmann.adapterdelegates4.ListDelegationAdapter
import kotlinx.android.synthetic.main.activity_main.*

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
}

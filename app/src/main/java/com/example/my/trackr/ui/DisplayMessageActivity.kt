package com.example.my.trackr.ui

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.example.my.trackr.R
import kotlinx.android.synthetic.main.activity_display_message.*

const val EXTRA_MESSAGE = "message"

class DisplayMessageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_display_message)

        val message = intent.getStringExtra(EXTRA_MESSAGE)
        textView.text = message
    }
}

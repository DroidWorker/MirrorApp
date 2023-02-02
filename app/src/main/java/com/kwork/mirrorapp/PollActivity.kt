package com.kwork.mirrorapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class PollActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_poll)
    }

    fun onClick(v: View){
        this.finish()
    }
}
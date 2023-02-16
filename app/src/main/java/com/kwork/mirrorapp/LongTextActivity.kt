package com.kwork.mirrorapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.webkit.WebView
import androidx.activity.viewModels
import com.kwork.mirrorapp.VM.MainViewModel

class LongTextActivity : AppCompatActivity() {
    private val viewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_long_text)

        val type = intent.getStringExtra("type") ?: "privacy"

        val wv = findViewById<WebView>(R.id.tvWebView)
        val text = viewModel.getTexts(type)
        println("sdsds"+text)
        wv.loadData(text, "text/html", "en_US");
    }

    fun onCloseClick(v : View){
        finish()
    }
}
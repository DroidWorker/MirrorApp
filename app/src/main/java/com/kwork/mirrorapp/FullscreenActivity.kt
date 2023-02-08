package com.kwork.mirrorapp

import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.activity.viewModels
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.lifecycleScope
import com.kwork.mirrorapp.VM.MainViewModel
import com.kwork.mirrorapp.adapters.ImageAdapter
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class FullscreenActivity : AppCompatActivity() {
    private val userViewModel by lazy { ViewModelProviders.of(this).get(MainViewModel::class.java)}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fullscreen)
        val path = intent.getStringExtra("imgPath")

        val imageView = findViewById<ImageView>(R.id.fullscreenImage)
        imageView.setImageBitmap(BitmapFactory.decodeFile(path))
    }

    fun onBackClick(v : View){
        finish()
    }
}
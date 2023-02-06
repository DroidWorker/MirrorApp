package com.kwork.mirrorapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.kwork.mirrorapp.VM.MainViewModel
import com.kwork.mirrorapp.adapters.ImageAdapter
import kotlinx.coroutines.launch

class FullscreenActivity : AppCompatActivity() {
    private val userViewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fullscreen)

        val imageView = findViewById<ImageView>(R.id.fullscreenImage)
        lifecycleScope.launch{
            userViewModel.currentImage.collect{
                if(it!=null)imageView.setImageBitmap(it)
            }
        }
    }
}
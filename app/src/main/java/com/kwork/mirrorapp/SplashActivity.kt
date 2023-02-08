package com.kwork.mirrorapp

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.Animation
import android.view.animation.Animation.AnimationListener
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.kwork.mirrorapp.VM.MainViewModel

class SplashActivity : AppCompatActivity() {
    private val viewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val tv = findViewById<TextView>(R.id.splashText)
        tv.text = viewModel.splashText
        tv.setOnClickListener(View.OnClickListener {
            val anim = AnimationUtils.loadAnimation(this, R.anim.diagonal)
            anim.fillAfter = true
            findViewById<TextView>(R.id.splashText).startAnimation(anim)
            anim.setAnimationListener(object : AnimationListener {
                override fun onAnimationStart(animation: Animation) {}
                override fun onAnimationEnd(animation: Animation) {
                    finish()
                    overridePendingTransition(R.anim.diagonal,R.anim.alpha)
                }

                override fun onAnimationRepeat(animation: Animation) {}
            })
        })
        Handler(Looper.getMainLooper()).postDelayed({
            close()
        }, 3000) // 3000 is the delayed time in milliseconds.
    }

    fun close(){
        val anim = AnimationUtils.loadAnimation(this, R.anim.diagonal)
        anim.fillAfter = true
        findViewById<TextView>(R.id.splashText).startAnimation(anim)
        anim.setAnimationListener(object : AnimationListener {
            override fun onAnimationStart(animation: Animation) {}
            override fun onAnimationEnd(animation: Animation) {
                finish()
                overridePendingTransition(R.anim.diagonal,R.anim.alpha)
            }

            override fun onAnimationRepeat(animation: Animation) {}
        })
    }
}
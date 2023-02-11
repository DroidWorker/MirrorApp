package com.kwork.mirrorapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.viewModels
import androidx.viewpager.widget.ViewPager
import com.kwork.mirrorapp.VM.MainViewModel
import com.kwork.mirrorapp.adapters.OnboardingViewAdapter
import com.tbuonomo.viewpagerdotsindicator.SpringDotsIndicator

class OnboardingActivity : AppCompatActivity() {
    private val userViewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)

        val later = findViewById<TextView>(R.id.onboardingLater)
        val recover = findViewById<TextView>(R.id.onboardingRecover)
        val pager = findViewById<ViewPager>(R.id.view_pager)
        val indicator = findViewById<SpringDotsIndicator>(R.id.dots_indicator)
        val adapter = OnboardingViewAdapter(this)
        val button = findViewById<Button>(R.id.onboarding_next)
        val close = findViewById<ImageButton>(R.id.closeOnboarding)
        pager.adapter = adapter
        indicator.attachTo(pager)
        button.setOnClickListener(View.OnClickListener {
            if(adapter.currentItem!=5)
                adapter.currentItem++
            else {
                //TODO("end onboarding")
                finish()
                return@OnClickListener
            }
            if (adapter.currentItem==5){
                button.text = this.resources.getString(R.string.start_free)
                close.visibility = View.GONE
                later.visibility = View.VISIBLE
                recover.visibility = View.VISIBLE
            }
            pager.currentItem = adapter.currentItem
        })

        later.setOnClickListener(View.OnClickListener {
            val pollIntent = Intent(this@OnboardingActivity, PollActivity::class.java)
            finish()
            startActivity(pollIntent)
        })

        close.setOnClickListener{
            val payIntent = Intent(this@OnboardingActivity, PayActivity::class.java)
            finish()
            startActivity(payIntent)
        }
    }
}
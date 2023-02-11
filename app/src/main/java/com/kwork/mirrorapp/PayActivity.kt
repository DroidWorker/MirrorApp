package com.kwork.mirrorapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.RadioButton
import androidx.constraintlayout.widget.ConstraintLayout

class PayActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pay)

        val p1 = findViewById<ConstraintLayout>(R.id.p1)
        val p2 = findViewById<ConstraintLayout>(R.id.p2)
        val rb1 = findViewById<RadioButton>(R.id.radioButton1)
        val rb2 = findViewById<RadioButton>(R.id.radioButton2)

        p1.setOnClickListener{
            p1.setBackgroundResource(R.drawable.rounded_corners)
            rb1.setBackgroundResource(R.drawable.radio_button_checked)
            p2.setBackgroundResource(R.drawable.rounded_corners_off)
            rb2.setBackgroundResource(R.drawable.rounded_transparent)
        }
        p2.setOnClickListener{
            p2.setBackgroundResource(R.drawable.rounded_corners)
            rb2.setBackgroundResource(R.drawable.radio_button_checked)
            p1.setBackgroundResource(R.drawable.rounded_corners_off)
            rb1.setBackgroundResource(R.drawable.rounded_transparent)
        }
    }
}
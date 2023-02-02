package com.kwork.mirrorapp

import android.Manifest.permission.CAMERA
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class CameraAccessActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera_access)
    }

    fun getAccess(v: View){
        requestPermissions(arrayOf(CAMERA), 100)
        finish()
    }
}
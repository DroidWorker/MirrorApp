package com.kwork.mirrorapp

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.kwork.mirrorapp.tools.TimelineView
import java.io.File

class VideoActivity : AppCompatActivity() {
    lateinit var timeline : TimelineView
    lateinit var videoView : VideoView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video)

        val mode  = intent.getStringExtra("mode") ?: "default"
        val path = intent.getStringExtra("imgPath") ?: ""
        if (path==""){
            Toast.makeText(this, "Ошибка видео", Toast.LENGTH_LONG).show()
            finish()
        }
        val videoUri: Uri = Uri.fromFile(File(getExternalFilesDir(null), "videos/video.mp4"))//Uri.parse("path/to/video.mp4")

        videoView = findViewById(R.id.videoView)
        timeline = findViewById(R.id.timelineView)
        timeline.setVideoUri(videoUri)
        videoView.setVideoURI(videoUri)

        //needed below line to properly pass the seek duration or if you dont set it you will get percent value
        //the seekMillis value will be icorrect in the callback

        timeline.setTotalDuration(videoView.duration.toLong())
        videoView.start()

        timeline.callback = object : TimelineView.Callback{
            override fun onSeek(position: Float, seekMillis: Long) {

            }

            override fun onSeekStart(position: Float, seekMillis: Long) {
            }

            override fun onStopSeek(position: Float, seekMillis: Long) {
            }

            override fun onLeftProgress(leftPos: Float, seekMillis: Long) {
            }

            override fun onRightProgress(rightPos: Float, seekMillis: Long) {
            }
        }
    }

    fun onBackClick(v: View){
        finish()
    }
}
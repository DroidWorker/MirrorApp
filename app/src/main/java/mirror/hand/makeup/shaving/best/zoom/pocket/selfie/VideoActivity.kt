package mirror.hand.makeup.shaving.best.zoom.pocket.selfie

import android.content.Intent
import android.graphics.Bitmap
import android.media.MediaPlayer
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.ImageButton
import android.widget.Toast
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity
import mirror.hand.makeup.shaving.best.zoom.pocket.selfie.tools.TimelineView
import java.io.*

class VideoActivity : AppCompatActivity() {
    lateinit var timeline : TimelineView
    lateinit var videoView : VideoView

    var saveImg = false
    lateinit var mode: String
    lateinit var path: String

    var isPlay = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video)

        mode  = intent.getStringExtra("mode") ?: "default"
        path = intent.getStringExtra("imgPath") ?: ""
        if (path==""){
            Toast.makeText(this, "Ошибка видео", Toast.LENGTH_LONG).show()
            finish()
        }
        val videoUri: Uri = Uri.fromFile(File(path))//Uri.parse("path/to/video.mp4")


        videoView = findViewById(R.id.videoView)
        timeline = findViewById(R.id.timelineView)
        timeline.setVideoUri(videoUri)
        videoView.setVideoURI(videoUri)

        //needed below line to properly pass the seek duration or if you dont set it you will get percent value
        //the seekMillis value will be icorrect in the callback

        timeline.setTotalDuration(videoView.duration.toLong())
        videoView.start()


        videoView.setOnCompletionListener(object : MediaPlayer.OnCompletionListener {
            override fun onCompletion(mp: MediaPlayer?) {
                videoView.seekTo(0)
            }
        })

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

    override fun onDestroy() {
        super.onDestroy()
        if (!saveImg && mode=="preview"){//preview mode remove image
            if(path!=null) deleteFileByAbsolutePath(path!!)
        }
    }

    fun onPausePlayClick(v: View){
        if (isPlay) {
            videoView.pause()
            (v as ImageButton).setImageResource(R.drawable.play)
            isPlay = false
        }else{
            videoView.start()
            (v as ImageButton).setImageResource(R.drawable.pause)
            isPlay = true
        }
    }

    fun onSaveClick(v: View){
        if (mode == "preview") {
            saveImg = true
            Toast.makeText(this, "Сохранено", Toast.LENGTH_SHORT).show()
            finish()
        }
        else{
            val file = File(path)

            try {
                val src = FileInputStream(File(filesDir, "video.mp4"))
                val dst = FileOutputStream(file)
                src.copyTo(dst)
                src.close()
                dst.close()

                // обновите галерею, чтобы добавить сохраненное видео
                MediaScannerConnection.scanFile(
                    applicationContext,
                    arrayOf(file.toString()),
                    arrayOf("video/*"),
                    null
                )
                Toast.makeText(this, "Сохранено в галерею", Toast.LENGTH_SHORT).show()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    fun onShareClick(v: View){
        try {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "video/*"
            shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(File(path)))
            startActivity(Intent.createChooser(shareIntent, "Поделиться видео"))
        }
        catch (ex: Exception){
            println("sharevidoe"+ex.stackTraceToString())
        }
    }

    fun onBackClick(v: View){
        finish()
    }

    private fun deleteFileByAbsolutePath(filePath: String) {
        val file = File(filePath)
        if (file.exists()) {
            file.delete()
        }
    }
}
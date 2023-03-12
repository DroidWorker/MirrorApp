package mirror.hand.makeup.shaving.best.zoom.pocket.selfie

import android.content.Intent
import android.graphics.Bitmap
import android.location.GnssAntennaInfo
import android.media.MediaPlayer
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.Toast
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.google.android.exoplayer2.ExoPlaybackException
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.PlayerView
import mirror.hand.makeup.shaving.best.zoom.pocket.selfie.tools.TimelineView
import java.io.*
import java.util.*

class VideoActivity : AppCompatActivity() {
    lateinit var timeline : TimelineView
    lateinit var playerView : PlayerView
    lateinit var exoplayer : ExoPlayer
    var videoViewDuration = 0

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

        timeline = findViewById(R.id.timelineView)
        timeline.setVideoUri(videoUri)

        //needed below line to properly pass the seek duration or if you dont set it you will get percent value
        //the seekMillis value will be icorrect in the callback

        exoplayer = ExoPlayer.Builder(this).build()
        val mediaItem = MediaItem.fromUri(videoUri)
        exoplayer.setMediaItem(mediaItem)
        exoplayer.prepare()

        playerView = findViewById<PlayerView>(R.id.videoView)

// Настраиваем PlayerView, чтобы он отображал ExoPlayer
        playerView.player = exoplayer
        exoplayer.playWhenReady = true

        exoplayer.addListener(object : Player.Listener {
            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                super.onPlayerStateChanged(playWhenReady, playbackState)
                when(playbackState) {
                    Player.STATE_ENDED -> {
                        // Перематываем в начало видео после его окончания
                        exoplayer.seekTo(0)
                        isPlay = false
                        findViewById<ImageButton>(R.id.imageButton6).setImageResource(R.drawable.play)
                        exoplayer.playWhenReady = false
                    }
                    Player.STATE_READY -> {
                        timeline.setTotalDuration(exoplayer.duration)
                        if (exoplayer.playWhenReady)timeline.smoothProgress()
                    }
                }
            }
        })

        timeline.callback = object : TimelineView.Callback{
            override fun onSeek(position: Float, seekMillis: Long) {

            }

            override fun onSeekStart(position: Float, seekMillis: Long) {
            }

            override fun onStopSeek(position: Float, seekMillis: Long) {
                exoplayer.seekTo(seekMillis)
                if(isPlay)timeline.smoothProgress()
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

    override fun onPause() {
        super.onPause()
        //finish()
        if(isPlay)onPausePlayClick(findViewById(R.id.imageButton6))
    }

    fun onPausePlayClick(v: View){
        if (isPlay) {
            exoplayer.pause()
            timeline.pause(exoplayer.currentPosition)
            (v as ImageButton).setImageResource(R.drawable.play)
            isPlay = false
        }else{
            exoplayer.playWhenReady = true
            if (timeline.animation!=null)timeline.resume()
            else timeline.smoothProgress()
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
            val fileUri = FileProvider.getUriForFile(this, "${this.packageName}.provider", File(path))
            shareIntent.type = "video/*"
            shareIntent.putExtra(Intent.EXTRA_STREAM, fileUri)
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
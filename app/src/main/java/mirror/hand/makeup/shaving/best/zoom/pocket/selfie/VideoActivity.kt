package mirror.hand.makeup.shaving.best.zoom.pocket.selfie

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.GnssAntennaInfo
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.Toast
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.viewpager.widget.ViewPager
import com.google.android.exoplayer2.ExoPlaybackException
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.PlayerView
import mirror.hand.makeup.shaving.best.zoom.pocket.selfie.adapters.VideoPagerAdapter
import mirror.hand.makeup.shaving.best.zoom.pocket.selfie.fragment.VideoFragment
import mirror.hand.makeup.shaving.best.zoom.pocket.selfie.tools.TimelineView
import java.io.*
import java.util.*
import kotlin.collections.ArrayList

class VideoActivity : AppCompatActivity() {
    lateinit var timeline : TimelineView
    lateinit var playerView : PlayerView
    lateinit var videoPager : ViewPager
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
        videoPager = findViewById<ViewPager>(R.id.videoPager)
        if (path==""){
            Toast.makeText(this, "Ошибка видео", Toast.LENGTH_LONG).show()
            finish()
        }
        val pathList =
            if (mode=="preview") listOf(path)
            else getVideoFromFolder()

        val videoPagerAdapter = VideoPagerAdapter(supportFragmentManager, pathList.toList())
        videoPagerAdapter.mode = mode
        videoPager.adapter = videoPagerAdapter
        if (mode=="default"){
            videoPager.setCurrentItem(pathList.indexOf(path), false)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (!saveImg && mode=="preview"){//preview mode remove image
            if(path!=null) deleteFileByAbsolutePath(path!!)
        }
    }

    fun  getVideoFromFolder(): List<String>{
        val folder = File(applicationContext.getExternalFilesDir(null), "videos")
        val vids = ArrayList<String>()
        if (folder.exists()) {
            for (file in folder.listFiles()) {
                if (file.isFile) {
                    if (file.exists() && file.length() == 0L) {
                        file.delete()
                    }else {
                        vids.add(file.absolutePath)
                    }
                }
            }
        }
        return vids
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
            Toast.makeText(this, getString(R.string.saved), Toast.LENGTH_SHORT).show()
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
                Toast.makeText(this, getString(R.string.saved_to_gallery), Toast.LENGTH_SHORT).show()
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
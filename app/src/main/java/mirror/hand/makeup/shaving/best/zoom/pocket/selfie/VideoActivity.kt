package mirror.hand.makeup.shaving.best.zoom.pocket.selfie

import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaScannerConnection
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.ui.PlayerView
import mirror.hand.makeup.shaving.best.zoom.pocket.selfie.adapters.VideoPagerAdapter
import mirror.hand.makeup.shaving.best.zoom.pocket.selfie.tools.TimelineView
import java.io.*
import kotlin.collections.ArrayList

class VideoActivity : AppCompatActivity() {
    lateinit var videoPager : ViewPager2

    var saveImg = false
    lateinit var mode: String
    lateinit var path: String
    private var direction: Boolean = true//true - default
    private var currentItemId = 0
    var isPlay = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video)

        mode  = intent.getStringExtra("mode") ?: "default"
        path = intent.getStringExtra("imgPath") ?: ""
        direction = intent.getBooleanExtra("direction", true)

        videoPager = findViewById<ViewPager2>(R.id.videoPager)
        val pathList =
            if (mode=="preview") listOf(path)
            else getVideoFromFolder()
        if(pathList.isNullOrEmpty()) return
        if (path==""&&mode!="inrow"){
            Toast.makeText(this, "Ошибка видео", Toast.LENGTH_LONG).show()
            finish()
        }else if(mode=="inrow"){
            path = pathList.first()
        }
        val videoPagerAdapter = VideoPagerAdapter(this, pathList.toList())
        videoPagerAdapter.mode = mode
        var settled = false
        videoPager.adapter = videoPagerAdapter
        videoPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                currentItemId = position
            }
            override fun onPageScrollStateChanged(state: Int) {
                if (state == ViewPager2.SCROLL_STATE_DRAGGING) {
                    settled = false
                }
                if (state == ViewPager2.SCROLL_STATE_SETTLING) {
                    settled = true
                }
                if (state == ViewPager2.SCROLL_STATE_IDLE && !settled) {
                    val images = getImagesFromFolder()
                    if(images.isNotEmpty()) {
                        val intent = Intent(this@VideoActivity, FullscreenActivity::class.java)
                        if(currentItemId==0){
                            intent!!.putExtra("imgPath", images.last())
                        }
                        else {
                            intent!!.putExtra("imgPath", images.first())
                        }
                        startActivity(intent)
                        finish()
                    }
                }
            }
        })
        if (mode=="default"){
            videoPager.setCurrentItem(pathList.indexOf(path), false)
        }else if(mode=="inrow"&&!direction){
            videoPager.setCurrentItem(pathList.size-1, false)
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

    fun getImagesFromFolder(): List<String> {
        val folder = File(applicationContext.getExternalFilesDir(null), "mirrorImages")
        val images = mutableListOf<String>()
        if (folder.exists()) {
            for (file in folder.listFiles()) {
                try {
                    images.add(file.absolutePath)
                }catch (ex: Exception){}
            }
        }
        return images
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
               scanMedia(file)
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

    fun scanMedia(mediaFile: File) {
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, mediaFile.name)
            put(MediaStore.MediaColumns.MIME_TYPE, "video/mp4")
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DCIM)
        }

        val contentResolver = this.contentResolver
        val uri = contentResolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, contentValues)
        uri?.let { outputStream ->
            contentResolver.openOutputStream(outputStream)?.use { output ->
                FileInputStream(mediaFile).use { input ->
                    input.copyTo(output)
                }
            }
        }
    }

    private fun deleteFileByAbsolutePath(filePath: String) {
        val file = File(filePath)
        if (file.exists()) {
            file.delete()
        }
    }
}
package mirror.hand.makeup.shaving.best.zoom.pocket.selfie

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.android.material.color.utilities.Score.score
import java.io.ByteArrayOutputStream
import java.io.File


class FullscreenActivity : AppCompatActivity() {
    var saveImg = false
    lateinit var mode : String
    private var path: String?  = null
    lateinit var ctx : Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(mirror.hand.makeup.shaving.best.zoom.pocket.selfie.R.layout.activity_fullscreen)
        ctx = this
        path = intent.getStringExtra("imgPath")
        mode = intent.getStringExtra("mode") ?: "default"

        val adView = findViewById<AdView>(mirror.hand.makeup.shaving.best.zoom.pocket.selfie.R.id.adViewFullscreen)
        MobileAds.initialize(
            this
        ) { }
        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)

        val imageView = findViewById<ImageView>(mirror.hand.makeup.shaving.best.zoom.pocket.selfie.R.id.fullscreenImage)
        imageView.setImageBitmap(BitmapFactory.decodeFile(path))

        if (mode=="preview") {
            findViewById<ImageButton>(R.id.imageButton3).visibility = View.VISIBLE
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (!saveImg && mode=="preview"){//preview mode remove image
            if(path!=null) deleteFileByAbsolutePath(path!!)
        }
    }

    fun onSaveClick(v: View){
        if(mode=="default"){//default mode save image to phone gallery
            val file = File(path)
            MediaScannerConnection.scanFile(
                this,
                arrayOf(file.absolutePath),
                null
            ) { _, uri ->
                // ???????????????????? uri ?? ?????????????? ?????????????????? ??????????????
                runOnUiThread {
                    val toast = Toast.makeText(ctx, "?????????????????????? ??????????????????", Toast.LENGTH_SHORT)
                    toast.show()
                    finish()
                }
            }
        }
        saveImg = true
        val toast = Toast.makeText(ctx, "?????????????????????? ??????????????????", Toast.LENGTH_SHORT)
        toast.show()
        finish()
    }

    fun onBackClick(v : View){
        finish()
    }

    fun onShareClick(v: View){
        shareImage(BitmapFactory.decodeFile(path))
    }

    private fun deleteFileByAbsolutePath(filePath: String) {
        val file = File(filePath)
        if (file.exists()) {
            file.delete()
        }
    }

    private fun shareImage(bitmap: Bitmap) {
        val bytes = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path: String = MediaStore.Images.Media.insertImage(
            this.contentResolver,
            bitmap,
            "sImg",
            null
        )
        val uri = Uri.parse(path)
        val shareIntent = Intent()
        shareIntent.action = Intent.ACTION_SEND
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri)
        shareIntent.type = "image/*"
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        startActivity(Intent.createChooser(shareIntent, "send"))
    }

}
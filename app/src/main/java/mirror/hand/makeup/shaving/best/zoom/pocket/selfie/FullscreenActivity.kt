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
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.android.material.color.utilities.Score.score
import mirror.hand.makeup.shaving.best.zoom.pocket.selfie.VM.MainViewModel
import mirror.hand.makeup.shaving.best.zoom.pocket.selfie.adapters.PhotoPagerAdapter
import java.io.ByteArrayOutputStream
import java.io.File


class FullscreenActivity : AppCompatActivity() {
    private val viewModel by viewModels<MainViewModel>()

    var saveImg = false
    lateinit var mode : String
    private var path: String?  = null
    lateinit var ctx : Context
    lateinit var imgs : Map<String, Bitmap>
    lateinit var adapter : PhotoPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fullscreen)
        ctx = this
        path = intent.getStringExtra("imgPath")
        mode = intent.getStringExtra("mode") ?: "default"

        val adView = findViewById<AdView>(mirror.hand.makeup.shaving.best.zoom.pocket.selfie.R.id.adViewFullscreen)
        MobileAds.initialize(
            this
        ) { }
        val adRequest = AdRequest.Builder().build()
        if(viewModel.isADActive) adView.loadAd(adRequest)
        else adView.visibility = View.GONE


        if (mode=="preview") {
            findViewById<ImageButton>(R.id.imageButton3).visibility = View.VISIBLE
            adapter = PhotoPagerAdapter()
            adapter.setPhotos(listOf(path!!))
            val viewPager = findViewById<ViewPager2>(R.id.fullscreenImage)
            viewPager.adapter = adapter
        }else {
            imgs = getImagesFromFolder()
            val tmplist: MutableList<String> = imgs.keys.toMutableList()
            val index = tmplist.indexOf(path)
            adapter = PhotoPagerAdapter()
            adapter.setPhotos(tmplist)
            val viewPager = findViewById<ViewPager2>(R.id.fullscreenImage)
            viewPager.adapter = adapter
            viewPager.setCurrentItem(index, false)
            viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    adapter.currentImageIndex = position
                }
            })
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
            val file = File(adapter.getCurrentImage())
            MediaScannerConnection.scanFile(
                this,
                arrayOf(file.absolutePath),
                null
            ) { _, uri ->
                // сохранение uri в галерее выполнено успешно
                runOnUiThread {
                    val toast = Toast.makeText(ctx, "Изображение сохранено", Toast.LENGTH_SHORT)
                    toast.show()
                    finish()
                }
            }
        }
        saveImg = true
        if(path!=null) viewModel.lastImagePath = path!!
        val toast = Toast.makeText(ctx, "Изображение сохранено", Toast.LENGTH_SHORT)
        toast.show()
        finish()
    }

    fun onBackClick(v : View){
        finish()
    }

    fun onShareClick(v: View){
        shareImage(BitmapFactory.decodeFile(if (mode=="default")adapter.getCurrentImage() else path))
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

    fun getImagesFromFolder(): Map<String, Bitmap> {
        val folder = File(applicationContext.getExternalFilesDir(null), "mirrorImages")
        val images = mutableMapOf<String, Bitmap>()
        if (folder.exists()) {
            for (file in folder.listFiles()) {
                try {
                    images[file.absolutePath] = BitmapFactory.decodeFile(file.absolutePath)
                }catch (ex: Exception){}
            }
        }
        return images
    }
}
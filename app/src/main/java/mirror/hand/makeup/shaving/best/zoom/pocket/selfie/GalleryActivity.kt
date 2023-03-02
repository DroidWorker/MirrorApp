package mirror.hand.makeup.shaving.best.zoom.pocket.selfie

import android.content.ContentProvider
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.MediaMetadataRetriever
import android.media.ThumbnailUtils
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.CancellationSignal
import android.provider.MediaStore
import android.provider.MediaStore.Video.Thumbnails.MINI_KIND
import android.util.Log
import android.util.Size
import android.view.View
import android.widget.*
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import mirror.hand.makeup.shaving.best.zoom.pocket.selfie.VM.MainViewModel
import mirror.hand.makeup.shaving.best.zoom.pocket.selfie.adapters.ImageAdapter
import mirror.hand.makeup.shaving.best.zoom.pocket.selfie.dialog.MyDialogFragment
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.time.Instant


class GalleryActivity : AppCompatActivity() {
    private val userViewModel by viewModels<MainViewModel>()

    private var isActionMode = false
    private var selectedItems = mutableListOf<Int>()
    lateinit var imgs : Map<String, Bitmap>
    lateinit var vids : Map<String, Bitmap>

    lateinit var adapter : ImageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(mirror.hand.makeup.shaving.best.zoom.pocket.selfie.R.layout.activity_gallery)

        val gridView = findViewById<GridView>(mirror.hand.makeup.shaving.best.zoom.pocket.selfie.R.id.grid_view)
        imgs = getImagesFromFolder("img")
        vids = getVideoFromFolder()
        adapter = ImageAdapter(this, imgs, vids)
        gridView.adapter = adapter


        gridView.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            if (isActionMode) {
                selectItem(position, view)
            } else {
                userViewModel.currentImage.tryEmit(adapter.separatedImages[position])
                var vintent : Intent?
                adapter.getItemPath(position).let {
                    if (it?.contains("mirrorImages") == true) {
                        vintent = Intent(this@GalleryActivity, FullscreenActivity::class.java)
                        vintent!!.putExtra("imgPath", it)
                    }
                    else {
                        vintent = Intent(this@GalleryActivity, VideoActivity::class.java)
                        vintent!!.putExtra("imgPath", File(this.getExternalFilesDir( "" ).toString()+ "/videos/${it}.mp4").absolutePath)
                    }
                }
                startActivity(vintent)
            }
        }

        gridView.onItemLongClickListener = AdapterView.OnItemLongClickListener { parent, view, position, id ->
            if (!isActionMode) {
                startActionMode()
                selectItem(position, view)
                true
            } else {
                false
            }
        }
    }

    fun onCloseSaveClick(view: View){
        if (!isActionMode)
            finish()
        else{
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                onSaveGalleryClick(view)
            }
            else{
                Toast.makeText(this, "SORRY, device is not support function", Toast.LENGTH_LONG)
            }
        }
    }

    fun onDeleteClick(view: View){
        val dialogFragment = MyDialogFragment()
        dialogFragment.setOnPositiveButtonClickListener { ->
            selectedItems.forEach{
                adapter.getItemPath(it)?.let { it1 ->
                    if (it1.contains("mirrorImages"))   deleteFileByAbsolutePath(it1)
                    else deleteFileByAbsolutePath(File(this.getExternalFilesDir( "" ).toString()+ "/videos/${it1}.mp4").absolutePath)
                }
                adapter.separatedImages.removeAt(it)
            }
            selectedItems.clear()
            adapter.notifyDataSetChanged()
            finishActionMode()
        }
        dialogFragment.show(supportFragmentManager, "MyDialog")
    }

    fun onShareClick(v: View){
        var sendArr: ArrayList<Uri> = ArrayList()
        selectedItems.forEach{
            val file = adapter.getItemPath(it)?.let { it1 ->
                if (it1.contains("mirrorImages")) File(it1)
                else File(this.getExternalFilesDir( "" ).toString()+ "/videos/${it1}.mp4")
            }
            if (file!=null) sendArr.add(FileProvider.getUriForFile(this, "${this.packageName}.provider", file))
        }

        val intent = Intent().apply {
            action = Intent.ACTION_SEND_MULTIPLE
            type = "*/*"
            putParcelableArrayListExtra(Intent.EXTRA_STREAM, sendArr)
        }
        startActivity(Intent.createChooser(intent, "Share images"))
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun onSaveGalleryClick(v: View){
        selectedItems.forEach{
            MediaStore.Images.Media.insertImage(getContentResolver(), imgs[adapter.getItemPath(it)], "HM"+ Instant.now() , "created by HandMirror")
        }
        Toast.makeText(this, "Сохранено", Toast.LENGTH_SHORT).show()
    }

    private fun startActionMode() {
        isActionMode = true
        val delete = findViewById<ImageView>(mirror.hand.makeup.shaving.best.zoom.pocket.selfie.R.id.galleryDelete)
        val share = findViewById<ImageView>(mirror.hand.makeup.shaving.best.zoom.pocket.selfie.R.id.galleryShare)
        val save = findViewById<ImageButton>(mirror.hand.makeup.shaving.best.zoom.pocket.selfie.R.id.GalleryClose_save)
        delete.visibility = View.VISIBLE
        share.visibility = View.VISIBLE
        save.setImageDrawable(ContextCompat.getDrawable(this@GalleryActivity,
            mirror.hand.makeup.shaving.best.zoom.pocket.selfie.R.drawable.download
        ))
    }

    private fun selectItem(position: Int, view: View) {
        if (selectedItems.contains(position)) {
            selectedItems.remove(position)
            //view.background = ColorDrawable(Color.TRANSPARENT)
            view.foreground = ColorDrawable(Color.TRANSPARENT)
            val checker = view.findViewById<ImageView>(mirror.hand.makeup.shaving.best.zoom.pocket.selfie.R.id.checkerView)
            checker.visibility = View.GONE
            val title = findViewById<TextView>(mirror.hand.makeup.shaving.best.zoom.pocket.selfie.R.id.galleryTitle)
            title.text = getString(mirror.hand.makeup.shaving.best.zoom.pocket.selfie.R.string.selected, selectedItems.size)
        } else {
            selectedItems.add(position)
            //view.background = ColorDrawable(Color.LTGRAY)
            view.foreground = ContextCompat.getDrawable(this@GalleryActivity,
                mirror.hand.makeup.shaving.best.zoom.pocket.selfie.R.drawable.image_selection
            )
            val checker = view.findViewById<ImageView>(mirror.hand.makeup.shaving.best.zoom.pocket.selfie.R.id.checkerView)
            checker.visibility = View.VISIBLE
            val title = findViewById<TextView>(mirror.hand.makeup.shaving.best.zoom.pocket.selfie.R.id.galleryTitle)
            title.text = getString(mirror.hand.makeup.shaving.best.zoom.pocket.selfie.R.string.selected, selectedItems.size)
        }

        if (selectedItems.isEmpty()) {
            finishActionMode()
            val title = findViewById<TextView>(mirror.hand.makeup.shaving.best.zoom.pocket.selfie.R.id.galleryTitle)
            title.text = getString(mirror.hand.makeup.shaving.best.zoom.pocket.selfie.R.string.gallery_title)
        }
    }

    private fun finishActionMode() {
        isActionMode = false
        val delete = findViewById<ImageView>(mirror.hand.makeup.shaving.best.zoom.pocket.selfie.R.id.galleryDelete)
        val share = findViewById<ImageView>(mirror.hand.makeup.shaving.best.zoom.pocket.selfie.R.id.galleryShare)
        val save = findViewById<ImageButton>(mirror.hand.makeup.shaving.best.zoom.pocket.selfie.R.id.GalleryClose_save)
        delete.visibility = View.GONE
        share.visibility = View.GONE
        save.setImageDrawable(ContextCompat.getDrawable(this@GalleryActivity,
            mirror.hand.makeup.shaving.best.zoom.pocket.selfie.R.drawable.close
        ))
        findViewById<TextView>(R.id.galleryTitle).text = resources.getString(R.string.gallery_title)
        selectedItems.clear()
    }

    fun getImagesFromFolder(folderName: String): Map<String, Bitmap> {
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
    fun  getVideoFromFolder(): Map<String, Bitmap>{
        val folder = File(applicationContext.getExternalFilesDir(null), "videos")
        val vids = mutableMapOf<String, Bitmap>()
        val folderth = File(this.getExternalFilesDir( "" ).toString()+ "/videos/thumb")
        folderth.mkdirs()
        if (folder.exists()) {
            for (file in folder.listFiles()) {
                if (file.isFile) {
                    val filename = file.name.replace(".mp4", "")
                    println("koko")
                    //create preview
                    if (!File(
                            this.getExternalFilesDir("")
                                .toString() + "/videos/thumb/${filename}.png"
                        ).exists()){
                            val mediaMetadataRetriever = MediaMetadataRetriever()
                            mediaMetadataRetriever.setDataSource(file.absolutePath)
                            //save preview
                            val f = File(
                                this.getExternalFilesDir("")
                                    .toString() + "/videos/thumb/${filename}.png"
                            )
                            val bitmap = mediaMetadataRetriever.getFrameAtTime(0)
                            try {
                                val fileOutputStream = FileOutputStream(f)
                                bitmap?.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream)
                                fileOutputStream.flush()
                                fileOutputStream.close()
                            } catch (e: IOException) {
                                e.printStackTrace()
                            }

                            mediaMetadataRetriever.release()
                }
                    try {
                        val f = File(
                            this.getExternalFilesDir("")
                                .toString() + "/videos/thumb/${filename}.png"
                        )
                        vids[f.name.replace(".png", "")] =
                            BitmapFactory.decodeFile(f.absolutePath)
                    } catch (ex: Exception) {
                        Log.e("parceThumbError", ex.stackTraceToString())
                    }
                }
            }
        }
        return vids
    }

    fun deleteFileByAbsolutePath(filePath: String) {
        val file = File(filePath)
        if (file.exists()) {
            file.delete()
        }
    }
}

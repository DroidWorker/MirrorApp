package mirror.hand.makeup.shaving.best.zoom.pocket.selfie

import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.MediaMetadataRetriever
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import mirror.hand.makeup.shaving.best.zoom.pocket.selfie.VM.MainViewModel
import mirror.hand.makeup.shaving.best.zoom.pocket.selfie.adapters.GalleryItem
import mirror.hand.makeup.shaving.best.zoom.pocket.selfie.adapters.ImageAdapter
import mirror.hand.makeup.shaving.best.zoom.pocket.selfie.dialog.MyDialogFragment
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.time.Instant


class GalleryActivity : AppCompatActivity() {
    private val userViewModel by viewModels<MainViewModel>()

    private var isActionMode = false
    private var selectedItems = mutableListOf<Int>()
    lateinit var imgs : List<String>
    lateinit var vids : List<String>

    lateinit var adapter : ImageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gallery)

        val gridView = findViewById<RecyclerView>(R.id.grid_view)
        imgs = getImagesFromFolder("img")
        vids = getVideoFromFolder()
        val galleryItems = mutableListOf<GalleryItem>()
        imgs.forEach{
            galleryItems.add(GalleryItem(it, true))
        }
        vids.forEach{
            galleryItems.add(GalleryItem(it, false))
        }
        adapter = ImageAdapter(this, galleryItems)
        val layoutManager = GridLayoutManager(this, 3) // Устанавливаем 2 столбца
        gridView.layoutManager = layoutManager
        gridView.adapter = adapter


        adapter.onItemClick = { position, view ->
            if (isActionMode) {
                selectItem(position, view)
            } else {
                //userViewModel.currentImage.tryEmit(adapter.separatedImages[position])
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

        adapter.onItemLongClick = { position, view ->
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
            selectedItems.sortDescending()
            selectedItems.forEach{
                adapter.getItemPath(it)?.let { it1 ->
                    if (it1.contains("mirrorImages"))   deleteFileByAbsolutePath(it1)
                    else deleteFileByAbsolutePath(File(this.getExternalFilesDir( "" ).toString()+ "/videos/${it1}.mp4").absolutePath)
                }
            }
            selectedItems.forEach {
                adapter.removeItem(it)
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
            if(adapter.getItemType(it)) scanMedia(File(adapter.getItemPath(it)), true)
                else scanMedia(File(getExternalFilesDir( "" ).toString()+  "/videos/" + adapter.getItemPath(it)+".mp4"), false)
        }
        finishActionMode()
        adapter.resetSelection()
        adapter.notifyDataSetChanged()
        Toast.makeText(this, getString(R.string.saved), Toast.LENGTH_SHORT).show()
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
            adapter.setSelection(position, false)
            adapter.notifyDataSetChanged()
            //view.background = ColorDrawable(Color.TRANSPARENT)
            /*view.foreground = ColorDrawable(Color.TRANSPARENT)
            val checker = view.findViewById<ImageView>(R.id.checkerView)
            checker.visibility = View.GONE*/
            val title = findViewById<TextView>(R.id.galleryTitle)
            title.text = getString(mirror.hand.makeup.shaving.best.zoom.pocket.selfie.R.string.selected, selectedItems.size)
        } else {
            selectedItems.add(position)
            adapter.setSelection(position, true)
            adapter.notifyDataSetChanged()
            //view.background = ColorDrawable(Color.LTGRAY)
            /*view.foreground = ContextCompat.getDrawable(this@GalleryActivity,
                mirror.hand.makeup.shaving.best.zoom.pocket.selfie.R.drawable.image_selection
            )
            val checker = view.findViewById<ImageView>(mirror.hand.makeup.shaving.best.zoom.pocket.selfie.R.id.checkerView)
            checker.visibility = View.VISIBLE*/
            val title = findViewById<TextView>(mirror.hand.makeup.shaving.best.zoom.pocket.selfie.R.id.galleryTitle)
            title.text = getString(mirror.hand.makeup.shaving.best.zoom.pocket.selfie.R.string.selected, selectedItems.size)
        }

        if (selectedItems.isEmpty()) {
            finishActionMode()
            val title = findViewById<TextView>(mirror.hand.makeup.shaving.best.zoom.pocket.selfie.R.id.galleryTitle)
            title.text = getString(mirror.hand.makeup.shaving.best.zoom.pocket.selfie.R.string.gallery_title)
        }
    }

    fun scanMedia(mediaFile: File, isPhoto: Boolean) {
        println("eeeeeerrrrrrr save "+mediaFile.absolutePath)
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, mediaFile.name)
            put(MediaStore.MediaColumns.MIME_TYPE, if(isPhoto)"image/jpeg" else "video/mp4")
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DCIM)
        }

        val contentResolver = this.contentResolver
        val uri = if(isPhoto) contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
            else contentResolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, contentValues)
        uri?.let { outputStream ->
            contentResolver.openOutputStream(outputStream)?.use { output ->
                FileInputStream(mediaFile).use { input ->
                    input.copyTo(output)
                }
            }
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
            R.drawable.close
        ))
        findViewById<TextView>(R.id.galleryTitle).text = resources.getString(R.string.gallery_title)
        selectedItems.clear()
    }

    fun getImagesFromFolder(folderName: String): List<String> {
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
    fun  getVideoFromFolder(): List<String>{
        val folder = File(applicationContext.getExternalFilesDir(null), "videos")
        val vids = mutableListOf<String>()
        val folderth = File(this.getExternalFilesDir( "" ).toString()+ "/videos/thumb")
        folderth.mkdirs()
        if (folder.exists()) {
            for (file in folder.listFiles()) {
                if (file.isFile) {
                    if (file.exists() && file.length() == 0L) {
                        file.delete()
                    }else {
                        val filename = file.name.replace(".mp4", "")
                        //create preview
                        if (!File(
                                this.getExternalFilesDir("")
                                    .toString() + "/videos/thumb/${filename}.png"
                            ).exists()
                        ) {
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
                            vids.add(f.name.replace(".png", ""))
                        } catch (ex: Exception) {
                            Log.e("parceThumbError", ex.stackTraceToString())
                        }
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
        val imgs = getImagesFromFolder("img")
        if (imgs.isNotEmpty()) userViewModel.lastImagePath = imgs.last()
        else userViewModel.lastImagePath = "err"
    }
}

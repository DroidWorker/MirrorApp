package mirror.hand.makeup.shaving.best.zoom.pocket.selfie

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.*
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import mirror.hand.makeup.shaving.best.zoom.pocket.selfie.VM.MainViewModel
import mirror.hand.makeup.shaving.best.zoom.pocket.selfie.adapters.ImageAdapter
import mirror.hand.makeup.shaving.best.zoom.pocket.selfie.dialog.MyDialogFragment
import java.io.File
import java.time.Instant

class GalleryActivity : AppCompatActivity() {
    private val userViewModel by viewModels<MainViewModel>()

    private var isActionMode = false
    private var selectedItems = mutableListOf<Int>()
    lateinit var imgs : Map<String, Bitmap>

    lateinit var adapter : ImageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(mirror.hand.makeup.shaving.best.zoom.pocket.selfie.R.layout.activity_gallery)

        val gridView = findViewById<GridView>(mirror.hand.makeup.shaving.best.zoom.pocket.selfie.R.id.grid_view)
        imgs = getImagesFromFolder("img")
        adapter = ImageAdapter(this, imgs)
        gridView.adapter = adapter


        gridView.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            if (isActionMode) {
                selectItem(position, view)
            } else {
                userViewModel.currentImage.tryEmit(adapter.separatedImages[position])
                val intent = Intent(this@GalleryActivity, mirror.hand.makeup.shaving.best.zoom.pocket.selfie.FullscreenActivity::class.java)
                intent.putExtra("imgPath", adapter.getItemPath(position))
                startActivity(intent)
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
                adapter.getItemPath(it)?.let { it1 -> deleteFileByAbsolutePath(it1) }
                adapter.separatedImages.removeAt(it)
            }
            selectedItems.clear()
            adapter.notifyDataSetChanged()
        }
        dialogFragment.show(supportFragmentManager, "MyDialog")
    }

    fun onShareClick(v: View){
        var sendArr: ArrayList<Uri> = ArrayList()
        imgs.keys.forEach{
            val file = File(it)
            sendArr.add(FileProvider.getUriForFile(this, "${this.packageName}.provider", file))
        }

        val intent = Intent().apply {
            action = Intent.ACTION_SEND_MULTIPLE
            type = "image/*"
            putParcelableArrayListExtra(Intent.EXTRA_STREAM, sendArr)
        }
        startActivity(Intent.createChooser(intent, "Share images"))
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun onSaveGalleryClick(v: View){
        imgs.values.forEach{
            MediaStore.Images.Media.insertImage(getContentResolver(), it, "HM"+ Instant.now() , "created by HandMirror")
        }
        Toast.makeText(this, "Сохранено", Toast.LENGTH_SHORT).show()
    }

    private fun startActionMode() {
        isActionMode = true
        val delete = findViewById<ImageView>(mirror.hand.makeup.shaving.best.zoom.pocket.selfie.R.id.galleryDelete)
        val share = findViewById<ImageView>(mirror.hand.makeup.shaving.best.zoom.pocket.selfie.R.id.galleryShare)
        val save = findViewById<ImageView>(mirror.hand.makeup.shaving.best.zoom.pocket.selfie.R.id.GalleryClose_save)
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
        val save = findViewById<ImageView>(mirror.hand.makeup.shaving.best.zoom.pocket.selfie.R.id.GalleryClose_save)
        delete.visibility = View.GONE
        share.visibility = View.GONE
        save.setImageDrawable(ContextCompat.getDrawable(this@GalleryActivity,
            mirror.hand.makeup.shaving.best.zoom.pocket.selfie.R.drawable.close
        ))
        selectedItems.clear()
    }

    fun getImagesFromFolder(folderName: String): Map<String, Bitmap> {
        val folder = File(applicationContext.getExternalFilesDir(null), "mirrorImages")
        val images = mutableMapOf<String, Bitmap>()
        if (folder.exists()) {
            for (file in folder.listFiles()) {
                images[file.absolutePath] = BitmapFactory.decodeFile(file.absolutePath)
            }
        }
        return images
    }

    fun deleteFileByAbsolutePath(filePath: String) {
        val file = File(filePath)
        if (file.exists()) {
            file.delete()
        }
    }
}

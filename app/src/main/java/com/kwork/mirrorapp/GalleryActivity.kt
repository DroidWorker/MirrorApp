package com.kwork.mirrorapp

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.GridView
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.kwork.mirrorapp.VM.MainViewModel
import com.kwork.mirrorapp.adapters.ImageAdapter
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.io.File

class GalleryActivity : AppCompatActivity() {
    private val userViewModel by viewModels<MainViewModel>()

    private var isActionMode = false
    private var selectedItems = mutableListOf<Int>()

    lateinit var adapter : ImageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gallery)

        val gridView = findViewById<GridView>(R.id.grid_view)
        val imgs  : Map<String, Bitmap> = getImagesFromFolder("img")
        adapter = ImageAdapter(this, imgs)
        gridView.adapter = adapter


        gridView.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            if (isActionMode) {
                selectItem(position, view)
            } else {
                userViewModel.currentImage.tryEmit(adapter.separatedImages[position])
                val intent = Intent(this@GalleryActivity, FullscreenActivity::class.java)
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
    }

    fun onDeleteClick(view: View){
        selectedItems.forEach{
            adapter.getItemPath(it)?.let { it1 -> deleteFileByAbsolutePath(it1) }
            adapter.separatedImages.removeAt(it)
        }
        selectedItems.clear()
        adapter.notifyDataSetChanged()
    }

    private fun startActionMode() {
        isActionMode = true
        val delete = findViewById<ImageView>(R.id.galleryDelete)
        val share = findViewById<ImageView>(R.id.galleryShare)
        val save = findViewById<ImageView>(R.id.GalleryClose_save)
        delete.visibility = View.VISIBLE
        share.visibility = View.VISIBLE
        save.setImageDrawable(ContextCompat.getDrawable(this@GalleryActivity, R.drawable.download))
    }

    private fun selectItem(position: Int, view: View) {
        if (selectedItems.contains(position)) {
            selectedItems.remove(position)
            //view.background = ColorDrawable(Color.TRANSPARENT)
            view.foreground = ColorDrawable(Color.TRANSPARENT)
            val checker = view.findViewById<ImageView>(R.id.checkerView)
            checker.visibility = View.GONE
            val title = findViewById<TextView>(R.id.galleryTitle)
            title.text = getString(R.string.selected, selectedItems.size)
        } else {
            selectedItems.add(position)
            //view.background = ColorDrawable(Color.LTGRAY)
            view.foreground = ContextCompat.getDrawable(this@GalleryActivity, R.drawable.image_selection)
            val checker = view.findViewById<ImageView>(R.id.checkerView)
            checker.visibility = View.VISIBLE
            val title = findViewById<TextView>(R.id.galleryTitle)
            title.text = getString(R.string.selected, selectedItems.size)
        }

        if (selectedItems.isEmpty()) {
            finishActionMode()
            val title = findViewById<TextView>(R.id.galleryTitle)
            title.text = getString(R.string.gallery_title)
        }
    }

    private fun finishActionMode() {
        isActionMode = false
        val delete = findViewById<ImageView>(R.id.galleryDelete)
        val share = findViewById<ImageView>(R.id.galleryShare)
        val save = findViewById<ImageView>(R.id.GalleryClose_save)
        delete.visibility = View.GONE
        share.visibility = View.GONE
        save.setImageDrawable(ContextCompat.getDrawable(this@GalleryActivity, R.drawable.close))
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

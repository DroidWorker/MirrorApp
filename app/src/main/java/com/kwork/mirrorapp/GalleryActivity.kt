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
import androidx.activity.viewModels
import com.kwork.mirrorapp.VM.MainViewModel
import com.kwork.mirrorapp.adapters.ImageAdapter
import java.io.File

class GalleryActivity : AppCompatActivity() {
    private val userViewModel by viewModels<MainViewModel>()

    private var isActionMode = false
    private var selectedItems = mutableListOf<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gallery)

        val gridView = findViewById<GridView>(R.id.grid_view)
        val imgs  : List<Bitmap> = getImagesFromFolder("img")
        val adapter = ImageAdapter(this, imgs)
        gridView.adapter = adapter

        gridView.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            if (isActionMode) {
                selectItem(position, view)
            } else {
                val intent = Intent(this@GalleryActivity, FullscreenActivity::class.java)
                userViewModel.currentImage.tryEmit(adapter.images[position])
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

    private fun startActionMode() {
        isActionMode = true
        supportActionBar?.hide()
    }

    private fun selectItem(position: Int, view: View) {
        if (selectedItems.contains(position)) {
            selectedItems.remove(position)
            view.background = ColorDrawable(Color.TRANSPARENT)
        } else {
            selectedItems.add(position)
            view.background = ColorDrawable(Color.LTGRAY)
        }

        if (selectedItems.isEmpty()) {
            finishActionMode()
        }
    }

    private fun finishActionMode() {
        isActionMode = false
        supportActionBar?.show()
        selectedItems.clear()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if (isActionMode) {
            menuInflater.inflate(R.menu.menu_galery, menu)
            return true
        }
        return false
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.delete -> {
                deleteSelectedItems()
                finishActionMode()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun deleteSelectedItems() {
        // Add the logic to delete the selected images here
        // You can store the images in a database or a file system and delete them based on the position or id
    }

    fun getImagesFromFolder(folderName: String): List<Bitmap> {
        val folder = File(Environment.getExternalStorageDirectory(), folderName)
        val images = mutableListOf<Bitmap>()
        if (folder.exists()) {
            for (file in folder.listFiles()) {
                images.add(BitmapFactory.decodeFile(file.absolutePath))
            }
        }
        return images
    }
}

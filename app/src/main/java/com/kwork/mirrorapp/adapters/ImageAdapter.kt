package com.kwork.mirrorapp.adapters

import android.content.Context
import android.graphics.Bitmap
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView

class ImageAdapter(private val context: Context, val images: List<Bitmap>) : BaseAdapter() {

    override fun getCount(): Int = images.size

    override fun getItem(position: Int): Any = images[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val imageView = ImageView(context)
        imageView.setImageBitmap(images[position])
        imageView.scaleType = ImageView.ScaleType.CENTER_CROP
        //imageView.layoutParams = GridView.LayoutParams(240, 240)
        return imageView
    }
}
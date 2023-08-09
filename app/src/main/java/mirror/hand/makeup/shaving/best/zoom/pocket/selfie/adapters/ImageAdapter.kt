package mirror.hand.makeup.shaving.best.zoom.pocket.selfie.adapters

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import mirror.hand.makeup.shaving.best.zoom.pocket.selfie.R
import java.io.File


class ImageAdapter(private val context: Context, private val galleryItems: MutableList<GalleryItem>) : RecyclerView.Adapter<ImageAdapter.ViewHolder>() {

    var onItemClick: ((Int, View) -> Unit)? = null
    var onItemLongClick: ((Int, View) -> Unit)? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.grid_image_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val galleryItem = galleryItems[position]
        holder.bind(galleryItem)

        holder.itemView.setOnClickListener {
            onItemClick?.invoke(position, it)
        }
        holder.itemView.setOnLongClickListener {
            onItemLongClick?.invoke(position, it)
            true
        }
    }

    override fun getItemCount(): Int = galleryItems.size

    fun getItemPath(position: Int): String {
        return galleryItems[position].path
    }
    fun getItemType(position: Int): Boolean
    {
        return galleryItems[position].isImage
    }
    fun setSelection(position: Int, selected: Boolean){
        galleryItems[position].active = selected
    }
    fun resetSelection(){
        galleryItems.forEach{
            it.active = false
        }
    }
    fun removeItem(position: Int) {
        if (position >= 0 && position < galleryItems.size) {
            galleryItems.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.imagepart)
        private val textView360d: TextView = itemView.findViewById(R.id.textView360d)

        fun bind(galleryItem: GalleryItem) {
            if(galleryItem.active) {
                itemView.foreground = ContextCompat.getDrawable(
                    context,
                    R.drawable.image_selection
                )
                val checker =
                    itemView.findViewById<ImageView>(mirror.hand.makeup.shaving.best.zoom.pocket.selfie.R.id.checkerView)
                checker.visibility = View.VISIBLE
            }else{
                itemView.foreground = ColorDrawable(Color.TRANSPARENT)
                val checker = itemView.findViewById<ImageView>(R.id.checkerView)
                checker.visibility = View.GONE
            }
            if (galleryItem.isImage) {
                Glide.with(context)
                    .load(galleryItem.path)
                    .into(imageView)
                textView360d.visibility = View.GONE
            } else {
                val f = File(context.getExternalFilesDir("").toString() + "/videos/thumb/${galleryItem.path}.png")
                Glide.with(context)
                    .load(f.absolutePath)
                    .into(imageView)
                textView360d.visibility = View.VISIBLE
            }
        }
    }
}

data class GalleryItem(val path: String, val isImage: Boolean, var active: Boolean = false)
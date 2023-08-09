package mirror.hand.makeup.shaving.best.zoom.pocket.selfie.adapters

import android.content.Context
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import mirror.hand.makeup.shaving.best.zoom.pocket.selfie.R
import com.bumptech.glide.Glide

class PhotoPagerAdapter : RecyclerView.Adapter<PhotoPagerAdapter.ViewHolder>() {

    private var photos = listOf<String>()
    var currentImageIndex = 0

    fun setPhotos(newPhotos: List<String>) {
        photos = newPhotos
        notifyDataSetChanged()
    }

    fun getCurrentImage(): String {
        return photos[currentImageIndex]
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.photo_image_view, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val photo = photos[position]
        holder.bind(photo)
    }

    override fun getItemCount(): Int {
        return photos.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val photoImageView: ImageView = itemView.findViewById(R.id.photo_image_view)
        fun bind(photo: String) {
            Glide.with(itemView.context)
                .load(photo)
                .into(photoImageView)
        }
    }
}
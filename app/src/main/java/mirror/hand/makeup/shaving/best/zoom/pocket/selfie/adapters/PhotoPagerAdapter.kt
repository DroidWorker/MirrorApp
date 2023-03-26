package mirror.hand.makeup.shaving.best.zoom.pocket.selfie.adapters

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import mirror.hand.makeup.shaving.best.zoom.pocket.selfie.R

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
        holder.bind(photos[position])
    }

    override fun getItemCount(): Int {
        return photos.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val photoImageView: ImageView = itemView.findViewById(R.id.photo_image_view)

        fun bind(photo: String) {
            photoImageView.setImageBitmap(BitmapFactory.decodeFile(photo))
        }
    }
}
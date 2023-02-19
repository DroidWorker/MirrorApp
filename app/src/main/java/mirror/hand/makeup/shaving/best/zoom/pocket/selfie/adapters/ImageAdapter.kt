package mirror.hand.makeup.shaving.best.zoom.pocket.selfie.adapters

import android.content.Context
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import mirror.hand.makeup.shaving.best.zoom.pocket.selfie.R


class ImageAdapter(private val context: Context, val images: Map<String, Bitmap>) : BaseAdapter() {
    var separatedImages : MutableList<Bitmap> = images.values.toMutableList()

    override fun getCount(): Int = separatedImages.size

    override fun getItem(position: Int): Any = separatedImages[position]

    fun getItemPath(position: Int) : String?{
        images.forEach{
            if (it.value == separatedImages[position])
                return it.key
        }
        return null
    }

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view : View //= ImageView(context)
        //imageView.setImageBitmap(separatedImages[position])
        //imageView.scaleType = ImageView.ScaleType.FIT_CENTER
        //imageView.layoutParams = GridView.LayoutParams(240, 240)5
        view = LayoutInflater.from(context).inflate(
            R.layout.grid_image_item,
            null,
            false
        )
        val iv = view.findViewById<ImageView>(R.id.imagepart)
        iv.setImageBitmap(separatedImages[position])
        iv.scaleType = ImageView.ScaleType.FIT_XY
        return view
    }
}
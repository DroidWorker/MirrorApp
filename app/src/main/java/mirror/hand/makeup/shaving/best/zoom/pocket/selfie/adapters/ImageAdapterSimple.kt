import android.content.Context
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import mirror.hand.makeup.shaving.best.zoom.pocket.selfie.R


class ImageAdapterSimple(private val context: Context, val images: Map<String, Bitmap>, val videos: Map<String, Bitmap>?) : BaseAdapter() {
    var separatedImages : MutableList<Bitmap> = images.values.toMutableList()

    init {
        videos?.values?.let { separatedImages.addAll(it.toMutableList()) }
    }

    override fun getCount(): Int = separatedImages.size

    override fun getItem(position: Int): Any = separatedImages[position]

    fun getItemPath(position: Int) : String?{
        videos?.forEach{
            if (it.value == separatedImages[position])
                return it.key
        }
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
        if (getItemPath(position)?.contains("mirrorImages")==false){
            view.findViewById<TextView>(R.id.textView360d).visibility= View.VISIBLE
        }
        val iv = view.findViewById<ImageView>(R.id.imagepart)
        iv.setImageBitmap(separatedImages[position])
        iv.scaleType = ImageView.ScaleType.FIT_XY
        return view
    }
}
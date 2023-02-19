import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import mirror.hand.makeup.shaving.best.zoom.pocket.selfie.R

class ViewPagerAdapter : RecyclerView.Adapter<PagerVH>() {

    private val images = listOf<Int>(
        R.drawable.image1,
        R.drawable.image2,
        R.drawable.image3,
        R.drawable.image1
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PagerVH =
        PagerVH(LayoutInflater.from(parent.context).inflate(R.layout.mask_image_item, parent, false))

    override fun getItemCount(): Int = images.size

    override fun onBindViewHolder(holder: PagerVH, position: Int) = holder.itemView.run {
        findViewById<ImageView>(R.id.ivMasc).setImageResource(images[position])
    }
}

class PagerVH(itemView: View) : RecyclerView.ViewHolder(itemView)
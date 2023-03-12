package mirror.hand.makeup.shaving.best.zoom.pocket.selfie.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import mirror.hand.makeup.shaving.best.zoom.pocket.selfie.R

class MascsAdapter(
    private  val ctx: Context,
    private val masks: ArrayList<Int>
) : RecyclerView.Adapter<MascsAdapter.DataViewHolder>() {

    class DataViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(c: Context, m: Int) {
            itemView.findViewById<ImageView>(R.id.maskPreviewItemPhoto).apply {
                setImageDrawable(c.getDrawable(m))
                visibility = View.VISIBLE
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        DataViewHolder(LayoutInflater.from(parent.context)
            .inflate(R.layout.mask_preview_item, parent,false))

    override fun getItemCount(): Int = Int.MAX_VALUE

    override fun onBindViewHolder(holder: DataViewHolder, position: Int) =
        run {
            val pos = position % masks.size
            holder.bind(ctx, masks[pos])
        }
}
package mirror.hand.makeup.shaving.best.zoom.pocket.selfie.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import mirror.hand.makeup.shaving.best.zoom.pocket.selfie.R

class MyDialogFragment : DialogFragment() {

    private lateinit var onPositiveButtonClickListener: (() -> Unit)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.dialog_layout, container, false)
        val positiveButton = view.findViewById<TextView>(R.id.negativeButton)
        val negativeButton = view.findViewById<TextView>(R.id.positiveButton)
        positiveButton.setOnClickListener {
            onPositiveButtonClickListener.invoke()
            dismiss()
        }
        negativeButton.setOnClickListener {
            dismiss()
        }
        return view
    }

    fun setOnPositiveButtonClickListener(listener: (() -> Unit)) {
        onPositiveButtonClickListener = listener
    }
}
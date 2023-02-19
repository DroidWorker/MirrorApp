package mirror.hand.makeup.shaving.best.zoom.pocket.selfie.tools

import android.content.Context
import com.google.android.material.bottomsheet.BottomSheetDialog
import mirror.hand.makeup.shaving.best.zoom.pocket.selfie.R

class SmoothBottomSheetDialog(context: Context) : BottomSheetDialog(context) {

    override fun show() {
        window?.attributes?.windowAnimations  = R.style.SmoothBottomSheetDialogAnimation
        super.show()
    }
}
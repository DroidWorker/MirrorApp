package mirror.hand.makeup.shaving.best.zoom.pocket.selfie.tools

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.google.android.material.bottomsheet.BottomSheetDialog
import mirror.hand.makeup.shaving.best.zoom.pocket.selfie.R

class SmoothBottomSheetDialog(context: Context) : BottomSheetDialog(context, R.style.BottomSheetDialogKeyboardTheme) {

    override fun show() {
        window?.attributes?.windowAnimations  = R.style.SmoothBottomSheetDialogAnimation
        super.show()
    }
}
package com.kwork.mirrorapp.tools

import android.content.Context
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.kwork.mirrorapp.R

class SmoothBottomSheetDialog(context: Context) : BottomSheetDialog(context) {

    override fun show() {
        window?.attributes?.windowAnimations  = R.style.SmoothBottomSheetDialogAnimation
        super.show()
    }
}
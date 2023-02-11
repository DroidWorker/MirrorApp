package com.kwork.mirrorapp

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.kwork.mirrorapp.tools.SmoothBottomSheetDialog


class SettingsActivity : AppCompatActivity() {
    var rate = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
    }

    fun onCloseClick(v: View){
        finish()
    }

    fun onSupportClick(v: View){
        val bottomSheetDialog = SmoothBottomSheetDialog(this)
        bottomSheetDialog.setContentView(R.layout.bottom_sheet_dialog_support)

        val icecream = bottomSheetDialog.findViewById<TextView>(R.id.bsdIcecream)
        val coffee = bottomSheetDialog.findViewById<TextView>(R.id.bsdCupofCoffee)
        val burger = bottomSheetDialog.findViewById<TextView>(R.id.bsdBurger)
        val pizza = bottomSheetDialog.findViewById<TextView>(R.id.bsdPizza)
        val dinner = bottomSheetDialog.findViewById<TextView>(R.id.bsdDinner)

        bottomSheetDialog.show()
    }

    fun onReviewClick(v: View){
        val bottomSheetDialog = SmoothBottomSheetDialog(this)
        bottomSheetDialog.setContentView(R.layout.bottom_sheet_dialog_review)

        val text = bottomSheetDialog.findViewById<EditText>(R.id.BSDReditText)
        val send = bottomSheetDialog.findViewById<Button>(R.id.BSDRsend)

        bottomSheetDialog.show()
    }

    fun onRateClick(v: View){
        val bottomSheetDialog = SmoothBottomSheetDialog(this)
        bottomSheetDialog.setContentView(R.layout.bottom_sheet_dialog_rate)

        val step1 = bottomSheetDialog.findViewById<ConstraintLayout>(R.id.bsdrateStep1)
        val stepOK = bottomSheetDialog.findViewById<ConstraintLayout>(R.id.bsdrateStepOK)
        val stepBAD = bottomSheetDialog.findViewById<ConstraintLayout>(R.id.bsdrateStepBAD)

        //if like
        val buttonOK = bottomSheetDialog.findViewById<Button>(R.id.buttonOK)
        //if hate
        val buttonBAD = bottomSheetDialog.findViewById<Button>(R.id.buttonBAD)

        buttonOK?.setOnClickListener {
            rate=-1
            step1?.visibility = View.GONE
            stepOK?.visibility = View.VISIBLE

            val star1 = bottomSheetDialog.findViewById<ImageView>(R.id.star1)
            val star2 = bottomSheetDialog.findViewById<ImageView>(R.id.star2)
            val star3 = bottomSheetDialog.findViewById<ImageView>(R.id.star3)
            val star4 = bottomSheetDialog.findViewById<ImageView>(R.id.star4)
            val star5 = bottomSheetDialog.findViewById<ImageView>(R.id.star5)
            val buttonRate = bottomSheetDialog.findViewById<Button>(R.id.bsdrateRate)

            star1?.setOnClickListener{
                selectStar(star1, star2, star3, star4, star5, buttonRate, 1)
            }
            star2?.setOnClickListener{
                selectStar(star1, star2, star3, star4, star5, buttonRate, 2)
            }
            star3?.setOnClickListener{
                selectStar(star1, star2, star3, star4, star5, buttonRate, 3)
            }
            star4?.setOnClickListener{
                selectStar(star1, star2, star3, star4, star5, buttonRate, 4)
            }
            star5?.setOnClickListener{
                selectStar(star1, star2, star3, star4, star5, buttonRate, 5)
            }

            buttonRate?.setOnClickListener{
                if (rate in 1..3){
                    TODO("do nothing?")
                    return@setOnClickListener
                }
                else if(rate>3){
                    TODO("go on playmarket")
                    return@setOnClickListener
                }
            }
        }

        buttonBAD?.setOnClickListener{
            step1?.visibility = View.GONE
            stepBAD?.visibility = View.VISIBLE

            val etText = bottomSheetDialog.findViewById<EditText>(R.id.BSDRateBadEditText)
            val buttonSendText = bottomSheetDialog.findViewById<Button>(R.id.BSDRateBadSend)

            buttonSendText?.setOnClickListener{
                if (etText?.text?.length!! >3){
                    TODO("send text")
                }
            }
        }

        bottomSheetDialog.show()
    }


    private fun selectStar(iv1: ImageView?, iv2: ImageView?,iv3: ImageView?,iv4: ImageView?,iv5: ImageView?, b: Button?, r: Int){
        if (r<=0) return
        else{
            iv1?.setImageDrawable(
                ContextCompat.getDrawable(
                    this@SettingsActivity,
                    R.drawable.star
                ))
            iv2?.setImageDrawable(
                ContextCompat.getDrawable(
                    this@SettingsActivity,
                    R.drawable.star
                ))
            iv3?.setImageDrawable(
                ContextCompat.getDrawable(
                    this@SettingsActivity,
                    R.drawable.star
                ))
            iv4?.setImageDrawable(
                ContextCompat.getDrawable(
                    this@SettingsActivity,
                    R.drawable.star
                ))
            iv5?.setImageDrawable(
                ContextCompat.getDrawable(
                    this@SettingsActivity,
                    R.drawable.star
                ))
        }
        rate=r
        b?.background = ContextCompat.getDrawable(this@SettingsActivity, R.drawable.rounded_white)
        if(r>3) b?.text = getString(R.string.rate_on_pm)
        else b?.text = getString(R.string.rate)
        if (r>0) {
            iv1?.setImageDrawable(
                ContextCompat.getDrawable(
                    this@SettingsActivity,
                    R.drawable.starfilles
                )
            )
        } else return
        if (r>1) {
            iv2?.setImageDrawable(
                ContextCompat.getDrawable(
                    this@SettingsActivity,
                    R.drawable.starfilles
                )
            )
        } else return
        if (r>2) {
            iv3?.setImageDrawable(
                ContextCompat.getDrawable(
                    this@SettingsActivity,
                    R.drawable.starfilles
                )
            )
        } else return
        if (r>3) {
            iv4?.setImageDrawable(
                ContextCompat.getDrawable(
                    this@SettingsActivity,
                    R.drawable.starfilles
                )
            )
        } else return
        if (r>4) {
            iv5?.setImageDrawable(
                ContextCompat.getDrawable(
                    this@SettingsActivity,
                    R.drawable.starfilles
                )
            )
        } else return
    }
}
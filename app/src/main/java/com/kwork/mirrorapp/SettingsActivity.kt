package com.kwork.mirrorapp

import android.content.Context
import android.content.Intent
import android.net.Uri
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

        var answer = "empty"

        val clMain = bottomSheetDialog.findViewById<ConstraintLayout>(R.id.constraintLayoutMain)
        val cllist1 = bottomSheetDialog.findViewById<ConstraintLayout>(R.id.qlist1)
        val cllist2 = bottomSheetDialog.findViewById<ConstraintLayout>(R.id.qlist2)
        val rbg = bottomSheetDialog.findViewById<RadioGroup>(R.id.radioGroup)
        val rbg2 = bottomSheetDialog.findViewById<RadioGroup>(R.id.radioGroup2)
        val rb1 = bottomSheetDialog.findViewById<RadioButton>(R.id.radioButton)
        val rb2 = bottomSheetDialog.findViewById<RadioButton>(R.id.radioButton3)
        val rb3 = bottomSheetDialog.findViewById<RadioButton>(R.id.radioButton4)
        val rb4 = bottomSheetDialog.findViewById<RadioButton>(R.id.radioButton5)
        val rb5 = bottomSheetDialog.findViewById<RadioButton>(R.id.radioButton6)
        val rb6 = bottomSheetDialog.findViewById<RadioButton>(R.id.radioButton7)
        val rb7 = bottomSheetDialog.findViewById<RadioButton>(R.id.radioButton9)
        val buttonNext = bottomSheetDialog.findViewById<Button>(R.id.buttonNexxt)
        val buttonNex2 = bottomSheetDialog.findViewById<Button>(R.id.buttonNexxt2)
        val text = bottomSheetDialog.findViewById<EditText>(R.id.BSDReditText)
        val send = bottomSheetDialog.findViewById<Button>(R.id.BSDRsend)

        rbg?.setOnCheckedChangeListener{group, checkedID->
            when(checkedID){
                R.id.radioButton->{
                    answer = "У меня возникла проблема"
                }
                R.id.radioButton3->{
                    answer = "Нужные мне функции отсутствуют"
                }
                R.id.radioButton4->{
                    answer = "У меня есть идея/предложение"
                }
                R.id.radioButton5->{
                    answer = "Приложение неудобно"
                }
                R.id.radioButton6->{
                    answer = "Много рекламы"
                }
                R.id.radioButton7->{
                    answer = "Мне нравится приложение"
                }
                R.id.radioButton9->{
                    answer = "Другое"
                }
            }
            buttonNext?.isClickable = true
        }

        buttonNext?.setOnClickListener{
            when(answer){
                "У меня возникла проблема"->{
                    cllist1?.visibility = View.GONE
                    cllist2?.visibility = View.VISIBLE
                }
                "Нужные мне функции отсутствуют"->{
                    cllist1?.visibility = View.GONE
                    clMain?.visibility = View.VISIBLE
                }
                "У меня есть идея/предложение"->{
                    cllist1?.visibility = View.GONE
                    clMain?.visibility = View.VISIBLE
                }
                "Приложение неудобно"->{
                    cllist1?.visibility = View.GONE
                    clMain?.visibility = View.VISIBLE
                }
                "Много рекламы"->{
                    val intent = Intent(this@SettingsActivity, PayActivity::class.java)
                    finish()
                    startActivity(intent)
                }
                "Мне нравится приложение"->{
                    onRateClick(v)
                    bottomSheetDialog.hide()
                }
                "Другое"->{
                    cllist1?.visibility = View.GONE
                    clMain?.visibility = View.VISIBLE
                }
            }

        }
        rbg2?.setOnCheckedChangeListener{group, checkedID->
            when(checkedID){
                R.id.radioButton10->{
                    answer = "Камера не работа"
                }
                R.id.radioButton11->{
                    answer = "Плохое качество изображения"
                }
                R.id.radioButton12->{
                    answer = "Проблемы с 3D режимом"
                }
                R.id.radioButton13->{
                    answer = "Другое"
                }
            }
        }
        buttonNex2?.setOnClickListener{
            cllist2?.visibility = View.GONE
            clMain?.visibility = View.VISIBLE
        }

        send?.setOnClickListener{
            if (text?.text!!.length>2){
                val intent = Intent(Intent.ACTION_SENDTO).apply {
                    data = Uri.parse("mailto:")
                    putExtra(Intent.EXTRA_EMAIL, arrayOf("test@test.test"))
                    putExtra(Intent.EXTRA_SUBJECT, "Обращение пользователя AppMirror")
                    putExtra(Intent.EXTRA_TEXT, "$answer, ")
                }
                if (intent.resolveActivity(this.packageManager) != null) {
                    this.startActivity(intent)
                } else {
                    Toast.makeText(this, "No email app found", Toast.LENGTH_SHORT).show()
                }
            }
            bottomSheetDialog.hide()
        }

        bottomSheetDialog.show()
    }

    fun shareText(v: View) {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_TEXT, "https/googleplaystorelink")
        this.startActivity(Intent.createChooser(intent, "Share via"))
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
                    bottomSheetDialog.hide()
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
                    val intent = Intent(Intent.ACTION_SENDTO).apply {
                        data = Uri.parse("mailto:")
                        putExtra(Intent.EXTRA_EMAIL, arrayOf("test@test.test"))
                        putExtra(Intent.EXTRA_SUBJECT, "mirrorAPP")
                        putExtra(Intent.EXTRA_TEXT, etText!!.text)
                    }
                    if (intent.resolveActivity(this.packageManager) != null) {
                        this.startActivity(intent)
                    } else {
                        Toast.makeText(this, "No email app found", Toast.LENGTH_SHORT).show()
                    }
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
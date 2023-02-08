package com.kwork.mirrorapp.VM

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.preference.PreferenceManager
import com.kwork.mirrorapp.R

class MainViewModel(val app: Application) : AndroidViewModel(app) {
    var isReadytoLoad : MutableStateFlow<Boolean> = MutableStateFlow(false)
    var currentImage : MutableStateFlow<Bitmap?> = MutableStateFlow(null)

    val sharedPreference = app.getSharedPreferences("appSettings", Context.MODE_PRIVATE)
    var editor = sharedPreference.edit()

    var splashText : String
        get() = sharedPreference.getString(app.resources.getString(R.string.splashType), app.resources.getString(R.string.splash_text)) ?: app.resources.getString(R.string.splash_text)
        set(value){
            editor.putString(app.resources.getString(R.string.splashType), value)
            editor.apply()
        }
    var isFirstLaunch : Boolean
        get() = sharedPreference.getBoolean("isFirstLaunch", true)
        set(value){
            editor.putBoolean("isFirstLaunch", value)
            editor.apply()
        }
}
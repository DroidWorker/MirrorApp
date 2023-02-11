package com.kwork.mirrorapp.VM

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.preference.PreferenceManager
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.kwork.mirrorapp.R
import kotlinx.coroutines.tasks.await

class MainViewModel(val app: Application) : AndroidViewModel(app) {
    val firebase = Firebase.database("https://mirror-ba8f2-default-rtdb.europe-west1.firebasedatabase.app/")

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

    fun addVote(type : String, value : Int){
        try {
            val ref = firebase.getReference("votes/$type")
            ref.setValue(value)
            println("set value "+type+" "+value)
        }catch (e : Exception){
            println("errr "+e.localizedMessage)
        }
    }

    fun loadVotes(){
        val ref = firebase.getReference("votes")
        ref.get().addOnSuccessListener {
            var compressData = ""
            it.children.forEach{
                if (it.key!=null) {
                    compressData+=it.key+"-"+it.value+"&"
                }
            }
            editor.putString("votes", compressData.dropLast(1))
            editor.apply()
        }.addOnFailureListener{
            println("Errrrol loading")
        }
    }

    fun getVotes() : Map<String, Int>{
        var votes : MutableMap<String, Int> = emptyMap<String, Int>().toMutableMap()
        val compressedData = sharedPreference.getString("votes", null)
        if (compressedData!=null){
            val pairs = compressedData.split("&")
            pairs.forEach{
                votes[it.split("-")[0]] = (it.split("-")[1].toString()).toInt()
            }
        }
        return votes
    }
}
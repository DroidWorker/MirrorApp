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
    var is360FirstLaunch : Boolean
        get() = sharedPreference.getBoolean("is360FirstLaunch", true)
        set(value){
            editor.putBoolean("is360FirstLaunch", value)
            editor.apply()
        }
    var isADActive : Boolean
        get() = sharedPreference.getBoolean("isADActive", true)
        set(value){
            editor.putBoolean("isADActive", value)
            editor.apply()
        }

    var subscriptionType : String
        get() = sharedPreference.getString("subscriptionType", "off")?: "off"
        set(value){
            editor.putString("subscriptionType", value)
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

    fun loadTexts(){
        val ref = firebase.getReference("texts")
        ref.get().addOnSuccessListener {
            var privacy = ""
            var terms = ""
            it.children.forEach{
                if (it.key!=null&&it.key=="privacy") {
                    privacy = it.value.toString()
                }else if (it.key!=null&&it.key=="terms") {
                    terms = it.value.toString()
                }
            }
            editor.putString("privacy", privacy)
            editor.putString("terms", terms)
            editor.apply()
        }.addOnFailureListener{
            println("Errrrol loading")
        }
    }

    fun getTexts(type: String) : String{
        val text = sharedPreference.getString(type, null)//privacy or terms
        return text ?: "<h1>......../404 NOT FOUND/.......</h1>"
    }
}
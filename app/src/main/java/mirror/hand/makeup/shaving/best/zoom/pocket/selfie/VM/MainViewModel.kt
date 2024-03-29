package mirror.hand.makeup.shaving.best.zoom.pocket.selfie.VM

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import android.graphics.Bitmap
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import mirror.hand.makeup.shaving.best.zoom.pocket.selfie.R

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

    var isNotificationActive : Boolean
        get() = sharedPreference.getBoolean("isNotificationActive", true)
        set(value){
            editor.putBoolean("isNotificationActive", value)
            editor.apply()
        }
    var isPaywallOpened : Boolean
        get() = sharedPreference.getBoolean("isPaywallOpened", true)
        set(value){
            editor.putBoolean("isPaywallOpened", value)
            editor.apply()
        }

    var isFeedbackActive : Boolean
        get() = sharedPreference.getBoolean("isFeedbackActive", true)
        set(value){
            editor.putBoolean("isFeedbackActive", value)
            editor.apply()
        }

    var subscriptionType : String
        get() = sharedPreference.getString("subscriptionType", "off")?: "off"
        set(value){
            editor.putString("subscriptionType", value)
            editor.apply()
        }

    var shareAppString : String
        get() = sharedPreference.getString("shareAppString", "Error")?: "Error"
        set(value){
            editor.putString("shareAppString", value)
            editor.apply()
        }

    var DEV_MODE : Boolean
        get() = sharedPreference.getBoolean("devmode", false)
        set(value){
            editor.putBoolean("devmode", value)
            editor.apply()
        }

    var myAppsString : String
        get() = sharedPreference.getString("myAppsString", "Error")?: "Error"
        set(value){
            editor.putString("myAppsString", value)
            editor.apply()
        }

    var adBannerTimer : Int
    get() = sharedPreference.getInt("adBannerTimer", 9)
    set(value){
        editor.putInt("adBannerTimer", value)
        editor.apply()
    }

    var rateRequestTimer : Int
        get() = sharedPreference.getInt("rateRequestTimer", 6)
        set(value){
            editor.putInt("rateRequestTimer", value)
            editor.apply()
        }

    var paywallTimer : Int
        get() = sharedPreference.getInt("paywallTimer", 8)
        set(value){
            editor.putInt("paywallTimer", value)
            editor.apply()
        }

    var interstitialTimer : Int
        get() = sharedPreference.getInt("InterstitialTimer", 180)
        set(value){
            editor.putInt("InterstitialTimer", value)
            editor.apply()
        }

    var lastInterstitialShowed : Long = 0
        get() = sharedPreference.getLong("lastInterstitialShowed", 0)
    fun shotCurrentADTime(){
        editor.putLong("lastInterstitialShowed", System.currentTimeMillis())
        editor.apply()
    }


    var splashDelay : Int
        get() = sharedPreference.getInt("splashDelay", 3000)
        set(value){
            editor.putInt("splashDelay", value)
            editor.apply()
        }

    var lastImagePath : String
        get() = sharedPreference.getString("lastImagePath", "err") ?: "err"
        set(value){
            editor.putString("lastImagePath", value)
            editor.apply()
        }

    fun addLog(key : String, value : String){
        try {
            val ref = firebase.getReference("appLog")
            ref.child(key).setValue(value)
        }catch (e : Exception){
            println("errr "+e.localizedMessage)
        }
    }

    fun addVote(type : String, value : Int){
        try {
            val ref = firebase.getReference("votes/$type")
            ref.setValue(value)
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

    fun getShareAppLink(){
        val ref = firebase.getReference("shareAppLink")
        ref.get().addOnSuccessListener {
            shareAppString = it.value.toString()
        }.addOnFailureListener{
            println("Errrrol loading")
        }
    }

    fun getMyAppsLink(){
        val ref = firebase.getReference("myAppsLink")
        ref.get().addOnSuccessListener {
            myAppsString = it.value.toString()
        }.addOnFailureListener{
            println("Errrrol loading")
        }
    }
}
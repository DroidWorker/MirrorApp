package mirror.hand.makeup.shaving.best.zoom.pocket.selfie

import android.app.Application
import android.content.Intent
import android.os.Build
import com.google.firebase.FirebaseApp
import mirror.hand.makeup.shaving.best.zoom.pocket.selfie.VM.MainViewModel
import java.util.Date

class MyApplication : Application() {
    private lateinit var viewModel : MainViewModel

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        viewModel = MainViewModel(this)
        registerGlobalExceptionHandler()
    }

    private fun registerGlobalExceptionHandler() {
        val defaultExceptionHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            // Отправка сообщения об ошибке в Firebase
            viewModel.addLog("Crash_"+ Build.MODEL+"_version "+Build.VERSION.SDK_INT+"_date"+ Date(), throwable.stackTraceToString())

            // Открытие ErrorActivity
            val intent = Intent(applicationContext, ErrorActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)

            // Вызов стандартного обработчика исключений
            //defaultExceptionHandler?.uncaughtException(thread, throwable)
        }
    }
}

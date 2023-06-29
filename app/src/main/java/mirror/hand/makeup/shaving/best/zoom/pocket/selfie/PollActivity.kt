package mirror.hand.makeup.shaving.best.zoom.pocket.selfie

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.RemoteViews
import android.widget.TextView
import androidx.activity.viewModels
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.NotificationCompat
import mirror.hand.makeup.shaving.best.zoom.pocket.selfie.VM.MainViewModel
import mirror.hand.makeup.shaving.best.zoom.pocket.selfie.tools.NotificationReceiver

class PollActivity : AppCompatActivity() {
    private val userViewModel by viewModels<MainViewModel>()
    private var votes : Map<String, Int>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_poll)

        votes = userViewModel.getVotes()
        val percents: MutableMap<String, Int> = emptyMap<String, Int>().toMutableMap()
        var total = 0
        if(votes==null) finish()
        votes!!.forEach{
            total+=it.value
            when(it.key){
                "Shave"->{
                    percents["1"] = it.value
                }
                "Putting_on_makeup"->{
                    percents["2"] = it.value
                }
                "Putting_on_lenses"->{
                    percents["3"] = it.value
                }
                "Use_in_dark"->{
                    percents["4"] = it.value
                }
                "Brush_your_teeth"->{
                    percents["5"] = it.value
                }
                "Just_to_look"->{
                    percents["6"] = it.value
                }
            }
        }
        findViewById<ProgressBar>(R.id.pBar1).progress = (((percents["1"]!!)*100)/total)
        findViewById<ProgressBar>(R.id.pBar2).progress = (((percents["2"]!!)*100)/total)
        findViewById<ProgressBar>(R.id.pBar3).progress = (((percents["3"]!!)*100)/total)
        findViewById<ProgressBar>(R.id.pBar4).progress = (((percents["4"]!!)*100)/total)
        findViewById<ProgressBar>(R.id.pBar5).progress = (((percents["5"]!!)*100)/total)
        findViewById<ProgressBar>(R.id.pBar6).progress = (((percents["6"]!!)*100)/total)
    }

    override fun onDestroy() {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

// уникальный идентификатор уведомления
        val notificationId = 257894

// проверяем, было ли уже показано уведомление
        val activeNotifications = notificationManager.activeNotifications
        val notificationAlreadyShown = activeNotifications.any { it.id == notificationId }

        if (!notificationAlreadyShown) {
            // создаем уведомление и выводим его

// Создаем канал уведомлений для Android O и выше
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(
                    "appMirrorChannel",
                    "AppMirror",
                    NotificationManager.IMPORTANCE_HIGH
                )
                notificationManager.createNotificationChannel(channel)
            }

// Создаем интент, который будет запускаться при нажатии на уведомление
            val intent = Intent(this, MainActivity::class.java)
            val pendingIntent =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_MUTABLE)
                else PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
            val intentClose = Intent(this, NotificationReceiver::class.java).apply {
                action = "mirror.hand.makeup.shaving.best.zoom.pocket.selfie.notification.ACTION_CLOSE"
                putExtra("notification_id", notificationId)
            }

            val pendingIntentClose =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) PendingIntent.getBroadcast(this, 0, intentClose, PendingIntent.FLAG_MUTABLE)
                else PendingIntent.getActivity(this, 0, intentClose, PendingIntent.FLAG_UPDATE_CURRENT)

            val remoteViews = RemoteViews(packageName, R.layout.notification)
            remoteViews.setTextViewText(R.id.notText, getString(R.string.press_to_open))
            remoteViews.setOnClickPendingIntent(R.id.root, pendingIntent)
            remoteViews.setOnClickPendingIntent(R.id.imageButton222, pendingIntentClose)
// Создаем уведомление
            val builder = NotificationCompat.Builder(this, "appMirrorChannel")
                .setSmallIcon(R.drawable.app_icon)
                .setContentTitle("BeauttyMirror")
                .setCustomContentView(remoteViews)
                .setStyle(NotificationCompat.DecoratedCustomViewStyle())
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(false) // флаг, который делает уведомление невозможным для закрытия свайпом
                .setOngoing(true) // флаг, который делает уведомление невозможным для закрытия пользователем

// Отображаем уведомление
            //notificationManager.notify(notificationId, builder.build())
        }
        super.onDestroy()
    }

    fun onClick(v: View){
        val tv = v as ConstraintLayout
        when(tv.tag.toString()){
            "1"->{
                votes!!["Shave"]?.let { userViewModel.addVote("Shave", it+1) }
            }
            "2"->{
                votes!!["Putting_on_makeup"]?.let { userViewModel.addVote("Putting_on_makeup", it+1) }
            }
            "3"->{
                votes!!["Putting_on_lenses"]?.let { userViewModel.addVote("Putting_on_lenses", it+1) }
            }
            "4"->{
                votes!!["Use_in_dark"]?.let { userViewModel.addVote("Use_in_dark", it+1) }
            }
            "5"->{
                votes!!["Brush_your_teeth"]?.let { userViewModel.addVote("Brush_your_teeth", it+1) }
            }
            "6"->{
                votes!!["Just_to_look"]?.let { userViewModel.addVote("Just_to_look", it+1) }
            }
        }

        this.finish()
    }
}
package mirror.hand.makeup.shaving.best.zoom.pocket.selfie

import android.Manifest.permission.*
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.RemoteViews
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import mirror.hand.makeup.shaving.best.zoom.pocket.selfie.tools.NotificationReceiver


class CameraAccessActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(mirror.hand.makeup.shaving.best.zoom.pocket.selfie.R.layout.activity_camera_access)
    }

    fun getAccess(v: View){
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
                .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
                .setContentIntent(pendingIntent)
                .setAutoCancel(false) // флаг, который делает уведомление невозможным для закрытия свайпом
                .setOngoing(true) // флаг, который делает уведомление невозможным для закрытия пользователем

// Отображаем уведомление
            //notificationManager.notify(notificationId, builder.build())
        }
        requestPermissions(arrayOf(CAMERA, ACCESS_NOTIFICATION_POLICY, WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE, RECORD_AUDIO), 100)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (permissions != null) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
        when (requestCode) {
            100 -> {

                // если пользователь закрыл запрос на разрешение, не дав ответа, массив grantResults будет пустым
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    finish()
                } else {
                    Toast.makeText(this@CameraAccessActivity, getString(R.string.permission_denied), Toast.LENGTH_LONG).show()
                }
                return
            }
        }
    }
}
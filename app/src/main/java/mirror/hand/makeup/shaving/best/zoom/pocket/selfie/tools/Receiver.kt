package mirror.hand.makeup.shaving.best.zoom.pocket.selfie.tools

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationManagerCompat

class NotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "mirror.hand.makeup.shaving.best.zoom.pocket.selfie.notification.ACTION_CLOSE") {
            val notificationId = intent.getIntExtra("notification_id", 0)
            val notificationManager = NotificationManagerCompat.from(context)
            notificationManager.cancel(notificationId)
            /*val sharedPreference = context.getSharedPreferences("appSettings", Context.MODE_PRIVATE)
            var editor = sharedPreference.edit()
            editor.putBoolean("isNotificationActive", false)
            editor.apply()*/
        }
    }
}
package mirror.hand.makeup.shaving.best.zoom.pocket.selfie

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.Animation.AnimationListener
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import com.android.billingclient.api.*
import com.android.billingclient.api.BillingClient.SkuType.SUBS
import mirror.hand.makeup.shaving.best.zoom.pocket.selfie.VM.MainViewModel


class SplashActivity : AppCompatActivity() {
    private val viewModel by viewModels<MainViewModel>()

    private lateinit var billingClient: BillingClient
    private val skuList = listOf("weeksubscription", "monthsubscription")
    private lateinit var skuDetailsList: List<SkuDetails>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        //loading data
        viewModel.loadVotes()
        viewModel.loadTexts()

        //syncronizedGP
        val purchasesUpdatedListener =
            PurchasesUpdatedListener { billingResult, purchases ->
                // To be implemented in a later section.
            }

        var billingClient = BillingClient.newBuilder(this)
            .setListener(purchasesUpdatedListener)
            .enablePendingPurchases()
            .build()

        startConnection(billingClient)

        //start notification
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

// уникальный идентификатор уведомления
        val notificationId = 257894

// проверяем, было ли уже показано уведомление
        val activeNotifications = notificationManager.activeNotifications
        val notificationAlreadyShown = activeNotifications.any { it.id == notificationId }

        if (!notificationAlreadyShown&&viewModel.isNotificationActive) {
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
                PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

// Создаем уведомление
            val builder = NotificationCompat.Builder(this, "appMirrorChannel")
                .setSmallIcon(R.drawable.app_icon)
                .setContentTitle("BeauttyMirror")
                .setContentText("Нажмите, чтобы открыть")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(false) // флаг, который делает уведомление невозможным для закрытия свайпом
                .setOngoing(true) // флаг, который делает уведомление невозможным для закрытия пользователем

// Отображаем уведомление
            notificationManager.notify(notificationId, builder.build())
        }


        val tv = findViewById<TextView>(R.id.splashText)
        tv.text = viewModel.splashText
        tv.setOnClickListener(View.OnClickListener {
            val anim = AnimationUtils.loadAnimation(this, R.anim.diagonal)
            anim.fillAfter = true
            findViewById<TextView>(R.id.splashText).startAnimation(anim)
            anim.setAnimationListener(object : AnimationListener {
                override fun onAnimationStart(animation: Animation) {}
                override fun onAnimationEnd(animation: Animation) {
                    finish()
                    overridePendingTransition(R.anim.diagonal,R.anim.alpha)
                }

                override fun onAnimationRepeat(animation: Animation) {}
            })
        })
        Handler(Looper.getMainLooper()).postDelayed({
            close()
        }, 3000) // 3000 is the delayed time in milliseconds.
    }

    fun close(){
        val anim = AnimationUtils.loadAnimation(this, R.anim.diagonal)
        anim.fillAfter = true
        findViewById<TextView>(R.id.splashText).startAnimation(anim)
        anim.setAnimationListener(object : AnimationListener {
            override fun onAnimationStart(animation: Animation) {}
            override fun onAnimationEnd(animation: Animation) {
                finish()
                overridePendingTransition(R.anim.diagonal,R.anim.alpha)
            }

            override fun onAnimationRepeat(animation: Animation) {}
        })
    }

    private fun startConnection(billingClient : BillingClient) {
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    billingClient.queryPurchaseHistoryAsync(
                        SUBS,
                        PurchaseHistoryResponseListener { billingResult, list ->
                            Log.e("TAG", "checkCached result: $list")
                            if (list == null||list.size==0) {
                                viewModel.subscriptionType = "off"
                                viewModel.isADActive = true
                                return@PurchaseHistoryResponseListener
                            }
                            for (ps in list) {
                                println("PAYMENT: " + ps.skus.firstOrNull() + " : " + ps.getPurchaseTime());
                            }
                        })
                }
            }

            override fun onBillingServiceDisconnected() {
                startConnection(billingClient)
            }
        })
    }
}
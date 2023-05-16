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
import android.widget.Button
import android.widget.ImageButton
import android.widget.RemoteViews
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.app.NotificationCompat
import androidx.viewpager.widget.ViewPager
import com.android.billingclient.api.*
import mirror.hand.makeup.shaving.best.zoom.pocket.selfie.VM.MainViewModel
import mirror.hand.makeup.shaving.best.zoom.pocket.selfie.adapters.OnboardingViewAdapter
import com.tbuonomo.viewpagerdotsindicator.SpringDotsIndicator
import mirror.hand.makeup.shaving.best.zoom.pocket.selfie.tools.NotificationReceiver

class OnboardingActivity : AppCompatActivity(), PurchasesUpdatedListener {
    private val userViewModel by viewModels<MainViewModel>()

    lateinit var billingClient : BillingClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)

        billingClient = BillingClient.newBuilder(this)
            .enablePendingPurchases()
            .setListener(this)
            .build()
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {

                }
            }

            override fun onBillingServiceDisconnected() {
                Toast.makeText(applicationContext, "server disconnected", Toast.LENGTH_SHORT).show()
            }
        })

        val later = findViewById<TextView>(R.id.onboardingLater)
        val recover = findViewById<TextView>(R.id.onboardingRecover)
        val pager = findViewById<ViewPager>(R.id.view_pager)
        val indicator = findViewById<SpringDotsIndicator>(R.id.dots_indicator)
        val adapter = OnboardingViewAdapter(this)
        val button = findViewById<Button>(R.id.onboarding_next)
        val close = findViewById<ImageButton>(R.id.closeOnboarding)
        val title = findViewById<TextView>(R.id.onboarding_title)
        val subtitle = findViewById<TextView>(R.id.onboarding_subtitle)
        pager.adapter = adapter
        indicator.attachTo(pager)
        button.setOnClickListener(View.OnClickListener {
            if(adapter.currentItem!=5) {
                adapter.currentItem++
                when(adapter.currentItem){
                    0->{
                        title.text = resources.getString(R.string.onboarding_title_1)
                        subtitle.text = resources.getString(R.string.onboarding_subtitle_1)
                    }
                    1->{
                        title.text = resources.getString(R.string.onboarding_title_2)
                        subtitle.text = resources.getString(R.string.onboarding_subtitle_2)
                    }
                    2->{
                        title.text = resources.getString(R.string.onboarding_title_3)
                        subtitle.text = resources.getString(R.string.onboarding_subtitle_3)
                    }
                    3->{
                        title.text = resources.getString(R.string.onboarding_title_4)
                        subtitle.text = resources.getString(R.string.onboarding_subtitle_4)
                    }
                    4->{
                        title.text = resources.getString(R.string.onboarding_title_5)
                        subtitle.text = resources.getString(R.string.onboarding_subtitle_5)
                    }
                    5->{
                        title.text = resources.getString(R.string.onboarding_title_6)
                        subtitle.text = resources.getString(R.string.onboarding_subtitle_6)
                    }
                }
            }
            else {
                val intent = Intent(this@OnboardingActivity, PayActivity::class.java)
                intent.putExtra("showVote", true)
                finish()
                startActivity(intent)
                return@OnClickListener
            }
            if (adapter.currentItem==5){
                button.text = this.resources.getString(R.string.start_free)
                close.visibility = View.GONE
                later.visibility = View.VISIBLE
                recover.visibility = View.VISIBLE
            }
            pager.currentItem = adapter.currentItem
        })

        later.setOnClickListener(View.OnClickListener {
            val pollIntent = Intent(this@OnboardingActivity, PollActivity::class.java)
            finish()
            startActivity(pollIntent)
        })

        recover.setOnClickListener{
            val skuList = listOf("mons67r", "year201r")
            val params = SkuDetailsParams.newBuilder()
                .setType(BillingClient.SkuType.SUBS)
                .setSkusList(skuList)
                .build()

            billingClient.querySkuDetailsAsync(params) { billingResult, skuDetailsList ->
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && skuDetailsList != null) {
                    val subscription = skuDetailsList[0]
                    val flowParams = BillingFlowParams.newBuilder()
                        .setSkuDetails(subscription)
                        .build()
                    billingClient.launchBillingFlow(this@OnboardingActivity, flowParams)
                }
            }
        }

        close.setOnClickListener{
            val intent = Intent(this@OnboardingActivity, PayActivity::class.java)
            intent.putExtra("showVote", true)
            finish()
            startActivity(intent)
            return@setOnClickListener
        }
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
            remoteViews.setTextViewText(R.id.notText, "Нажмите, чтобы открыть")
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
            notificationManager.notify(notificationId, builder.build())
        }
        super.onDestroy()
    }

    fun openPP(v : View){
        val intent = Intent(this@OnboardingActivity, LongTextActivity::class.java)
        intent.putExtra("type", "privacy")
        startActivity(intent)
    }
    fun openTerms(v : View){
        val intent = Intent(this@OnboardingActivity, LongTextActivity::class.java)
        intent.putExtra("type", "terms")
        startActivity(intent)
    }

    override fun onPurchasesUpdated(p0: BillingResult, p1: MutableList<Purchase>?) {
        if (p0?.responseCode == BillingClient.BillingResponseCode.OK && p1 != null) {
            for (purchase in p1) {
                if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED &&
                    purchase.skus == arrayListOf("mons67r", "year201r")) {
                    // продлеваем подписку
                    val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                        .setPurchaseToken(purchase.purchaseToken)
                        .build()
                    billingClient.acknowledgePurchase(acknowledgePurchaseParams) { billingResult ->
                        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                            userViewModel.subscriptionType = "year"
                            userViewModel.isADActive = false
                            finish()
                        }
                    }
                }
            }
        }
    }
}
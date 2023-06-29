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
import android.widget.RemoteViews
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import com.android.billingclient.api.*
import com.android.billingclient.api.BillingClient.SkuType.SUBS
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import mirror.hand.makeup.shaving.best.zoom.pocket.selfie.VM.MainViewModel
import mirror.hand.makeup.shaving.best.zoom.pocket.selfie.tools.NotificationReceiver


class SplashActivity : AppCompatActivity() {
    private val viewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        //loading data
        viewModel.loadVotes()
        viewModel.loadTexts()
        if (viewModel.shareAppString=="Error")viewModel.getShareAppLink()
        if (viewModel.myAppsString=="Error")viewModel.getMyAppsLink()
        val remoteConfig: FirebaseRemoteConfig = Firebase.remoteConfig
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 3600
        }
        remoteConfig.setConfigSettingsAsync(configSettings)
        remoteConfig.fetchAndActivate()
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val updated = task.result
                    val adBannerTimer = remoteConfig.getDouble("adBannerTimer").toInt()
                    val rateRequestTimer = remoteConfig.getDouble("rateRequestTimer").toInt()
                    val splashDelay = remoteConfig.getDouble("splashDelay").toInt()
                    val paywallTimer = remoteConfig.getDouble("paywallTimer").toInt()
                    val interstitialTimer = remoteConfig.getDouble("interstitialTimer").toInt()

                    viewModel.adBannerTimer = adBannerTimer
                    viewModel.rateRequestTimer = rateRequestTimer
                    viewModel.splashDelay = splashDelay
                    viewModel.paywallTimer = paywallTimer
                    viewModel.interstitialTimer = interstitialTimer

                    println("Fetch and activate succeeded")
                } else {
                    println("Fetch failed")
                }
            }

        //syncronizedGP
        val purchasesUpdatedListener =
            PurchasesUpdatedListener { billingResult, purchases ->
                // To be implemented in a later section.
            }

        var billingClient = BillingClient.newBuilder(this)
            .setListener(purchasesUpdatedListener)
            .enablePendingPurchases()
            .build()

        //startConnection(billingClient)
        checkActiveSubscription(billingClient)

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
                }

                override fun onAnimationRepeat(animation: Animation) {}
            })
        })
        Handler(Looper.getMainLooper()).postDelayed({
            close()
        }, 1500) // 3000 is the delayed time in milliseconds.
    }

    fun close(){
        val anim = AnimationUtils.loadAnimation(this, R.anim.diagonal)
        anim.fillAfter = true
        findViewById<TextView>(R.id.splashText).startAnimation(anim)
        anim.setAnimationListener(object : AnimationListener {
            override fun onAnimationStart(animation: Animation) {}
            override fun onAnimationEnd(animation: Animation) {
                finish()
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
    private fun checkActiveSubscription(billingClient: BillingClient) {
        val purchasesResult = billingClient.queryPurchases(BillingClient.SkuType.SUBS)
        if (purchasesResult.purchasesList != null && purchasesResult.purchasesList!!.isNotEmpty()) {
            for (purchase in purchasesResult.purchasesList!!) {
                // Проверка статуса подписки
                if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED && purchase.isAutoRenewing) {
                    viewModel.subscriptionType = "on"
                    viewModel.isADActive = false
                    return
                }
            }
        }
        viewModel.subscriptionType = "off"
        viewModel.isADActive = true
    }
}
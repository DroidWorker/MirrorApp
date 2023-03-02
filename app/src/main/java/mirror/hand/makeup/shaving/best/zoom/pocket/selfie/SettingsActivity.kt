package mirror.hand.makeup.shaving.best.zoom.pocket.selfie

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.android.billingclient.api.*
import com.google.android.gms.common.ErrorDialogFragment.newInstance
import com.google.android.material.bottomsheet.BottomSheetDialog
import mirror.hand.makeup.shaving.best.zoom.pocket.selfie.VM.MainViewModel
import mirror.hand.makeup.shaving.best.zoom.pocket.selfie.tools.NotificationReceiver
import mirror.hand.makeup.shaving.best.zoom.pocket.selfie.tools.SmoothBottomSheetDialog


class SettingsActivity : AppCompatActivity(), PurchasesUpdatedListener {
    var sky = "burger"
    var rate = -1
    private val viewModel by viewModels<MainViewModel>()
    lateinit var billingClient : BillingClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val notificationSwitch = findViewById<SwitchCompat>(R.id.switch1)
        if (viewModel.isNotificationActive){
            notificationSwitch.isChecked = true
        }
        notificationSwitch.setOnCheckedChangeListener{button, isActive->
            viewModel.isNotificationActive = isActive
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val notificationId = 257894

            val activeNotifications = notificationManager.activeNotifications
            val notificationAlreadyShown = activeNotifications.any { it.id == notificationId }
            if (isActive){
                //start notification
                if (!notificationAlreadyShown) {
                    // создаем уведомление и выводим его

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        val channel = NotificationChannel(
                            "appMirrorChannel",
                            "AppMirror",
                            NotificationManager.IMPORTANCE_HIGH
                        )
                        notificationManager.createNotificationChannel(channel)
                    }

                    val intent = Intent(this, MainActivity::class.java)
                    val pendingIntent =
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_MUTABLE)
                        else PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

                    val intentClose = Intent(this, NotificationReceiver::class.java).apply {
                        action = "mirror.hand.makeup.shaving.best.zoom.pocket.selfie.notification.ACTION_CLOSE"
                        putExtra("notification_id", notificationId)
                    }

                    val pendingIntentClose =
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) PendingIntent.getBroadcast(this, 0, intentClose, PendingIntent.FLAG_IMMUTABLE)
                        else PendingIntent.getActivity(this, 0, intentClose, PendingIntent.FLAG_UPDATE_CURRENT)

                    val remoteViews = RemoteViews(packageName, R.layout.notification)
                    remoteViews.setTextViewText(R.id.notText, "Нажмите, чтобы открыть")
                    remoteViews.setOnClickPendingIntent(R.id.root, pendingIntent)
                    remoteViews.setOnClickPendingIntent(R.id.imageButton222, pendingIntentClose)

                    val builder = NotificationCompat.Builder(this, "appMirrorChannel")
                        .setSmallIcon(R.drawable.app_icon)
                        .setContentTitle("BeautyMirror")
                        .setCustomContentView(remoteViews)
                        .setStyle(NotificationCompat.DecoratedCustomViewStyle())
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setAutoCancel(false) // флаг, который делает уведомление невозможным для закрытия свайпом
                        .setOngoing(true) // флаг, который делает уведомление невозможным для закрытия пользователем

                    notificationManager.notify(notificationId, builder.build())
                }
            }
            else{
                notificationManager.cancel(notificationId)
            }
        }

        val purchasesUpdatedListener =
            PurchasesUpdatedListener { billingResult, purchases ->
                // To be implemented in a later section.
            }

        billingClient = BillingClient.newBuilder(this)
            .setListener(purchasesUpdatedListener)
            .enablePendingPurchases()
            .build()

        startConnection(billingClient)
    }

    fun onCloseClick(v: View){
        finish()
    }

    fun onSupportClick(v: View){
        val bottomSheetDialog = SmoothBottomSheetDialog(this)
        bottomSheetDialog.setContentView(R.layout.bottom_sheet_dialog_support)

        val icecream = bottomSheetDialog.findViewById<TextView>(R.id.bsdIcecream)
        val coffee = bottomSheetDialog.findViewById<TextView>(R.id.bsdCupofCoffee)
        val burger = bottomSheetDialog.findViewById<TextView>(R.id.bsdBurger)
        val pizza = bottomSheetDialog.findViewById<TextView>(R.id.bsdPizza)
        val dinner = bottomSheetDialog.findViewById<TextView>(R.id.bsdDinner)

        icecream?.setOnClickListener{subscribe(0)}
        coffee?.setOnClickListener{subscribe(1)}
        burger?.setOnClickListener{subscribe(2)}
        pizza?.setOnClickListener{subscribe(3)}
        dinner?.setOnClickListener{subscribe(4)}

        bottomSheetDialog.show()
    }

    fun onBuyPremClick(v: View){
        val intent = Intent(this@SettingsActivity, PayActivity::class.java)
        startActivity(intent)
    }

    fun onReviewClick(v: View){
        val bottomSheetDialog = SmoothBottomSheetDialog(this)
        bottomSheetDialog.setContentView(R.layout.bottom_sheet_dialog_review)

        var answer = "empty"

        val clMain = bottomSheetDialog.findViewById<ConstraintLayout>(R.id.constraintLayoutMain)
        val cllist1 = bottomSheetDialog.findViewById<ConstraintLayout>(R.id.qlist1)
        val cllist2 = bottomSheetDialog.findViewById<ConstraintLayout>(R.id.qlist2)
        val rbg = bottomSheetDialog.findViewById<RadioGroup>(R.id.radioGroup)
        val rbg2 = bottomSheetDialog.findViewById<RadioGroup>(R.id.radioGroup2)
        val buttonNext = bottomSheetDialog.findViewById<Button>(R.id.buttonNexxt)
        val buttonNex2 = bottomSheetDialog.findViewById<Button>(R.id.buttonNexxt2)
        val text = bottomSheetDialog.findViewById<EditText>(R.id.BSDReditText)
        val send = bottomSheetDialog.findViewById<Button>(R.id.BSDRsend)

        rbg?.setOnCheckedChangeListener{group, checkedID->
            when(checkedID){
                R.id.radioButton->{
                    answer = "У меня возникла проблема"
                }
                R.id.radioButton3->{
                    answer = "Нужные мне функции отсутствуют"
                }
                R.id.radioButton4->{
                    answer = "У меня есть идея/предложение"
                }
                R.id.radioButton5->{
                    answer = "Приложение неудобно"
                }
                R.id.radioButton6->{
                    answer = "Много рекламы"
                }
                R.id.radioButton7->{
                    answer = "Мне нравится приложение"
                }
                R.id.radioButton9->{
                    answer = "Другое"
                }
            }
            buttonNext?.isClickable = true
        }

        buttonNext?.setOnClickListener{
            when(answer){
                "У меня возникла проблема"->{
                    cllist1?.visibility = View.GONE
                    cllist2?.visibility = View.VISIBLE
                }
                "Нужные мне функции отсутствуют"->{
                    cllist1?.visibility = View.GONE
                    clMain?.visibility = View.VISIBLE
                }
                "У меня есть идея/предложение"->{
                    cllist1?.visibility = View.GONE
                    clMain?.visibility = View.VISIBLE
                }
                "Приложение неудобно"->{
                    cllist1?.visibility = View.GONE
                    clMain?.visibility = View.VISIBLE
                }
                "Много рекламы"->{
                    val intent = Intent(this@SettingsActivity, PayActivity::class.java)
                    finish()
                    startActivity(intent)
                }
                "Мне нравится приложение"->{
                    onRateClick(v)
                    bottomSheetDialog.hide()
                }
                "Другое"->{
                    cllist1?.visibility = View.GONE
                    clMain?.visibility = View.VISIBLE
                }
            }

        }
        rbg2?.setOnCheckedChangeListener{group, checkedID->
            when(checkedID){
                R.id.radioButton10->{
                    answer = "Камера не работа"
                }
                R.id.radioButton11->{
                    answer = "Плохое качество изображения"
                }
                R.id.radioButton12->{
                    answer = "Проблемы с 3D режимом"
                }
                R.id.radioButton13->{
                    answer = "Другое"
                }
            }
        }
        buttonNex2?.setOnClickListener{
            cllist2?.visibility = View.GONE
            clMain?.visibility = View.VISIBLE
        }

        send?.setOnClickListener{
            if (text?.text!!.length>2){
                val intent = Intent(Intent.ACTION_SENDTO).apply {
                    data = Uri.parse("mailto:")
                    putExtra(Intent.EXTRA_EMAIL, arrayOf("smarteasyapps17@gmail.com"))
                    putExtra(Intent.EXTRA_SUBJECT, "Обращение пользователя AppMirror")
                    putExtra(Intent.EXTRA_TEXT, "$answer, ")
                }
                if (intent.resolveActivity(this.packageManager) != null) {
                    startActivity(Intent.createChooser(intent, "Choose an Email client :"));
                } else {
                    Toast.makeText(this, "No email app found", Toast.LENGTH_SHORT).show()
                }
            }
            bottomSheetDialog.hide()
        }

        bottomSheetDialog.show()
    }

    fun shareText(v: View) {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_TEXT, viewModel.shareAppString)
        this.startActivity(Intent.createChooser(intent, "Share via"))
    }

    fun onRateClick(v: View){
        val bottomSheetDialog = SmoothBottomSheetDialog(this)
        bottomSheetDialog.setContentView(R.layout.bottom_sheet_dialog_rate)

        val step1 = bottomSheetDialog.findViewById<ConstraintLayout>(R.id.bsdrateStep1)
        val stepOK = bottomSheetDialog.findViewById<ConstraintLayout>(R.id.bsdrateStepOK)
        val stepBAD = bottomSheetDialog.findViewById<ConstraintLayout>(R.id.bsdrateStepBAD)

        //if like
        val buttonOK = bottomSheetDialog.findViewById<Button>(R.id.buttonOK)
        //if hate
        val buttonBAD = bottomSheetDialog.findViewById<Button>(R.id.buttonBAD)

        buttonOK?.setOnClickListener {
            rate=-1
            step1?.visibility = View.GONE
            stepOK?.visibility = View.VISIBLE

            val star1 = bottomSheetDialog.findViewById<ImageView>(R.id.star1)
            val star2 = bottomSheetDialog.findViewById<ImageView>(R.id.star2)
            val star3 = bottomSheetDialog.findViewById<ImageView>(R.id.star3)
            val star4 = bottomSheetDialog.findViewById<ImageView>(R.id.star4)
            val star5 = bottomSheetDialog.findViewById<ImageView>(R.id.star5)
            val buttonRate = bottomSheetDialog.findViewById<Button>(R.id.bsdrateRate)

            star1?.setOnClickListener{
                selectStar(star1, star2, star3, star4, star5, buttonRate, 1)
            }
            star2?.setOnClickListener{
                selectStar(star1, star2, star3, star4, star5, buttonRate, 2)
            }
            star3?.setOnClickListener{
                selectStar(star1, star2, star3, star4, star5, buttonRate, 3)
            }
            star4?.setOnClickListener{
                selectStar(star1, star2, star3, star4, star5, buttonRate, 4)
            }
            star5?.setOnClickListener{
                selectStar(star1, star2, star3, star4, star5, buttonRate, 5)
            }

            buttonRate?.setOnClickListener{
                if (rate in 1..3){
                    bottomSheetDialog.hide()
                    return@setOnClickListener
                }
                else if(rate>3){
                    try {
                        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=mirror.hand.makeup.shaving.best.zoom.pocket.selfie")))
                    } catch (e: ActivityNotFoundException) {
                        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=mirror.hand.makeup.shaving.best.zoom.pocket.selfie")))
                    }
                    return@setOnClickListener
                }
            }
        }

        buttonBAD?.setOnClickListener{
            step1?.visibility = View.GONE
            stepBAD?.visibility = View.VISIBLE

            val etText = bottomSheetDialog.findViewById<EditText>(R.id.BSDRateBadEditText)
            val buttonSendText = bottomSheetDialog.findViewById<Button>(R.id.BSDRateBadSend)

            buttonSendText?.setOnClickListener{
                if (etText?.text?.length!! >3){
                    val intent = Intent(Intent.ACTION_SENDTO).apply {
                        data = Uri.parse("mailto:")
                        putExtra(Intent.EXTRA_EMAIL, arrayOf("smarteasyapps17@gmail.com"))
                        putExtra(Intent.EXTRA_SUBJECT, "mirrorAPP")
                        putExtra(Intent.EXTRA_TEXT, etText!!.text)
                    }
                    if (intent.resolveActivity(this.packageManager) != null) {
                        startActivity(Intent.createChooser(intent, "Send mail..."))
                    } else {
                        Toast.makeText(this, "No email app found", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        bottomSheetDialog.show()
    }

    fun onOtherAppsClick(v: View){
        try {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://search?q=pub:SmartEasyApps")))
        } catch (e: ActivityNotFoundException) {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(viewModel.myAppsString)))
        }
    }

    private fun selectStar(iv1: ImageView?, iv2: ImageView?,iv3: ImageView?,iv4: ImageView?,iv5: ImageView?, b: Button?, r: Int){
        if (r<=0) return
        else{
            iv1?.setImageDrawable(
                ContextCompat.getDrawable(
                    this@SettingsActivity,
                    R.drawable.star
                ))
            iv2?.setImageDrawable(
                ContextCompat.getDrawable(
                    this@SettingsActivity,
                    R.drawable.star
                ))
            iv3?.setImageDrawable(
                ContextCompat.getDrawable(
                    this@SettingsActivity,
                    R.drawable.star
                ))
            iv4?.setImageDrawable(
                ContextCompat.getDrawable(
                    this@SettingsActivity,
                    R.drawable.star
                ))
            iv5?.setImageDrawable(
                ContextCompat.getDrawable(
                    this@SettingsActivity,
                    R.drawable.star
                ))
        }
        rate=r
        b?.background = ContextCompat.getDrawable(this@SettingsActivity, R.drawable.rounded_white)
        if(r>3) b?.text = getString(R.string.rate_on_pm)
        else b?.text = getString(R.string.rate)
        if (r>0) {
            iv1?.setImageDrawable(
                ContextCompat.getDrawable(
                    this@SettingsActivity,
                    R.drawable.starfilles
                )
            )
        } else return
        if (r>1) {
            iv2?.setImageDrawable(
                ContextCompat.getDrawable(
                    this@SettingsActivity,
                    R.drawable.starfilles
                )
            )
        } else return
        if (r>2) {
            iv3?.setImageDrawable(
                ContextCompat.getDrawable(
                    this@SettingsActivity,
                    R.drawable.starfilles
                )
            )
        } else return
        if (r>3) {
            iv4?.setImageDrawable(
                ContextCompat.getDrawable(
                    this@SettingsActivity,
                    R.drawable.starfilles
                )
            )
        } else return
        if (r>4) {
            iv5?.setImageDrawable(
                ContextCompat.getDrawable(
                    this@SettingsActivity,
                    R.drawable.starfilles
                )
            )
        } else return
    }

    private fun startConnection(billingClient : BillingClient) {
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    billingClient.queryPurchaseHistoryAsync(
                        BillingClient.SkuType.INAPP,
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

    fun subscribe(skyId: Int) {
        //check if service is already connected
        println("steeeeeeeeeep1")
        if (billingClient!!.isReady) {
            initiatePurchase(true, skyId)
        } else {
            billingClient = BillingClient.newBuilder(this).enablePendingPurchases().setListener(this).build()
            billingClient!!.startConnection(object : BillingClientStateListener {
                override fun onBillingSetupFinished(billingResult: BillingResult) {
                    if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                        initiatePurchase(false, skyId)
                    } else {
                        Toast.makeText(applicationContext, "Error " + billingResult.debugMessage, Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onBillingServiceDisconnected() {
                    Toast.makeText(applicationContext, "Service Disconnected ", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    private fun initiatePurchase(isSubscr: Boolean, skyId: Int) {
        println("stttteeeep2")
        val skuList: MutableList<String> = ArrayList()
        /*skuList.add("deliciousdinner")
        skuList.add("burger")
        skuList.add("pizza")
        skuList.add("cupofcoffee")
        skuList.add("icecream")*/
        skuList.add(
            when(skyId){
                0->"icecream"
                1->"cupofcoffee"
                2->"burger"
                3->"pizza"
                4->"deliciousdinner"
                else->"icecream"
            }
        )
        sky = skuList[0]
        val params = SkuDetailsParams.newBuilder()
        params.setSkusList(skuList).setType(BillingClient.SkuType.INAPP)
        val billingResult = if (isSubscr)billingClient!!.isFeatureSupported(BillingClient.FeatureType.SUBSCRIPTIONS)
                            else billingClient!!.isFeatureSupported(BillingClient.FeatureType.SUBSCRIPTIONS)
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
            billingClient!!.querySkuDetailsAsync(params.build()
            ) { billingResult, skuDetailsList ->
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    if (skuDetailsList != null && skuDetailsList.size > 0) {
                        val flowParams = BillingFlowParams.newBuilder()
                            .setSkuDetails(skuDetailsList[0])
                            .build()
                        billingClient!!.launchBillingFlow(this@SettingsActivity, flowParams)
                    } else {
                        println("ItemNotfound")
                        //try to add subscription item "sub_example" in google play console
                        Toast.makeText(applicationContext, "Item not Found", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(applicationContext,
                        " Error " + billingResult.debugMessage, Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(applicationContext,
                "Sorry Subscription not Supported. Please Update Play Store", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onPurchasesUpdated(billingResult: BillingResult, purchases: List<Purchase>?) {
        //if item subscribed
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
            handlePurchases(purchases)
        }
        else if (billingResult.responseCode == BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED) {
            val queryAlreadyPurchasesResult = billingClient!!.queryPurchases(BillingClient.SkuType.INAPP)
            val alreadyPurchases = queryAlreadyPurchasesResult.purchasesList
            alreadyPurchases?.let { handlePurchases(it) }
        }
        else if (billingResult.responseCode == BillingClient.BillingResponseCode.USER_CANCELED) {
            Toast.makeText(applicationContext, "Purchase Canceled", Toast.LENGTH_SHORT).show()
        }
        else {
            Toast.makeText(applicationContext, "Error " + billingResult.debugMessage, Toast.LENGTH_SHORT).show()
        }
    }
    fun handlePurchases(purchases: List<Purchase>) {
        for (purchase in purchases) {
            //if item is purchased
            if (purchase.skus.contains(sky) && purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {

                // else purchase is valid
                //if item is purchased and not acknowledged
                if (!purchase.isAcknowledged) {
                    val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                        .setPurchaseToken(purchase.purchaseToken)
                        .build()
                    billingClient!!.acknowledgePurchase(acknowledgePurchaseParams, ackPurchase)
                } else {
                    // Grant entitlement to the user on item purchase
                    // restart activity
                    //if (viewModel.subscriptionType=="off") {
                        //viewModel.subscriptionType = "week"
                        Toast.makeText(applicationContext, "Item Purchased", Toast.LENGTH_SHORT).show()
                        recreate()
                    //}
                }
            } else if (purchase.skus.contains(sky) && purchase.purchaseState == Purchase.PurchaseState.PENDING) {
                Toast.makeText(applicationContext,
                    "Purchase is Pending. Please complete Transaction", Toast.LENGTH_SHORT).show()
            } else if (purchase.skus.contains(sky) && purchase.purchaseState == Purchase.PurchaseState.UNSPECIFIED_STATE) {
                viewModel.subscriptionType = "week"
                viewModel.isADActive = false
                Toast.makeText(applicationContext, "Purchase Status Unknown", Toast.LENGTH_SHORT).show()
            }
        }
    }

    var ackPurchase = AcknowledgePurchaseResponseListener { billingResult ->
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
            //if purchase is acknowledged
            // Grant entitlement to the user. and restart activity
            //viewModel.subscriptionType = "week"
            recreate()
        }
    }
}
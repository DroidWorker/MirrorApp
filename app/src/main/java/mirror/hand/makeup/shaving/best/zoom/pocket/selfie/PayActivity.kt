package mirror.hand.makeup.shaving.best.zoom.pocket.selfie

import ImageAdapterSimple
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.android.billingclient.api.*
import mirror.hand.makeup.shaving.best.zoom.pocket.selfie.VM.MainViewModel
import mirror.hand.makeup.shaving.best.zoom.pocket.selfie.adapters.ImageAdapter
import mirror.hand.makeup.shaving.best.zoom.pocket.selfie.customView.ExpandableHeightGridView

class PayActivity : AppCompatActivity(), PurchasesUpdatedListener {
    private val viewModel by viewModels<MainViewModel>()

    lateinit var billingClient : BillingClient

    var showVote = false

    var tarif = "week"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pay)

        showVote = intent.getBooleanExtra("showVote", false)

        //syncronizedGP
        val purchasesUpdatedListener =
            PurchasesUpdatedListener { billingResult, purchases ->
                // To be implemented in a later section.
            }

        billingClient = BillingClient.newBuilder(this)
            .setListener(this)
            .enablePendingPurchases()
            .build()

        startConnection(billingClient)

        val p1 = findViewById<ConstraintLayout>(R.id.p1)
        val p2 = findViewById<ConstraintLayout>(R.id.p2)
        val rb1 = findViewById<RadioButton>(R.id.radioButton1)
        val rb2 = findViewById<RadioButton>(R.id.radioButton2)
        val payDescription = findViewById<TextView>(R.id.payDescription)
        val maskView = findViewById<ExpandableHeightGridView>(R.id.mascView)

        val txtp1 = resources.getString(R.string.pay_description)+" "+resources.getString(R.string.price1)
        val txtp2 = resources.getString(R.string.pay_description_year)+" "+resources.getString(R.string.price2)
        payDescription.text = txtp1
        rb1.isChecked = true

        val imgs  : Map<String, Bitmap> = mapOf(
            Pair("1", BitmapFactory.decodeResource(resources, R.drawable.image1)),
            Pair("2", BitmapFactory.decodeResource(resources, R.drawable.image2)),
            Pair("3", BitmapFactory.decodeResource(resources, R.drawable.image3)),
            Pair("4", BitmapFactory.decodeResource(resources, R.drawable.image4)),
            Pair("5", BitmapFactory.decodeResource(resources, R.drawable.image5)),
            Pair("6", BitmapFactory.decodeResource(resources, R.drawable.image6))
        )
        val adapter = ImageAdapterSimple(this, imgs, null)
        maskView.isExpanded = true
        maskView.adapter = adapter

        p1.setOnClickListener{
            tarif = "week"
            payDescription.text = txtp1
            p2.background = ContextCompat.getDrawable(this@PayActivity, R.drawable.rounded_corners_off)
            p1.background = ContextCompat.getDrawable(this@PayActivity, R.drawable.rounded_corners)
            rb1.isChecked = true
            rb2.isChecked = false
        }
        p2.setOnClickListener{
            tarif = "year"
            payDescription.text = txtp2
            p1.background = ContextCompat.getDrawable(this@PayActivity, R.drawable.rounded_corners_off)
            p2.background = ContextCompat.getDrawable(this@PayActivity, R.drawable.rounded_corners)
            rb2.isChecked = true
            rb1.isChecked = false
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.isPaywallOpened = false
        billingClient.endConnection()
    }

    fun openPP(v : View){
        val intent = Intent(this@PayActivity, LongTextActivity::class.java)
        intent.putExtra("type", "privacy")
        startActivity(intent)
    }
    fun openTerms(v : View){
        val intent = Intent(this@PayActivity, LongTextActivity::class.java)
        intent.putExtra("type", "terms")
        startActivity(intent)
    }

    fun onLaterClick(v: View){
        if (showVote) {
            val intent = Intent(this@PayActivity, PollActivity::class.java)
            startActivity(intent)
        }
        finish()
    }

    private fun startConnection(billingClient : BillingClient) {
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    billingClient.queryPurchaseHistoryAsync(
                        BillingClient.SkuType.SUBS,
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

    fun subscribe(view: View?) {
        //check if service is already connected
        if (billingClient!!.isReady) {
            initiatePurchase()
        } else {
            billingClient = BillingClient.newBuilder(this).enablePendingPurchases().setListener(this).build()
            billingClient!!.startConnection(object : BillingClientStateListener {
                override fun onBillingSetupFinished(billingResult: BillingResult) {
                    if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                        initiatePurchase()
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

    private fun initiatePurchase() {
        val skuList: MutableList<String> = ArrayList()
        if (tarif=="week")skuList.add("mons67r")
        else skuList.add("year201r")
        val params = SkuDetailsParams.newBuilder()
        params.setSkusList(skuList).setType(BillingClient.SkuType.SUBS)
        val billingResult = billingClient!!.isFeatureSupported(BillingClient.FeatureType.SUBSCRIPTIONS)
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
            billingClient!!.querySkuDetailsAsync(params.build()
            ) { billingResult, skuDetailsList ->
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    if (skuDetailsList != null && skuDetailsList.size > 0) {
                        val flowParams = BillingFlowParams.newBuilder()
                            .setSkuDetails(skuDetailsList[0])
                            .build()
                        billingClient!!.launchBillingFlow(this@PayActivity, flowParams)
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
            val queryAlreadyPurchasesResult = billingClient!!.queryPurchases(BillingClient.SkuType.SUBS)
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
                val sky = if(tarif == "week") "mons67r"
                            else "year201r"
            if (purchase.skus.contains(sky) && purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {

                // else purchase is valid
                //if item is purchased and not acknowledged
                if (!purchase.isAcknowledged) {
                    val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                        .setPurchaseToken(purchase.purchaseToken)
                        .build()
                    billingClient!!.acknowledgePurchase(acknowledgePurchaseParams, ackPurchase)
                    viewModel.isADActive = false
                } else {
                    // Grant entitlement to the user on item purchase
                    // restart activity
                        viewModel.isADActive = false
                    if (viewModel.subscriptionType=="off") {
                        viewModel.subscriptionType = if(tarif == "week") "week"
                                                        else "year"
                        Toast.makeText(applicationContext, "Item Purchased", Toast.LENGTH_SHORT).show()
                        //recreate()
                        finish()
                    }
                }
            } else if (purchase.skus.contains(sky) && purchase.purchaseState == Purchase.PurchaseState.PENDING) {
                Toast.makeText(applicationContext,
                    "Purchase is Pending. Please complete Transaction", Toast.LENGTH_SHORT).show()
            } else if (purchase.skus.contains(sky) && purchase.purchaseState == Purchase.PurchaseState.UNSPECIFIED_STATE) {
                Toast.makeText(applicationContext, "Purchase Status Unknown", Toast.LENGTH_SHORT).show()
            }
        }
    }

    var ackPurchase = AcknowledgePurchaseResponseListener { billingResult ->
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
            //if purchase is acknowledged
            // Grant entitlement to the user. and restart activity
            viewModel.subscriptionType = "week"
            recreate()
        }
    }

    fun launchSubscriptionRestoreFlow(v: View) {
        if (!billingClient.isReady) {
            billingClient = BillingClient.newBuilder(this)
                .enablePendingPurchases()
                .setListener(this)
                .build()
        }

        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    val skuList = ArrayList<String>()
                    skuList.add("mons67r")
                    skuList.add("year201r")

                    val params = SkuDetailsParams.newBuilder()
                        .setSkusList(skuList)
                        .setType(BillingClient.SkuType.SUBS)
                        .build()

                    billingClient.querySkuDetailsAsync(params) { billingResult, skuDetailsList ->
                        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && skuDetailsList != null) {
                            for (skuDetails in skuDetailsList) {
                                if (skuDetails.sku == "mons67r") {
                                    val flowParams = BillingFlowParams.newBuilder()
                                        .setSkuDetails(skuDetails)
                                        .build()

                                    billingClient.launchBillingFlow(this@PayActivity, flowParams)
                                }
                            }
                        }
                    }
                }
            }

            override fun onBillingServiceDisconnected() {
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
            }
        })
    }

}
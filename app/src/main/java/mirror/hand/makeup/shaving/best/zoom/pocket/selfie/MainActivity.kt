package mirror.hand.makeup.shaving.best.zoom.pocket.selfie

import android.Manifest
import android.Manifest.permission.CAMERA
import android.Manifest.permission.RECORD_AUDIO
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.*
import android.hardware.Camera
import android.hardware.camera2.*
import android.hardware.camera2.CameraCaptureSession.CaptureCallback
import android.hardware.camera2.CaptureRequest.LENS_FOCUS_DISTANCE
import android.hardware.camera2.CaptureResult.LENS_FOCUS_DISTANCE
import android.media.*
import android.media.ImageReader.OnImageAvailableListener
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import android.util.Size
import android.view.*
import android.view.animation.AnimationUtils
import android.view.animation.LinearInterpolator
import android.view.animation.OvershootInterpolator
import android.view.animation.TranslateAnimation
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.view.setPadding
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.android.material.button.MaterialButton
import mirror.hand.makeup.shaving.best.zoom.pocket.selfie.VM.MainViewModel
import mirror.hand.makeup.shaving.best.zoom.pocket.selfie.adapters.MascsAdapter
import mirror.hand.makeup.shaving.best.zoom.pocket.selfie.tools.*
import mirror.hand.makeup.shaving.best.zoom.pocket.selfie.tools.Timer
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.lang.Math.abs
import java.nio.ByteBuffer
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZonedDateTime
import java.util.*
import kotlin.collections.RandomAccess


class MainActivity : AppCompatActivity() {
    lateinit var ctx : Context
    private val userViewModel by viewModels<MainViewModel>()
    var isAppReady = false
    var isCameraReady = false
    var isCamStarted = false
    var isInShot = false
    var is360Mode = false

    var cameraMode = "photo"

    private var mBackgroundThread: HandlerThread? = null
    private var mBackgroundHandler: Handler? = null

    private lateinit var gestureDetector: GestureDetector

    var vt : VIdeoTool? = null

    var myCameras: ArrayList<CameraService>? = null
    var openedCamera = 1
    var mCameraManager: CameraManager? = null

    var flashlightEnabled = false
    var backlightEnables = false
    var flipEnabled = false
    var isMirrorSelected = true

    private val timerAdBanner : Timer = Timer()
    private val timerRateRequest : Timer = Timer()

    lateinit var mImageView: TextureView
    lateinit var mSurfaceView: SurfaceView
    lateinit var surfaceforVT: Surface
    lateinit var imageContainer: ConstraintLayout
    lateinit var seekbar: SeekBar
    lateinit var backgroundLayout : ConstraintLayout
    lateinit var shohtButtin : ImageButton
    lateinit var mask1 : ImageButton
    lateinit var mask2 : ImageButton
    lateinit var maskHidden : ImageButton
    lateinit var mask3 : ImageButton
    lateinit var mask4 : ImageButton

    var currentIndex = 9
    var currentMask = R.drawable.transparent_retro_borders

    var dialogStep = 1
    var currentSessionNotificationActive = true
    var isAppStarted = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isAppStarted = true
        println("isAppStarted = true create")
        ViewModelProvider(this)[MainViewModel::class.java]
        ctx = this
        val splashIntent = Intent(this@MainActivity, SplashActivity::class.java)
        startActivity(splashIntent)
        setContentView(R.layout.activity_main)
        mImageView = findViewById(R.id.mImageView)
        imageContainer = findViewById(R.id.imageContainer)
        seekbar = findViewById(R.id.seekBar)
        backgroundLayout = findViewById(R.id.backgroundLayout)
        shohtButtin = findViewById(R.id.shotButton)
        mask1 = findViewById(R.id.mask1)
        mask2 = findViewById(R.id.mask2)
        maskHidden = findViewById(R.id.maskHidden)
        mask3 = findViewById(R.id.mask3)
        mask4 = findViewById(R.id.mask4)
        val cp = findViewById<RecyclerView>(R.id.carousel)
        val mascArray : List<Int>
        val mascPreviewArray: ArrayList<Int>
        if (userViewModel.isADActive) {
            mascArray = listOf(
                R.drawable.transparent_retro_borders,
                R.drawable.mask_newlyweds,
                R.drawable.transparent_retro_borders,
                R.drawable.mask_squares,
                R.drawable.transparent_retro_borders,
                R.drawable.mask_flower_rhombus,
                R.drawable.transparent_retro_borders,
                R.drawable.mask_pixel_hearts,
                R.drawable.mask_hearts,
                R.drawable.transparent_retro_borders,
                R.drawable.mask_christmas_tree,
                R.drawable.mask_gold_frame,
                R.drawable.transparent_retro_borders,
                R.drawable.mask_yellow_neon,
                R.drawable.mask_red_neon,
                R.drawable.transparent_retro_borders,
                R.drawable.mask_blue_neon,
                R.drawable.transparent_retro_borders,
                R.drawable.mask_film_rec
            )
            mascPreviewArray = arrayListOf(
                R.drawable.preview_flower_circle_lock,
                R.drawable.preview_name_newlyweds,
                R.drawable.preview_folk_style_locked,
                R.drawable.preview_squares,
                R.drawable.preview_plaster_locked,
                R.drawable.preview_flower_rhombus,
                R.drawable.preview_animals_heart_locked,
                R.drawable.preview_pixel_hearts,
                R.drawable.preview_hearts,
                R.drawable.preview_none,
                R.drawable.preview_christmas_tree,
                R.drawable.preview_gold_frame,
                R.drawable.preview_exhibition_locked,
                R.drawable.preview_yellow_neon,
                R.drawable.preview_red_neon,
                R.drawable.preview_blue_locked,
                R.drawable.preview_blue_neon,
                R.drawable.preview_rowan_locked,
                R.drawable.preview_film_rec
            )
        }else{
            mascArray = listOf(
                R.drawable.mask_flower_circle,
                R.drawable.mask_newlyweds,
                R.drawable.mask_folk_style,
                R.drawable.mask_squares,
                R.drawable.mask_plaster,
                R.drawable.mask_flower_rhombus,
                R.drawable.mask_animals_heart,
                R.drawable.mask_pixel_hearts,
                R.drawable.mask_hearts,
                R.drawable.transparent_retro_borders,
                R.drawable.mask_christmas_tree,
                R.drawable.mask_gold_frame,
                R.drawable.mask_exhibition,
                R.drawable.mask_yellow_neon,
                R.drawable.mask_red_neon,
                R.drawable.mask_blue,
                R.drawable.mask_blue_neon,
                R.drawable.mask_rowan,
                R.drawable.mask_film_rec
            )
            mascPreviewArray = arrayListOf(
                R.drawable.preview_flower_circle,
                R.drawable.preview_name_newlyweds,
                R.drawable.preview_folc_style,
                R.drawable.preview_squares,
                R.drawable.preview_plaster,
                R.drawable.preview_flower_rhombus,
                R.drawable.preview_animals_heart,
                R.drawable.preview_pixel_hearts,
                R.drawable.preview_hearts,
                R.drawable.preview_none,
                R.drawable.preview_christmas_tree,
                R.drawable.preview_gold_frame,
                R.drawable.preview_exhibition,
                R.drawable.preview_yellow_neon,
                R.drawable.preview_red_neon,
                R.drawable.preview_blue,
                R.drawable.preview_blue_neon,
                R.drawable.preview_rowan,
                R.drawable.preview_film_rec
            )
        }
        val madapter = MascsAdapter(this, mascPreviewArray)
        cp.adapter = madapter
        cp.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        cp.scrollToPosition(Int.MAX_VALUE/2-3)
        val snapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(cp)
        cp.setOnScrollChangeListener(View.OnScrollChangeListener{v, i, j, k, n->
            val selectedView = snapHelper.findSnapView(cp.layoutManager)
            currentIndex = selectedView?.let {
                (cp.getChildAdapterPosition(it)) % mascPreviewArray.size
            }?: 9
            val omaskView = findViewById<ImageView>(R.id.overmaskView)
            omaskView.setImageResource(mascArray[currentIndex])
            currentMask = mascArray[currentIndex]
        })
       madapter.onClick = {
           if(it%mascArray.size>currentIndex) cp.smoothScrollToPosition(it+2)
           else cp.smoothScrollToPosition(it-2)
       }

        //adTimer
        if (!timerAdBanner.isStarted&&userViewModel.isFeedbackActive){
            timerAdBanner.startTimer((userViewModel.adBannerTimer*60000).toLong())
        }
        timerAdBanner.listener = {
            findViewById<AdView>(R.id.adViewMain).visibility = View.GONE
            findViewById<TextView>(R.id.textView12).visibility = View.VISIBLE
            findViewById<MaterialButton>(R.id.clear).visibility = View.VISIBLE
            findViewById<ImageButton>(R.id.closeAdDialog).visibility = View.VISIBLE
        }

        if (!timerRateRequest.isStarted&&userViewModel.isFeedbackActive)timerRateRequest.startTimer((userViewModel.rateRequestTimer*60000).toLong())
        timerRateRequest.listener = {

            var rate = 0
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
                    rate = 1
                    selectStar(star1, star2, star3, star4, star5, buttonRate, 1)
                }
                star2?.setOnClickListener{
                    rate = 2
                    selectStar(star1, star2, star3, star4, star5, buttonRate, 2)
                }
                star3?.setOnClickListener{
                    rate = 3
                    selectStar(star1, star2, star3, star4, star5, buttonRate, 3)
                }
                star4?.setOnClickListener{
                    rate = 4
                    selectStar(star1, star2, star3, star4, star5, buttonRate, 4)
                }
                star5?.setOnClickListener{
                    rate = 5
                    selectStar(star1, star2, star3, star4, star5, buttonRate, 5)
                }

                buttonRate?.setOnClickListener{
                    userViewModel.isFeedbackActive = false
                    timerRateRequest.stop()
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
                    userViewModel.isFeedbackActive = false
                    timerRateRequest.stop()
                    if (etText?.text?.length!! >3){
                        val intent = Intent(Intent.ACTION_SENDTO).apply {
                            data = Uri.parse("mailto:")
                            putExtra(Intent.EXTRA_EMAIL, arrayOf("smarteasyapps17@gmail.com"))
                            putExtra(Intent.EXTRA_SUBJECT, "mirrorAPP")
                            putExtra(Intent.EXTRA_TEXT, etText!!.text)
                        }
                        if (intent.resolveActivity(this.packageManager) != null) {
                            startActivity(Intent.createChooser(intent, "Send mail..."))
                            bottomSheetDialog.hide()
                        } else {
                            Toast.makeText(this, "No email app found", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
            bottomSheetDialog.show()
            if (!timerRateRequest.isStarted&&userViewModel.isFeedbackActive){
                timerRateRequest.startTimer((userViewModel.rateRequestTimer*60000).toLong())
            }
        }

        // Создаем GestureDetector
        val gestureListener = GestureListener()
        gestureListener.swipeRight = {
            onMackClick(mask1, 3)
        }
        gestureListener.swipeLeft = {
            onMackClick(mask4, 3)
        }
        gestureListener.click = {
            onMackClick(gestureListener.lastClickedView!!)
        }
        gestureDetector = GestureDetector(this, gestureListener)

        // Назначаем OnTouchListener для ConstraintLayout
        mask1.setOnTouchListener { _, event ->
            gestureListener.lastClickedView = mask1
            gestureDetector.onTouchEvent(event)
            true
        }
        mask2.setOnTouchListener { _, event ->
            gestureListener.lastClickedView = mask2
            gestureDetector.onTouchEvent(event)
            true
        }
        mask3.setOnTouchListener { _, event ->
            gestureListener.lastClickedView = mask3
            gestureDetector.onTouchEvent(event)
            true
        }
        mask4.setOnTouchListener { _, event ->
            gestureListener.lastClickedView = mask4
            gestureDetector.onTouchEvent(event)
            true
        }

        val seekBar = findViewById<SeekBar>(R.id.brightnessSeekBar)
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                adjustBrightness(seekBar)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}

            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })
        val backlightSeekBar = findViewById<SeekBar>(R.id.backlightSeekBar)
        backlightSeekBar.setOnSeekBarChangeListener(object  : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                adjustBacklight(seekBar)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}

            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })


        //vidrobuttonListener
        val folder = File(ctx.getExternalFilesDir( "" ).toString()+ "/videos")
        folder.mkdirs()
        if (folder.exists())
            findViewById<CameraVideoButton>(R.id.videoShotButton).actionListener = object : CameraVideoButton.ActionListener{
                override fun onStartRecord() {
                    hideShowInterface()
                    val filename = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        LocalTime.now().toString().replace(".","_").replace(":","_")
                    } else {
                        Random().nextInt()
                    }
                    if (/*isOpen && */cameraMode == "video") {
                        //startRecording()
                        if (myCameras?.size != null && myCameras?.size!! >= openedCamera) myCameras?.get(
                            openedCamera
                        )?.closeCamera()
                        isCamStarted = false
                        isInShot = false
                        stopBackgroundThread()
                        //val cam : Camera = Camera.open()
                        //vt?.startRecording()
                        vt = VIdeoTool(ctx, this@MainActivity)
                        val folder = File(ctx.getExternalFilesDir("").toString() + "/videos")
                        folder.mkdirs()
                        val f = File(ctx.getExternalFilesDir("").toString() + "/videos/${filename}.mp4")
                        f.createNewFile()
                        vt?.startCam(f.absolutePath)
                    }
                }

                override fun onEndRecord() {
                    if (/*isOpen &&*/cameraMode == "video") {
                        hideShowInterface(false)
                        vt?.destroy()
                        val outPath = vt?.stopCam()

                        vt = null
                        val intent = Intent(this@MainActivity, VideoActivity::class.java)
                        intent.putExtra("imgPath", outPath)
                        intent.putExtra("mode", "preview")
                        startActivity(intent)
                        myCameras?.get(openedCamera)?.openCamera()
                    }
                }

                override fun onDurationTooShortError() {
                    return
                }

                override fun onSingleTap() {
                    return
                }

                override fun onCancelled() {
                    return
                }

            }
    }

    override fun onResume(){
        super.onResume()
        if (!is360Mode)onMirrorClick(findViewById(R.id.buttonMirror))
        else on360Click(findViewById(R.id.button360))
        if (userViewModel.isFirstLaunch && isAppReady){
            userViewModel.isFirstLaunch = false
            val onboardingIntent = Intent(this@MainActivity, OnboardingActivity::class.java)
            startActivity(onboardingIntent)
        }else if(ContextCompat.checkSelfPermission(this, CAMERA)==PackageManager.PERMISSION_DENIED&&isAppReady){
            val caIntent = Intent(this@MainActivity, CameraAccessActivity::class.java)
            startActivity(caIntent)
        }else if(ContextCompat.checkSelfPermission(this, RECORD_AUDIO)==PackageManager.PERMISSION_DENIED&&isAppReady){
            val caIntent = Intent(this@MainActivity, CameraAccessActivity::class.java)
            startActivity(caIntent)
        }
        else {
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val notificationId = 257894
            val activeNotifications = notificationManager.activeNotifications
            val notificationAlreadyShown = activeNotifications.any { it.id == notificationId }
            if (isAppStarted) {
                // приложение только что запущено
            } else {
                currentSessionNotificationActive = true
                println("lplplpl"+notificationAlreadyShown+" "+userViewModel.isNotificationActive+" "+currentSessionNotificationActive)
                // приложение было восстановлено из background
                if (!notificationAlreadyShown && userViewModel.isNotificationActive && currentSessionNotificationActive) {
                    currentSessionNotificationActive = false
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
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) PendingIntent.getActivity(
                            this,
                            0,
                            intent,
                            PendingIntent.FLAG_MUTABLE
                        )
                        else PendingIntent.getActivity(
                            this,
                            0,
                            intent,
                            PendingIntent.FLAG_UPDATE_CURRENT
                        )
                    val intentClose = Intent(this, NotificationReceiver::class.java).apply {
                        action =
                            "mirror.hand.makeup.shaving.best.zoom.pocket.selfie.notification.ACTION_CLOSE"
                        putExtra("notification_id", notificationId)
                    }
                    val pendingIntentClose =
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) PendingIntent.getBroadcast(
                            this,
                            0,
                            intentClose,
                            PendingIntent.FLAG_MUTABLE
                        )
                        else PendingIntent.getActivity(
                            this,
                            0,
                            intentClose,
                            PendingIntent.FLAG_UPDATE_CURRENT
                        )

                    val remoteViews = RemoteViews(packageName, R.layout.notification)
                    remoteViews.setTextViewText(R.id.notText, "Нажмите, чтобы открыть")
                    remoteViews.setOnClickPendingIntent(R.id.root, pendingIntent)
                    remoteViews.setOnClickPendingIntent(R.id.imageButton222, pendingIntentClose)
                    val builder = NotificationCompat.Builder(this, "appMirrorChannel")
                        .setSmallIcon(R.drawable.app_icon)
                        .setContentTitle("BeauttyMirror")
                        .setCustomContentView(remoteViews)
                        .setStyle(NotificationCompat.DecoratedCustomViewStyle())
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(false) // флаг, который делает уведомление невозможным для закрытия свайпом
                        .setOngoing(true) // флаг, который делает уведомление невозможным для закрытия пользователем

                    notificationManager.notify(notificationId, builder.build())
                }
            }
            //overridePendingTransition(R.anim.diagonal,R.anim.alpha)
            MobileAds.initialize(
                this
            ) { }
            //синхронизируем с галереей
            if(userViewModel.lastImagePath!="err"&&File(userViewModel.lastImagePath).exists()) {
                findViewById<ImageView>(R.id.openGalery).setImageURI(Uri.fromFile(File(userViewModel.lastImagePath)))
            }
            val mAdView :AdView = findViewById(R.id.adViewMain)
            val adRequest = AdRequest.Builder().build()
            if(userViewModel.isADActive) mAdView.loadAd(adRequest)
            else mAdView.visibility = View.GONE
            isCameraReady = true
            if (!isCamStarted) {
                startCam()
            }
        }
        isAppReady=true
        startBackgroundThread()
    }

    override fun onPause() {
        super.onPause()
        try {
            if (myCameras?.size != null && myCameras?.size!! >= openedCamera) myCameras?.get(
                openedCamera
            )?.closeCamera()
        }catch (ex : java.lang.Exception){}
        isCamStarted=false
        isInShot = false
        stopBackgroundThread()
    }

    override fun onStop() {
        super.onStop()
        isAppStarted = false
        println("isAppStarted = false stop")
    }

    override fun onRestart() {
        super.onRestart()
        isAppStarted = true
        println("isAppStarted = true restart")
    }

    /*override fun onDestroy() {
        super.onDestroy()
        //myCameras?.get(openedCamera)?.closeCamera()
    }*/

    private fun selectStar(iv1: ImageView?, iv2: ImageView?,iv3: ImageView?,iv4: ImageView?,iv5: ImageView?, b: Button?, r: Int){
        if (r<=0) return
        else{
            iv1?.setImageDrawable(
                ContextCompat.getDrawable(
                    this@MainActivity,
                    R.drawable.star
                ))
            iv2?.setImageDrawable(
                ContextCompat.getDrawable(
                    this@MainActivity,
                    R.drawable.star
                ))
            iv3?.setImageDrawable(
                ContextCompat.getDrawable(
                    this@MainActivity,
                    R.drawable.star
                ))
            iv4?.setImageDrawable(
                ContextCompat.getDrawable(
                    this@MainActivity,
                    R.drawable.star
                ))
            iv5?.setImageDrawable(
                ContextCompat.getDrawable(
                    this@MainActivity,
                    R.drawable.star
                ))
        }
        b?.background = ContextCompat.getDrawable(this@MainActivity, R.drawable.rounded_white)
        if(r>3) b?.text = getString(R.string.rate_on_pm)
        else b?.text = getString(R.string.rate)
        if (r>0) {
            iv1?.setImageDrawable(
                ContextCompat.getDrawable(
                    this@MainActivity,
                    R.drawable.starfilles
                )
            )
        } else return
        if (r>1) {
            iv2?.setImageDrawable(
                ContextCompat.getDrawable(
                    this@MainActivity,
                    R.drawable.starfilles
                )
            )
        } else return
        if (r>2) {
            iv3?.setImageDrawable(
                ContextCompat.getDrawable(
                    this@MainActivity,
                    R.drawable.starfilles
                )
            )
        } else return
        if (r>3) {
            iv4?.setImageDrawable(
                ContextCompat.getDrawable(
                    this@MainActivity,
                    R.drawable.starfilles
                )
            )
        } else return
        if (r>4) {
            iv5?.setImageDrawable(
                ContextCompat.getDrawable(
                    this@MainActivity,
                    R.drawable.starfilles
                )
            )
        } else return
    }

    private fun startBackgroundThread() {
        mBackgroundThread = HandlerThread("CameraBackground")
        mBackgroundThread!!.start()
        mBackgroundHandler = Handler(mBackgroundThread!!.looper)
    }

    private fun stopBackgroundThread() {
        mBackgroundThread?.quitSafely()
        try {
            if (mBackgroundThread!=null)
                mBackgroundThread!!.join()
            mBackgroundThread = null
            mBackgroundHandler = null
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }

    private fun startCam(){
        mCameraManager = getSystemService(CAMERA_SERVICE) as CameraManager
        try {
            if(mCameraManager!=null) {
                // Получение списка камер с устройства
                myCameras = ArrayList<CameraService>()
                var i = 0
                for (cameraID in mCameraManager!!.cameraIdList) {
                    Log.i("cameraerr", "cameraID: $cameraID")
                    val id = cameraID.toInt()

                    // создаем обработчик для камеры
                    myCameras!!.add(CameraService(mCameraManager!!, cameraID))
                    i++
                }
                if (mImageView.isAvailable) {
                    myCameras!![openedCamera].openCamera()
                }
                mImageView.surfaceTextureListener = object : TextureView.SurfaceTextureListener {
                    override fun onSurfaceTextureAvailable(surface: SurfaceTexture, width: Int, height: Int) {
                        surfaceforVT = Surface(surface)
                        myCameras!![openedCamera].openCamera()
                        myCameras!![openedCamera].setFocusBySeekBar(findViewById(R.id.seekBar))
                    }
                    override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture, width: Int, height: Int) {}
                    override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {
                        return false
                    }

                    override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {}
                }
            }
        } catch (e: CameraAccessException) {
            Log.e("camerr", e.message!!)
            e.printStackTrace()
        }
    }

    fun onsurvayClickOk(v: View){
        if (dialogStep==1){
            dialogStep=2
            findViewById<TextView>(R.id.textView12).text = resources.getString(R.string.survey2)
            findViewById<Button>(R.id.clear).text = resources.getString(R.string.okey)
        }
        else if(dialogStep==2){
            userViewModel.isFeedbackActive = false
            timerAdBanner.stop()
            try {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=mirror.hand.makeup.shaving.best.zoom.pocket.selfie")))
            } catch (e: ActivityNotFoundException) {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=mirror.hand.makeup.shaving.best.zoom.pocket.selfie")))
            }
        }else{
            //feedback
            val bottomSheetDialog = SmoothBottomSheetDialog(this)
            bottomSheetDialog.setContentView(R.layout.bottom_sheet_dialog_review)

            val clMain = bottomSheetDialog.findViewById<ConstraintLayout>(R.id.constraintLayoutMain)
            val cllist1 = bottomSheetDialog.findViewById<ConstraintLayout>(R.id.qlist1)
            val text = bottomSheetDialog.findViewById<EditText>(R.id.BSDReditText)
            val send = bottomSheetDialog.findViewById<Button>(R.id.BSDRsend)

            cllist1?.visibility = View.GONE
            clMain?.visibility = View.VISIBLE

            send?.setOnClickListener{
                if (text?.text!!.length>2){
                    userViewModel.isFeedbackActive = false
                    timerAdBanner.stop()
                    val intent = Intent(Intent.ACTION_SENDTO).apply {
                        data = Uri.parse("mailto:")
                        putExtra(Intent.EXTRA_EMAIL, arrayOf("smarteasyapps17@gmail.com"))
                        putExtra(Intent.EXTRA_SUBJECT, "Обращение пользователя AppMirror")
                        putExtra(Intent.EXTRA_TEXT, "${text.text}")
                    }
                    if (intent.resolveActivity(this.packageManager) != null) {
                        this.startActivity(intent)
                        bottomSheetDialog.hide()
                    } else {
                        Toast.makeText(this, "No email app found", Toast.LENGTH_SHORT).show()
                    }
                }
                bottomSheetDialog.hide()
            }
            findViewById<AdView>(R.id.adViewMain).visibility = View.VISIBLE
            findViewById<TextView>(R.id.textView12).visibility = View.GONE
            findViewById<MaterialButton>(R.id.clear).visibility = View.GONE
            findViewById<ImageButton>(R.id.closeAdDialog).visibility = View.GONE
            bottomSheetDialog.show()
        }
    }

    fun onSurvayClickNo(v: View){
        if (dialogStep!=3){
            dialogStep=3
            findViewById<TextView>(R.id.textView12).text = resources.getString(R.string.survey3)
            findViewById<Button>(R.id.clear).text = resources.getString(R.string.okey)
        }else{
            findViewById<AdView>(R.id.adViewMain).visibility = View.VISIBLE
            findViewById<TextView>(R.id.textView12).visibility = View.GONE
            findViewById<MaterialButton>(R.id.clear).visibility = View.GONE
            findViewById<ImageButton>(R.id.closeAdDialog).visibility = View.GONE
            dialogStep = 1
            findViewById<TextView>(R.id.textView12).text = resources.getString(R.string.survey1)
            findViewById<Button>(R.id.clear).text = resources.getString(R.string.yes)
            userViewModel.isFeedbackActive = true
            timerAdBanner.startTimer((userViewModel.adBannerTimer*60000).toLong())
        }
    }

    fun onMackClick(v: View, offsetkoeff : Int = 1){
        if(isInShot) return
        val mascArray : List<Int>
        val mascPreviewArray: List<Int>
        if (userViewModel.isADActive) {
            mascArray = listOf(
                R.drawable.transparent_retro_borders,
                R.drawable.mask_newlyweds,
                R.drawable.transparent_retro_borders,
                R.drawable.mask_squares,
                R.drawable.transparent_retro_borders,
                R.drawable.mask_flower_rhombus,
                R.drawable.transparent_retro_borders,
                R.drawable.mask_pixel_hearts,
                R.drawable.mask_hearts,
                R.drawable.transparent_retro_borders,
                R.drawable.mask_christmas_tree,
                R.drawable.mask_gold_frame,
                R.drawable.transparent_retro_borders,
                R.drawable.mask_yellow_neon,
                R.drawable.mask_red_neon,
                R.drawable.transparent_retro_borders,
                R.drawable.mask_blue_neon,
                R.drawable.transparent_retro_borders,
                R.drawable.mask_film_rec
            )
            mascPreviewArray = listOf(
                R.drawable.preview_flower_circle_lock,
                R.drawable.preview_name_newlyweds,
                R.drawable.preview_folk_style_locked,
                R.drawable.preview_squares,
                R.drawable.preview_plaster_locked,
                R.drawable.preview_flower_rhombus,
                R.drawable.preview_animals_heart_locked,
                R.drawable.preview_pixel_hearts,
                R.drawable.preview_hearts,
                R.drawable.preview_none,
                R.drawable.preview_christmas_tree,
                R.drawable.preview_gold_frame,
                R.drawable.preview_exhibition_locked,
                R.drawable.preview_yellow_neon,
                R.drawable.preview_red_neon,
                R.drawable.preview_blue_locked,
                R.drawable.preview_blue_neon,
                R.drawable.preview_rowan_locked,
                R.drawable.preview_film_rec
            )
        }else{
            mascArray = listOf(
                R.drawable.mask_flower_circle,
                R.drawable.mask_newlyweds,
                R.drawable.mask_folk_style,
                R.drawable.mask_squares,
                R.drawable.mask_plaster,
                R.drawable.mask_flower_rhombus,
                R.drawable.mask_animals_heart,
                R.drawable.mask_pixel_hearts,
                R.drawable.mask_hearts,
                R.drawable.transparent_retro_borders,
                R.drawable.mask_christmas_tree,
                R.drawable.mask_gold_frame,
                R.drawable.mask_exhibition,
                R.drawable.mask_yellow_neon,
                R.drawable.mask_red_neon,
                R.drawable.mask_blue,
                R.drawable.mask_blue_neon,
                R.drawable.mask_rowan,
                R.drawable.mask_film_rec
            )
            mascPreviewArray = listOf(
                R.drawable.preview_flower_circle,
                R.drawable.preview_name_newlyweds,
                R.drawable.preview_folc_style,
                R.drawable.preview_squares,
                R.drawable.preview_plaster,
                R.drawable.preview_flower_rhombus,
                R.drawable.preview_animals_heart,
                R.drawable.preview_pixel_hearts,
                R.drawable.preview_hearts,
                R.drawable.preview_none,
                R.drawable.preview_christmas_tree,
                R.drawable.preview_gold_frame,
                R.drawable.preview_exhibition,
                R.drawable.preview_yellow_neon,
                R.drawable.preview_red_neon,
                R.drawable.preview_blue,
                R.drawable.preview_blue_neon,
                R.drawable.preview_rowan,
                R.drawable.preview_film_rec
            )
        }
        if(v.id==R.id.mask3||v.id==R.id.mask4) {
            val animation = AnimationUtils.loadAnimation(this, R.anim.slide_mask)
            animation.interpolator = LinearInterpolator()
            maskHidden.visibility = View.VISIBLE
            for (i in 0..offsetkoeff) {
                if (i==offsetkoeff) animation.interpolator = OvershootInterpolator()
                mask1.startAnimation(animation)
                mask2.startAnimation(animation)
                maskHidden.startAnimation(animation)
                mask3.startAnimation(animation)
                mask4.startAnimation(animation)
                currentIndex = (currentIndex + 1) % mascPreviewArray.size
                mask1.setImageResource(mascPreviewArray[getImdex(false, 2, mascPreviewArray.size)])
                mask2.setImageResource(mascPreviewArray[getImdex(false, 1, mascPreviewArray.size)])
                maskHidden.setImageResource(mascPreviewArray[currentIndex])
                mask3.setImageResource(
                    mascPreviewArray[getImdex(
                        step = 1,
                        size = mascPreviewArray.size
                    )]
                )
                mask4.setImageResource(
                    mascPreviewArray[getImdex(
                        step = 2,
                        size = mascPreviewArray.size
                    )]
                )
            }
            maskHidden.visibility = View.INVISIBLE
        }else{
            maskHidden.visibility=View.VISIBLE
            val animation = AnimationUtils.loadAnimation(this, R.anim.slide_mask_reverce)
            mask1.startAnimation(animation)
            mask2.startAnimation(animation)
            maskHidden.startAnimation(animation)
            mask3.startAnimation(animation)
            mask4.startAnimation(animation)
            currentIndex = getImdex(false, 1, mascPreviewArray.size)
            mask1.setImageResource(mascPreviewArray[getImdex(false, 2, mascPreviewArray.size)])
            mask2.setImageResource(mascPreviewArray[getImdex(false, 1, mascPreviewArray.size)])
            maskHidden.setImageResource(mascPreviewArray[currentIndex])
            mask3.setImageResource(mascPreviewArray[getImdex(step = 1, size = mascPreviewArray.size)])
            mask4.setImageResource(mascPreviewArray[getImdex(step = 2, size = mascPreviewArray.size)])
            maskHidden.visibility=View.INVISIBLE
        }
        val omaskView = findViewById<ImageView>(R.id.overmaskView)
        omaskView.setImageResource(mascArray[currentIndex])
        currentMask = mascArray[currentIndex]
    }
    fun getImdex(plus : Boolean = true, step: Int, size: Int): Int{
        var res = currentIndex
        if (plus){
            return (currentIndex+step)%size
        }else{
            res = currentIndex-step
            if (res<0) return size+res
            else return res%size
        }
    }

    fun onSettingsClick(V: View){
        if(isInShot) return
        val intent = Intent(this@MainActivity, SettingsActivity::class.java)
        startActivity(intent)
    }

    fun onMirrorClick(v: View){
        if(isInShot) return
        is360Mode =  false
        isMirrorSelected = true
        val ll = findViewById<LinearLayout>(R.id.moveContainer)
        val bMirror = findViewById<TextView>(R.id.buttonMirror)
        val b360 = findViewById<TextView>(R.id.button360)
        findViewById<ImageButton>(R.id.shotButton).setImageResource(R.drawable.snowflake)
        findViewById<ImageButton>(R.id.info360Button).visibility = View.GONE
        ll.startAnimation(TranslateAnimation(-100f, 0f,
            0f, 0f)
            .apply {
                duration = 400
                fillAfter = true
            })
        b360.setBackgroundColor(Color.TRANSPARENT)
        if (backlightEnables){
            bMirror.background = ContextCompat.getDrawable(this@MainActivity, R.drawable.rounded_blck)
            bMirror.setTextColor(Color.WHITE)
            b360.setTextColor(Color.BLACK)
        }
        else{
            bMirror.background = ContextCompat.getDrawable(this@MainActivity, R.drawable.rounded_white)
            bMirror.setTextColor(Color.BLACK)
            b360.setTextColor(Color.WHITE)
        }

        //hide videoShotButton
        val vsb : CameraVideoButton = findViewById(R.id.videoShotButton)
        vsb.visibility = View.INVISIBLE
        shohtButtin.visibility = View.VISIBLE

        cameraMode = "photo"
    }

    fun on360Click(v: View){
        if(isInShot) return
        is360Mode = true
        isMirrorSelected =  false
        val ll = findViewById<LinearLayout>(R.id.moveContainer)
        val bMirror = findViewById<TextView>(R.id.buttonMirror)
        val b360 = findViewById<TextView>(R.id.button360)
        findViewById<ImageButton>(R.id.shotButton).setImageResource(R.drawable.shoot_button_v)
        findViewById<ImageButton>(R.id.info360Button).visibility = View.VISIBLE
        ll.startAnimation(TranslateAnimation(0f, -100f,
            0f, 0f)
            .apply {
                duration = 400
                fillAfter = true
            })
        bMirror.setBackgroundColor(Color.TRANSPARENT)
        if(backlightEnables){
            b360.background = ContextCompat.getDrawable(this@MainActivity, R.drawable.rounded_blck)
            b360.setTextColor(Color.WHITE)
            bMirror.setTextColor(Color.BLACK)
        }
        else{
            b360.background = ContextCompat.getDrawable(this@MainActivity, R.drawable.rounded_white)
            b360.setTextColor(Color.BLACK)
            bMirror.setTextColor(Color.WHITE)
        }
        if (userViewModel.is360FirstLaunch){
            val intent = Intent(this@MainActivity, Info360Activity::class.java)
            userViewModel.is360FirstLaunch = false
            startActivity(intent)
        }

        //show videoShotButton
        val vsb : CameraVideoButton = findViewById(R.id.videoShotButton)
        vsb.enableVideoRecording(true)
        vsb.visibility = View.VISIBLE
        shohtButtin.visibility = View.INVISIBLE

        cameraMode = "video"
    }

    fun onFlipClick(v: View){
        if(isInShot) return
        val matrix = Matrix()
        if (!flipEnabled) {
            matrix.setScale(-1f, 1f, mImageView.width / 2f, mImageView.height / 2f)
            mImageView.setTransform(matrix)
            flipEnabled = true
        }else{
            matrix.setScale(1f, 1f, mImageView.width / 2f, mImageView.height / 2f)
            mImageView.setTransform(matrix)
            flipEnabled = false
        }
    }

    fun onFlashlightClick(V: View){
        if(isInShot) return
        mCameraManager?.let { myCameras?.get(openedCamera)?.turnOnFlashlight(it) }
    }

    fun onBackLightClick(v: View){
        if(isInShot) return
        val bMirror = findViewById<TextView>(R.id.buttonMirror)
        val b360 = findViewById<TextView>(R.id.button360)
        val bBacklight = findViewById<ImageButton>(R.id.buttonBacklight)
        val seekbarbacklight = findViewById<SeekBar>(R.id.backlightSeekBar)
        val seekbarbrightness = findViewById<SeekBar>(R.id.brightnessSeekBar)
        if (backlightEnables){
            backgroundLayout.setBackgroundColor(Color.BLACK)
            seekbarbacklight.visibility = View.GONE
            //seekbarbrightness.layoutParams = LinearLayout.LayoutParams(200, 100)
            bBacklight.setImageDrawable(ContextCompat.getDrawable(this@MainActivity, R.drawable.backlight))
            imageContainer.setPadding(0)
            if (isMirrorSelected){
                bMirror.background = ContextCompat.getDrawable(this@MainActivity, R.drawable.rounded_white)
                bMirror.setTextColor(Color.BLACK)
                b360.setTextColor(Color.WHITE)
            }
            else{
                b360.background = ContextCompat.getDrawable(this@MainActivity, R.drawable.rounded_white)
                b360.setTextColor(Color.BLACK)
                bMirror.setTextColor(Color.WHITE)
            }
            backlightEnables = false
        }
        else {
            backgroundLayout.setBackgroundColor(Color.WHITE)
            seekbarbacklight.visibility = View.VISIBLE
            //seekbarbrightness.layoutParams = LinearLayout.LayoutParams(170, 170)
            bBacklight.setImageDrawable(ContextCompat.getDrawable(this@MainActivity, R.drawable.backlight_off))
            imageContainer.setPadding(100)
            if (isMirrorSelected){
                bMirror.background = ContextCompat.getDrawable(this@MainActivity, R.drawable.rounded_blck)
                bMirror.setTextColor(Color.WHITE)
                b360.setTextColor(Color.BLACK)
            }
            else{
                b360.background = ContextCompat.getDrawable(this@MainActivity, R.drawable.rounded_blck)
                b360.setTextColor(Color.WHITE)
                bMirror.setTextColor(Color.BLACK)
            }
            backlightEnables = true
        }
    }

    fun onInfo360Click(v: View){
        if(isInShot) return
        val intent = Intent(this@MainActivity, Info360Activity::class.java)
        startActivity(intent)
    }

    fun onOpengalleryClick(v: View){
        if(isInShot) return
        val intent = Intent(this@MainActivity, mirror.hand.makeup.shaving.best.zoom.pocket.selfie.GalleryActivity::class.java)
        startActivity(intent)
    }

    fun noAdsClick(v: View){
        if(isInShot) return
        val intent = Intent(this@MainActivity, PayActivity::class.java)
        startActivity(intent)
    }

    fun onCamShot(v: View){
        if (cameraMode == "photo") {
            if (!isInShot) {
                myCameras?.get(openedCamera)?.makePhoto()
                isInShot = true
            }
        }
    }

    private fun adjustBrightness(seekBar: SeekBar) {
        val brightness = seekBar.progress / 100.0f
        val layout = window.attributes
        layout.screenBrightness = brightness
        window.attributes = layout
    }

    private fun adjustBacklight(seekBar: SeekBar) {
        val light = seekBar.progress+100
        imageContainer.setPadding(light)
    }

    private fun hideShowInterface(hide: Boolean = true){
        if (hide) {
            findViewById<TextView>(R.id.videobottext).visibility = View.VISIBLE
            findViewById<ConstraintLayout>(R.id.constraintLayout).visibility = View.INVISIBLE
            findViewById<ImageButton>(R.id.imageButton).visibility = View.INVISIBLE
            findViewById<ImageButton>(R.id.imageView7).visibility = View.INVISIBLE
            findViewById<ImageButton>(R.id.button2).visibility = View.INVISIBLE
            findViewById<ImageButton>(R.id.buttonFlashlight).visibility = View.INVISIBLE
            findViewById<ImageButton>(R.id.buttonBacklight).visibility = View.INVISIBLE
            findViewById<SeekBar>(R.id.seekBar).visibility = View.INVISIBLE
            findViewById<SeekBar>(R.id.seekBar3).visibility = View.INVISIBLE
            findViewById<LinearLayout>(R.id.linearLayout3).visibility = View.INVISIBLE
        }
        else{
            findViewById<TextView>(R.id.videobottext).visibility = View.GONE
            findViewById<ConstraintLayout>(R.id.constraintLayout).visibility = View.VISIBLE
            findViewById<ImageButton>(R.id.imageButton).visibility = View.VISIBLE
            findViewById<ImageButton>(R.id.imageView7).visibility = View.VISIBLE
            findViewById<ImageButton>(R.id.button2).visibility = View.VISIBLE
            findViewById<ImageButton>(R.id.buttonFlashlight).visibility = View.VISIBLE
            findViewById<ImageButton>(R.id.buttonBacklight).visibility = View.VISIBLE
            findViewById<SeekBar>(R.id.seekBar).visibility = View.VISIBLE
            findViewById<SeekBar>(R.id.seekBar3).visibility = View.VISIBLE
            findViewById<LinearLayout>(R.id.linearLayout3).visibility = View.VISIBLE
        }
    }

    inner class CameraService(cameraManager: CameraManager, cameraID: String) {
        private val mCameraID: String
        private var mCameraDevice: CameraDevice? = null
        private var mCaptureSession: CameraCaptureSession? = null
        private var characteristics: CameraCharacteristics? = null
        private var mImageReader: ImageReader? = null

        //video
        private var videoFile: File? = null
        private val videoSize: Size = Size(1080, 1920)
        private var mediaRecorder: MediaRecorder? = null
        private var isRecording = false
        private var isPrepared = false
        private var recorderSurface : Surface? = null
        var texture: SurfaceTexture? = null

        private val mFile: File = File(ctx.getExternalFilesDir(null), "mirrorImages/1.jpg")
        val isOpen: Boolean
            get() = mCameraDevice != null
        var videoStarted = false

        fun openCamera() {
            if(isOpen){
                println("ERROR camera opened")
                return
            }
            try {
                if (checkSelfPermission(CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    println("cManOpen")
                    mCameraManager?.openCamera(mCameraID, mCameraCallback, null)
                    characteristics = mCameraManager?.getCameraCharacteristics(mCameraID)
                    val focusRange = characteristics?.get(CameraCharacteristics.LENS_INFO_MINIMUM_FOCUS_DISTANCE) to
                            characteristics?.get(CameraCharacteristics.LENS_INFO_HYPERFOCAL_DISTANCE)
                    println("staaaaaaaaaaaaaa"+focusRange)
                }
            } catch (e: Exception) {
                Log.i("opencamerr", e.localizedMessage)
            }
        }

        fun closeCamera() {
            if (mCameraDevice != null) {
                mCameraDevice!!.close()
                mCameraDevice = null
            }
        }

        init {
            mCameraManager = cameraManager
            mCameraID = cameraID
            val folder = File(ctx.getExternalFilesDir( "" ).toString()+ "/videos")
            folder.mkdirs()
        }

        fun turnOnFlashlight(cameraManager: CameraManager) {
            try {
                val support = characteristics?.get(CameraCharacteristics.FLASH_INFO_AVAILABLE)
                if (support == true) {
                    println("fdfdfdfdf")
                    if (!flashlightEnabled) {
                        cameraManager.setTorchMode(mCameraID, true)
                        flashlightEnabled = true
                    }else {
                        cameraManager.setTorchMode(mCameraID, false)
                        flashlightEnabled = false
                    }
                } else{
                    val cameraId =  "0"
                    val characteristics2 = cameraManager.getCameraCharacteristics(cameraId)
                    val support2 = characteristics2.get(CameraCharacteristics.FLASH_INFO_AVAILABLE)
                    if (support2 == true){
                        if (!flashlightEnabled) {
                            cameraManager.setTorchMode(cameraId, true)
                            flashlightEnabled = true
                        }else {
                            cameraManager.setTorchMode(cameraId, false)
                            flashlightEnabled = false
                        }
                    }else{
                        Toast.makeText(ctx, "Flashlight not available on this device", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }


        fun setFocusBySeekBar(seekBar: SeekBar) {
            seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    if (fromUser) {
                        try {
                            // Get the minimum and maximum focus distances supported by the camera
                            val focusRange = characteristics?.get(CameraCharacteristics.LENS_INFO_MINIMUM_FOCUS_DISTANCE) to
                                    characteristics?.get(CameraCharacteristics.LENS_INFO_HYPERFOCAL_DISTANCE)

                            if(focusRange.first==null||focusRange.second==null) return
                            // Calculate the desired focus distance based on the SeekBar progress
                            val desiredFocusDistance = (focusRange.second!! - focusRange.first!!) * progress / 100f + focusRange.first!!

                            val texture: SurfaceTexture? = mImageView.surfaceTexture
                            // texture.setDefaultBufferSize(1920,1080);
                            val surface = Surface(texture)

                            // Create a new capture request with the desired focus distance
                            val captureRequestBuilder = mCameraDevice?.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
                            captureRequestBuilder?.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_OFF)
                            captureRequestBuilder?.set(CaptureRequest.LENS_FOCUS_DISTANCE, desiredFocusDistance)
                            captureRequestBuilder?.set(CaptureRequest.JPEG_ORIENTATION, getOrientation())
                            captureRequestBuilder?.addTarget(surface)
                            captureRequestBuilder?.build()

                            // Update the focus distance in the current capture session
                            if (captureRequestBuilder != null && mCaptureSession!=null) {
                                mCaptureSession?.setRepeatingRequest(captureRequestBuilder.build(), null, null)
                            }
                        } catch (e: CameraAccessException) {
                            Log.e("focusERR", "Failed to change focus", e)
                        }
                    }
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {}
                override fun onStopTrackingTouch(seekBar: SeekBar?) {}
            })
        }



        private fun createCameraPreviewSession() {
            texture = mImageView.surfaceTexture
            //previewSize settings
            val map = characteristics!!.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)
            val format = ImageFormat.JPEG
            val sizes = map!!.getOutputSizes(format)
            val viewWidth: Int = mImageView.width
            val viewHeight: Int = mImageView.height
            val aspectRatio: Float = sizes[1].width / sizes[1].height.toFloat()
            mImageView.setLayoutParams(FrameLayout.LayoutParams(viewWidth*aspectRatio.toInt(), viewHeight*aspectRatio.toInt()))
            texture?.setDefaultBufferSize(viewHeight*aspectRatio.toInt(),viewWidth*aspectRatio.toInt())
            mImageReader = ImageReader.newInstance(viewHeight*aspectRatio.toInt(),viewWidth*aspectRatio.toInt(), ImageFormat.JPEG,1)
            mImageReader!!.setOnImageAvailableListener(mOnImageAvailableListener, null)
            val surface = Surface(texture)
            try {

                val builder = mCameraDevice!!.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
                //zoom
                findViewById<SeekBar>(R.id.seekBar3).setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
                            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                                val sensorRect =
                                    characteristics!!.get(CameraCharacteristics.SENSOR_INFO_ACTIVE_ARRAY_SIZE)

                                val maxZoom =
                                    characteristics!!.get(CameraCharacteristics.SCALER_AVAILABLE_MAX_DIGITAL_ZOOM)!!

                                if (progress in 0..25)
                                    seekBar?.thumb = ContextCompat.getDrawable(this@MainActivity, R.drawable.zoom)
                                else if (progress in 26..50)
                                    seekBar?.thumb = ContextCompat.getDrawable(this@MainActivity, R.drawable.zoomx)
                                else if (progress in 51..75)
                                    seekBar?.thumb = ContextCompat.getDrawable(this@MainActivity, R.drawable.zoomxx)
                                else if (progress in 76..100)
                                    seekBar?.thumb = ContextCompat.getDrawable(this@MainActivity, R.drawable.zoomxxx)

                                if (maxZoom == 0f) {
                                    return
                                }

                                val zoomRatio = (100f-progress.toFloat()) / 100

                                val cropWidth = sensorRect!!.width() - Math.round(
                                    sensorRect!!.width().toFloat() * zoomRatio
                                )
                                val cropHeight = sensorRect!!.height() - Math.round(
                                    sensorRect!!.height()
                                        .toFloat() * zoomRatio
                                )

                                val zoomRect = Rect(
                                    cropWidth / 2,
                                    cropHeight / 2,
                                    sensorRect!!.width() - cropWidth / 2,
                                    sensorRect!!.height() - cropHeight / 2
                                )
                                builder.set(
                                    CaptureRequest.SCALER_CROP_REGION,
                                    zoomRect
                                )

                                try {
                                    if (mCaptureSession != null) {
                                        updatePreview(builder)
                                    }
                                } catch (e: CameraAccessException) {
                                    e.printStackTrace()
                                }
                            }
                            override fun onStartTrackingTouch(seekBar: SeekBar?) {    }
                            override fun onStopTrackingTouch(seekBar: SeekBar?) {     }
                        })
                        //zoom
                val surfaces = ArrayList<Surface>()
                surfaces.add(surface)
                builder.set(CaptureRequest.JPEG_ORIENTATION, getOrientation())
                builder.addTarget(surface)
                if (mImageReader!=null){
                    surfaces.add(mImageReader!!.surface)
                    //builder.addTarget(mImageReader!!.surface)
                }
                            mCameraDevice!!.createCaptureSession(surfaces,
                            object : CameraCaptureSession.StateCallback() {
                                override fun onConfigured(session: CameraCaptureSession) {
                                    mCaptureSession = session
                                    try {
                                        mCaptureSession?.setRepeatingRequest(builder.build(), null, null)
                                        updatePreview(builder)
                                    } catch (e: CameraAccessException) {
                                        e.printStackTrace()
                                    }
                                }

                                override fun onConfigureFailed(session: CameraCaptureSession) {}
                            }, null
                        )
                    } catch (e: CameraAccessException) {
                        println("camPreviewERR"+e.localizedMessage)
                        e.printStackTrace()
                    }

        }
        private fun updatePreview(captureRequestBuilder: CaptureRequest.Builder) {
            if (mCameraDevice == null) return
            mCaptureSession?.setRepeatingRequest(captureRequestBuilder.build(), null, null)
        }

        fun makePhoto() {
            try {
                if (mCameraDevice==null) return
                val orientation = getOrientation()

                // This is the CaptureRequest.Builder that we use to take a picture.
                val captureBuilder =
                    mCameraDevice!!.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
                captureBuilder.set(CaptureRequest.JPEG_ORIENTATION, orientation)
                captureBuilder.addTarget(mImageReader!!.surface)
                val CaptureCallback: CaptureCallback = object : CaptureCallback() {
                    override fun onCaptureCompleted(
                        session: CameraCaptureSession,
                        request: CaptureRequest,
                        result: TotalCaptureResult
                    ) {
                    }
                }
                mCaptureSession!!.stopRepeating()
                //mCaptureSession!!.abortCaptures()
                mCaptureSession!!.capture(captureBuilder.build(), CaptureCallback, null)
            } catch (e: CameraAccessException) {
                e.printStackTrace()
            }
        }

        private fun getOrientation(): Int{
            //получаем необходимую ориентацию
            val display = (ctx.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay
            var rotation = display.rotation
            val isFrontCamera = characteristics?.get(CameraCharacteristics.LENS_FACING) == CameraCharacteristics.LENS_FACING_FRONT
            val sensorOrnt = characteristics?.get(CameraCharacteristics.SENSOR_ORIENTATION)
            if (sensorOrnt != null) {
                //userViewModel.addLog(Date().toString(), "sensorOrnt - $sensorOrnt | rotation - $rotation")
                if (abs(sensorOrnt/90-rotation)<=2) rotation++
            }else userViewModel.addLog(Date().toString(), "sensorOrnt null")

            var orientation = 0
            when (rotation) {
                Surface.ROTATION_0 -> orientation = if (isFrontCamera) 270 else 90
                Surface.ROTATION_90 -> orientation = 0
                Surface.ROTATION_180 -> orientation = if (isFrontCamera) 90 else 270
                Surface.ROTATION_270 -> orientation = 180
            }
            return orientation
        }

        private val mOnImageAvailableListener: OnImageAvailableListener =
            object : OnImageAvailableListener {
                override fun onImageAvailable(reader: ImageReader) {
                    run {
                        if (ContextCompat.checkSelfPermission(ctx, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                            // Do the file write
                        } else {
                            // Request permission from the user
                            ActivityCompat.requestPermissions(this@MainActivity,
                                arrayOf( Manifest.permission.WRITE_EXTERNAL_STORAGE), 0)
                        }
                        val backBitmap = if(currentMask==R.drawable.transparent_retro_borders) {null} else{BitmapFactory.decodeResource(resources ,currentMask)}
                        mBackgroundHandler?.post(ImageSaver(reader.acquireNextImage(), mFile, backBitmap, flipEnabled, ctx, userViewModel))
                    }
                }
            }

        private val mCameraCallback: CameraDevice.StateCallback =
            object : CameraDevice.StateCallback() {
                override fun onOpened(camera: CameraDevice) {
                    println("ooooooopened")
                    isCamStarted = true
                    mCameraDevice = camera
                    Log.i("camerr", "Open camera  with id:" + mCameraDevice!!.id)
                    if (ContextCompat.checkSelfPermission(ctx, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        // Do the file write
                    } else {
                        // Request permission from the user
                        ActivityCompat.requestPermissions(this@MainActivity,
                            arrayOf( Manifest.permission.WRITE_EXTERNAL_STORAGE), 0)
                    }
                    if (videoFile?.exists() == true) {
                        println("fdfddgdgdg"+videoFile?.absolutePath)
                    }
                    createCameraPreviewSession()
                }

                override fun onDisconnected(camera: CameraDevice) {
                    mCameraDevice!!.close()
                    Log.i("camerr", "disconnect camera  with id:" + mCameraDevice!!.id)
                    mCameraDevice = null
                }

                override fun onError(camera: CameraDevice, error: Int) {
                    Log.i("camerr", "error! camera id:" + camera.id + " error:" + error)
                }
            }
    }
}
private class ImageSaver internal constructor(image: Image, file: File, mask: Bitmap?, isFlipped: Boolean, ctx: Context, viewModel: MainViewModel) : Runnable {
    private var mFile: File = file
    private val mask = mask
    private val viewModel = viewModel
    private val isFlipped = !isFlipped
    private val mImage: Image = image
    private val ctx: Context = ctx
    override fun run() {
        /*val buffer: ByteBuffer = mImage.planes[0].buffer
        val bytes = ByteArray(buffer.remaining())
        buffer.get(bytes)*/
        val bmpOrnt = processImage(mImage) ?: return
        var output: FileOutputStream? = null
        try {
            val folder = File(ctx.getExternalFilesDir(null), "mirrorImages")
            if(!folder.exists()) folder.mkdir()
            var newName = "${Date()}.jpg"
            mFile = File(ctx.getExternalFilesDir(null), "mirrorImages/$newName")
            output = FileOutputStream(mFile)
            var rbytes : ByteArray? = null
            if(mask!=null) {
                //val img = combineImg(bytes, mask, isFlipped )
                val img = combineImg(bmpOrnt, mask, isFlipped )
                val stream = ByteArrayOutputStream()
                img?.compress(Bitmap.CompressFormat.JPEG, 90, stream)
                rbytes = stream.toByteArray()
            }else{
                if (isFlipped){
                    val bitmap = bmpOrnt//BitmapFactory.decodeByteArray(bytes, 0, bytes.size)

                    val matrix = Matrix()
                    matrix.setScale(-1f, 1f) // Отзеркаливание по горизонтали

                    //matrix.postTranslate(bitmap.width.toFloat(), 0f) // Сдвиг на ширину изображения

                    val flippedBitmap =
                        Bitmap.createBitmap(bitmap!!, 0, 0, bitmap.width, bitmap.height, matrix, true)
                    val stream = ByteArrayOutputStream()
                    flippedBitmap.compress(Bitmap.CompressFormat.JPEG, 90, stream)
                    rbytes = stream.toByteArray()
                }else {
                    val stream = ByteArrayOutputStream()
                    bmpOrnt!!.compress(Bitmap.CompressFormat.JPEG, 90, stream)
                    rbytes = stream.toByteArray()
                    //rbytes = bytes
                }
            }
            output.write(rbytes)
        } catch (e: IOException) {
            println("imgsaveErr"+e.localizedMessage)
        } finally {
            if (null != output) {
                try {
                    val intent = Intent(ctx, mirror.hand.makeup.shaving.best.zoom.pocket.selfie.FullscreenActivity::class.java)
                    intent.putExtra("imgPath", mFile.absolutePath)
                    intent.putExtra("mode", "preview")
                    if (mFile.exists()) viewModel.lastImagePath = mFile.absolutePath
                    ctx.startActivity(intent)
                    mImage.close()
                    output.close()
                } catch (e: IOException) {
                    println("saveERR"+e.localizedMessage)
                }
            }
        }
    }

    private fun processImage(image: Image) :Bitmap? {
        val buffer = image.planes[0].buffer
        val bytes = ByteArray(buffer.remaining())
        buffer.get(bytes)

        // Get the image's orientation from the ExifInterface.
        val exif = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            ExifInterface(bytes.inputStream())
        } else {
            return null
        }
        val orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED)

        // Rotate the image according to its orientation.
        val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        val matrix = Matrix()
        when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
            ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
            ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
        }
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    fun combineImg(back : Bitmap, front: Bitmap, isFlipped: Boolean) : Bitmap?{
        var myBitmap = back//BitmapFactory.decodeByteArray(back, 0, back.size, null)
        if (isFlipped){
            val matrix = Matrix()
            matrix.setScale(-1f, 1f) // Отзеркаливание по горизонтали

            matrix.postTranslate(myBitmap.width.toFloat(), 0f) // Сдвиг на ширину изображения

            myBitmap =
                Bitmap.createBitmap(myBitmap, 0, 0, myBitmap.getWidth(), myBitmap.getHeight(), matrix, true)
        }
        val combinedBitmap = Bitmap.createBitmap(myBitmap.width, myBitmap.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(combinedBitmap)
        canvas.drawBitmap(myBitmap, Rect(0,0, myBitmap.width, myBitmap.height), Rect(0, 0, myBitmap.width, myBitmap.height), null)
        canvas.drawBitmap(front, Rect(0,0, front.width, front.height), Rect(0, 0, myBitmap.width, myBitmap.height), null)
        return combinedBitmap
    }
}
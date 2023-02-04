package com.kwork.mirrorapp

import android.Manifest.permission.CAMERA
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.SurfaceTexture
import android.hardware.camera2.*
import android.os.Bundle
import android.util.Log
import android.view.Surface
import android.view.SurfaceView
import android.view.TextureView
import android.widget.ImageView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.kwork.mirrorapp.VM.MainViewModel
import java.util.*


class MainActivity : AppCompatActivity() {
    public lateinit var ctx : Context
    private val userViewModel by viewModels<MainViewModel>()
    var isAppReady = false
    var isCameraReady = false

    var myCameras: ArrayList<CameraService>? = null
    var openedCamera = 1
    var mCameraManager: CameraManager? = null

    lateinit var mImageView: TextureView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ctx = this
        val splashIntent : Intent = Intent(this@MainActivity, SplashActivity::class.java)
        startActivity(splashIntent)
        setContentView(R.layout.activity_main)
        mImageView = findViewById(R.id.mImageView)
    }

    override fun onResume(){
        super.onResume()
        if (userViewModel.isFirstLaunch && isAppReady){
            userViewModel.isFirstLaunch = false
            val onboardingIntent = Intent(this@MainActivity, OnboardingActivity::class.java)
            startActivity(onboardingIntent)
        }else if(ContextCompat.checkSelfPermission(this, CAMERA)==PackageManager.PERMISSION_DENIED&&isAppReady){
            val caIntent = Intent(this@MainActivity, CameraAccessActivity::class.java)
            startActivity(caIntent)
        }
        else {
            isCameraReady = true
            startCam()
        }
        isAppReady=true
    }

    override fun onPause() {
        super.onPause()
        myCameras?.get(openedCamera)?.closeCamera()
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
                mImageView.surfaceTextureListener = object : TextureView.SurfaceTextureListener {
                    override fun onSurfaceTextureAvailable(surface: SurfaceTexture, width: Int, height: Int) {
                        myCameras!![openedCamera].openCamera()
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


    inner class CameraService(cameraManager: CameraManager, cameraID: String) {
        private val mCameraID: String
        private var mCameraDevice: CameraDevice? = null
        private var mCaptureSession: CameraCaptureSession? = null
        val isOpen: Boolean
            get() = mCameraDevice != null

        fun openCamera() {
            if(isOpen){
                println("ERROR camera opened")
                return
            }
            try {
                println("open camera"+mCameraID)
                if (checkSelfPermission(CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    mCameraManager?.openCamera(mCameraID, mCameraCallback, null)
                }
            } catch (e: CameraAccessException) {
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
        }

        private fun createCameraPreviewSession() {

                    val texture: SurfaceTexture? = mImageView.surfaceTexture
                    // texture.setDefaultBufferSize(1920,1080);
                    val surface = Surface(texture)
                    try {
                        val builder = mCameraDevice!!.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
                        builder.addTarget(surface)
                        mCameraDevice!!.createCaptureSession(Arrays.asList(surface),
                            object : CameraCaptureSession.StateCallback() {
                                override fun onConfigured(session: CameraCaptureSession) {
                                    mCaptureSession = session
                                    try {
                                        mCaptureSession!!.setRepeatingRequest(builder.build(), null, null)
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

        private val mCameraCallback: CameraDevice.StateCallback =
            object : CameraDevice.StateCallback() {
                override fun onOpened(camera: CameraDevice) {
                    mCameraDevice = camera
                    Log.i("camerr", "Open camera  with id:" + mCameraDevice!!.id)
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
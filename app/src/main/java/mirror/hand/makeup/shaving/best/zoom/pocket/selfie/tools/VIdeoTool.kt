package mirror.hand.makeup.shaving.best.zoom.pocket.selfie.tools

import android.app.Activity
import android.content.Context
import android.hardware.Camera
import android.opengl.GLSurfaceView
import android.util.Size
import android.view.*
import android.widget.FrameLayout
import com.daasuu.camerarecorder.CameraRecorder
import com.daasuu.camerarecorder.CameraRecorderBuilder
import com.daasuu.camerarecorder.LensFacing
import mirror.hand.makeup.shaving.best.zoom.pocket.selfie.R
import java.io.File


class VIdeoTool(context: Context, act : Activity) {

    var sampleGLView: GLSurfaceView? = null
    var cameraRecorder: CameraRecorder? = null
    var activity: Activity ? = null
    var recOutputPath: String? = null

    init {
        activity = act
        sampleGLView = GLSurfaceView(context)
        val frameLayout: FrameLayout = act.findViewById(R.id.surfaceView)
        frameLayout.visibility = View.VISIBLE
        frameLayout.addView(sampleGLView)

        cameraRecorder = CameraRecorderBuilder(act, sampleGLView)
            .lensFacing(LensFacing.FRONT)
            .build()
    }

    fun destroy(){
        sampleGLView?.onPause();

        cameraRecorder?.stop();
        cameraRecorder?.release();
        cameraRecorder = null;

        ( activity?.findViewById<FrameLayout>(R.id.surfaceView))?.apply {
            removeView(sampleGLView)
            visibility = View.GONE
        }

        sampleGLView = null
    }

    fun startCam(path: String){
        recOutputPath = path
        cameraRecorder?.start(path)
    }

    fun stopCam():String?{
        cameraRecorder?.stop()
        return recOutputPath
    }

}
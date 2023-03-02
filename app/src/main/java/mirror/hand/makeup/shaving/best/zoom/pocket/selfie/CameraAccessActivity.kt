package mirror.hand.makeup.shaving.best.zoom.pocket.selfie

import android.Manifest.permission.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class CameraAccessActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(mirror.hand.makeup.shaving.best.zoom.pocket.selfie.R.layout.activity_camera_access)
    }

    fun getAccess(v: View){
        requestPermissions(arrayOf(CAMERA, WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE, RECORD_AUDIO), 100)
        finish()
    }
}
package mirror.hand.makeup.shaving.best.zoom.pocket.selfie

import android.Manifest.permission.*
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity


class CameraAccessActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(mirror.hand.makeup.shaving.best.zoom.pocket.selfie.R.layout.activity_camera_access)
    }

    fun getAccess(v: View){
        requestPermissions(arrayOf(CAMERA, ACCESS_NOTIFICATION_POLICY, WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE, RECORD_AUDIO), 100)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (permissions != null) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
        when (requestCode) {
            100 -> {

                // если пользователь закрыл запрос на разрешение, не дав ответа, массив grantResults будет пустым
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    finish()
                } else {
                    Toast.makeText(this@CameraAccessActivity, getString(R.string.permission_denied), Toast.LENGTH_LONG).show()
                }
                return
            }
        }
    }
}
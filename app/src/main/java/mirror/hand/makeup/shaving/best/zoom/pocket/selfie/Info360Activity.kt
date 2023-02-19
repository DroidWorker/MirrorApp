package mirror.hand.makeup.shaving.best.zoom.pocket.selfie

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class Info360Activity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info360)
    }

    fun onClearClick(v : View){
        finish()
    }
}
package mirror.hand.makeup.shaving.best.zoom.pocket.selfie.tools

import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View

class GestureListener : GestureDetector.SimpleOnGestureListener() {

    // Минимальное расстояние и скорость для считывания жеста
    private val SWIPE_THRESHOLD = 100
    private val SWIPE_VELOCITY_THRESHOLD = 100

    var swipeLeft : (()->Unit)? = null
    var swipeRight : (()->Unit)? = null
    var click : (()->Unit)? = null
    var lastClickedView: View? = null

    override fun onDown(e: MotionEvent?): Boolean {
        return true
    }

    override fun onSingleTapUp(e: MotionEvent?): Boolean {
        // Вызывается, когда пользователь быстро касается и отпускает экран
        click?.invoke()
        return super.onSingleTapUp(e)
    }

    override fun onFling(
        event1: MotionEvent?,
        event2: MotionEvent?,
        velocityX: Float,
        velocityY: Float
    ): Boolean {
        try {
            val diffY = event2?.y?.minus(event1?.y ?: 0f) ?: 0f
            val diffX = event2?.x?.minus(event1?.x ?: 0f) ?: 0f
            // Определяем направление свайпа
            if (Math.abs(diffX) > Math.abs(diffY)) {
                if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffX > 0) {
                        // Свайп вправо
                        swipeRight?.invoke()
                    } else {
                        // Свайп влево
                        swipeLeft?.invoke()
                    }
                }
            } else {
                if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffY > 0) {
                        // Свайп вниз

                    } else {
                        // Свайп вверх

                    }
                }
            }
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
        return true
    }
}
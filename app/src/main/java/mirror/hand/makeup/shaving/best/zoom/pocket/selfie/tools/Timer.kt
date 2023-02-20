package mirror.hand.makeup.shaving.best.zoom.pocket.selfie.tools

import android.os.CountDownTimer

class Timer {
    companion object{
        var timer : CountDownTimer? = null
        var isStarted = false
        var listener: (()->Unit)? = null
        fun startTimer(interval : Long){
            isStarted = true
            timer = object: CountDownTimer(interval, 1000) {
                override fun onTick(millisUntilFinished: Long) {

                }

                override fun onFinish() {
                    listener?.invoke()
                }
            }
            timer!!.start()
        }

    }
}
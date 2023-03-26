package mirror.hand.makeup.shaving.best.zoom.pocket.selfie.tools

import android.os.CountDownTimer

class Timer {
        var timer : CountDownTimer? = null
        var isStarted = false
        var listener: (()->Unit)? = null
        fun startTimer(interval : Long){
            isStarted = true
            timer = object: CountDownTimer(interval, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                }

                override fun onFinish() {
                    isStarted = false
                    listener?.invoke()
                }
            }
            timer!!.start()
        }
        fun stop(){
            timer?.cancel()
        }
}
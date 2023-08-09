package mirror.hand.makeup.shaving.best.zoom.pocket.selfie.tools

import android.os.CountDownTimer

class Timer {
        var timer : CountDownTimer? = null
        var isStarted = false
        var isPaused = false
        var isDev = false
        var remainingTime : Long = 0
        var listener: (()->Unit)? = null
        var devListener: ((Long)->Unit)? = null
        fun startTimer(interval : Long){
            isStarted = true
            timer = object: CountDownTimer(interval, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    remainingTime = millisUntilFinished
                    if(isDev){
                        println("eeeeeerrrrrr remaining "+millisUntilFinished)
                        devListener?.invoke(millisUntilFinished)
                    }
                }

                override fun onFinish() {
                    isStarted = false
                    listener?.invoke()
                }
            }
            timer!!.start()
        }

    fun pauseTimer() {
        if (isStarted && !isPaused) {
            isPaused = true
            timer?.cancel()
        }
    }

    fun resumeTimer() {
        if (isStarted && isPaused) {
            isPaused = false
            startTimer(remainingTime)
        }
    }
        fun stop(){
            timer?.cancel()
        }
}
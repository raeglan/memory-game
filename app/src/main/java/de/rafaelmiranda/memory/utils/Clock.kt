package de.rafaelmiranda.memory.utils

import android.util.Log

/**
 * This is tool for running timer clock with option of pause.
 * */
object Clock {

    private var mPauseTimer: PauseTimer? = null
    private var mInstance: Clock? = null


    val passedTime: Long
        get() = mPauseTimer!!.timePassed()

    init {
        Log.i("my_tag", "NEW INSTANCE OF CLOCK")
    }

    class PauseTimer(millisOnTimer: Long, countDownInterval: Long, runAtStart: Boolean, onTimerCount: OnTimerCount) : CountDownClock(millisOnTimer, countDownInterval, runAtStart) {
        var mOnTimerCount: OnTimerCount? = null

        init {
            mOnTimerCount = onTimerCount
        }

        override fun onTick(millisUntilFinished: Long) {
            if (mOnTimerCount != null) {
                mOnTimerCount!!.onTick(millisUntilFinished)
            }
        }

        override fun onFinish() {
            if (mOnTimerCount != null) {
                mOnTimerCount!!.onFinish()
            }
        }

    }

    /**
     * Start timer
     *
     * @param millisOnTimer
     * @param countDownInterval
     */
    fun startTimer(millisOnTimer: Long, countDownInterval: Long, onTimerCount: OnTimerCount) {
        if (mPauseTimer != null) {
            mPauseTimer!!.cancel()
        }
        mPauseTimer = PauseTimer(millisOnTimer, countDownInterval, true, onTimerCount)
        mPauseTimer!!.create()
    }

    /**
     * Pause
     */
    fun pause() {
        if (mPauseTimer != null) {
            mPauseTimer!!.pause()
        }
    }

    /**
     * Resume
     */
    fun resume() {
        if (mPauseTimer != null) {
            mPauseTimer!!.resume()
        }
    }

    /**
     * Stop and cancel the timer
     */
    fun cancel() {
        if (mPauseTimer != null) {
            mPauseTimer!!.mOnTimerCount = null
            mPauseTimer!!.cancel()
        }
    }

    interface OnTimerCount {
        fun onTick(millisUntilFinished: Long)

        fun onFinish()
    }

}

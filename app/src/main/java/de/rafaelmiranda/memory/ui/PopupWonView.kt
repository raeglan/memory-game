package de.rafaelmiranda.memory.ui

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.os.Handler
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.animation.BounceInterpolator
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView

import de.rafaelmiranda.memory.R
import de.rafaelmiranda.memory.common.Music
import de.rafaelmiranda.memory.common.Shared
import de.rafaelmiranda.memory.events.ui.BackGameEvent
import de.rafaelmiranda.memory.events.ui.NextGameEvent
import de.rafaelmiranda.memory.model.GameState
import de.rafaelmiranda.memory.utils.Clock
import de.rafaelmiranda.memory.utils.FontLoader

class PopupWonView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) : RelativeLayout(context, attrs) {

    private val mTime: TextView
    private val mScore: TextView
    private val mStar1: ImageView
    private val mStar2: ImageView
    private val mStar3: ImageView
    private val mNextButton: ImageView
    private val mBackButton: ImageView
    private val mHandler: Handler

    init {
        LayoutInflater.from(context).inflate(R.layout.popup_won_view, this, true)
        mTime = findViewById<View>(R.id.time_bar_text) as TextView
        mScore = findViewById<View>(R.id.score_bar_text) as TextView
        mStar1 = findViewById<View>(R.id.star_1) as ImageView
        mStar2 = findViewById<View>(R.id.star_2) as ImageView
        mStar3 = findViewById<View>(R.id.star_3) as ImageView
        mBackButton = findViewById<View>(R.id.button_back) as ImageView
        mNextButton = findViewById<View>(R.id.button_next) as ImageView
        FontLoader.setTypeface(context, arrayOf(mTime, mScore), FontLoader.Font.GROBOLD)
        setBackgroundResource(R.drawable.level_complete)
        mHandler = Handler()

        mBackButton.setOnClickListener { Shared.eventBus.notify(BackGameEvent()) }

        mNextButton.setOnClickListener { Shared.eventBus.notify(NextGameEvent()) }
    }

    fun setGameState(gameState: GameState) {
        val min = gameState.remainedSeconds / 60
        val sec = gameState.remainedSeconds - min * 60
        mTime.text = " ${String.format("%02d", min)}:${String.format("%02d", sec)}"
        mScore.text = 0.toString()

        mHandler.postDelayed({
            animateScoreAndTime(gameState.remainedSeconds, gameState.achievedScore)
            animateStars(gameState.achievedStars)
        }, 500)
    }

    private fun animateStars(start: Int) {
        when (start) {
            0 -> {
                mStar1.visibility = View.GONE
                mStar2.visibility = View.GONE
                mStar3.visibility = View.GONE
            }
            1 -> {
                mStar2.visibility = View.GONE
                mStar3.visibility = View.GONE
                mStar1.alpha = 0f
                animateStar(mStar1, 0)
            }
            2 -> {
                mStar3.visibility = View.GONE
                mStar1.visibility = View.VISIBLE
                mStar1.alpha = 0f
                animateStar(mStar1, 0)
                mStar2.visibility = View.VISIBLE
                mStar2.alpha = 0f
                animateStar(mStar2, 600)
            }
            3 -> {
                mStar1.visibility = View.VISIBLE
                mStar1.alpha = 0f
                animateStar(mStar1, 0)
                mStar2.visibility = View.VISIBLE
                mStar2.alpha = 0f
                animateStar(mStar2, 600)
                mStar3.visibility = View.VISIBLE
                mStar3.alpha = 0f
                animateStar(mStar3, 1200)
            }
            else -> {
            }
        }
    }

    private fun animateStar(view: View, delay: Int) {
        val alpha = ObjectAnimator.ofFloat(view, "alpha", 0f, 1f)
        alpha.duration = 100
        val scaleX = ObjectAnimator.ofFloat(view, "scaleX", 0f, 1f)
        val scaleY = ObjectAnimator.ofFloat(view, "scaleY", 0f, 1f)
        val animatorSet = AnimatorSet()
        animatorSet.playTogether(alpha, scaleX, scaleY)
        animatorSet.interpolator = BounceInterpolator()
        animatorSet.startDelay = delay.toLong()
        animatorSet.duration = 600
        view.setLayerType(View.LAYER_TYPE_HARDWARE, null)
        animatorSet.start()

        mHandler.postDelayed({ Music.showStar() }, delay.toLong())
    }

    private fun animateScoreAndTime(remainedSeconds: Int, achievedScore: Int) {
        val totalAnimation = 1200

        Clock.startTimer(totalAnimation.toLong(), 35, object : Clock.OnTimerCount {

            // TODO: WTF!
            override fun onTick(millisUntilFinished: Long) {
                val factor = millisUntilFinished / (totalAnimation * 1f) // 0.1
                val scoreToShow = achievedScore - (achievedScore * factor).toInt()
                val timeToShow = (remainedSeconds * factor).toInt()
                val min = timeToShow / 60
                val sec = timeToShow - min * 60
                mTime.text = " " + String.format("%02d", min) + ":" + String.format("%02d", sec)
                mScore.text = "" + scoreToShow
            }

            override fun onFinish() {
                mTime.text = " " + String.format("%02d", 0) + ":" + String.format("%02d", 0)
                mScore.text = "" + achievedScore
            }
        })

    }

}

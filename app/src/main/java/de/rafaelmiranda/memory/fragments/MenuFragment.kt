package de.rafaelmiranda.memory.fragments

import android.animation.*
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.view.animation.BounceInterpolator
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import android.widget.Toast
import de.rafaelmiranda.memory.R
import de.rafaelmiranda.memory.common.Music
import de.rafaelmiranda.memory.common.Shared
import de.rafaelmiranda.memory.events.ui.StartEvent
import de.rafaelmiranda.memory.ui.PopupManager
import de.rafaelmiranda.memory.utils.Utils

class MenuFragment : Fragment() {

    private lateinit var mTitle: ImageView
    private lateinit var mStartGameButton: ImageView
    private lateinit var mStartButtonLights: ImageView
    private lateinit var mTooltip: ImageView
    private lateinit var mSettingsGameButton: ImageView
    private lateinit var mGooglePlayGameButton: ImageView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.menu_fragment, container, false)
        mTitle = view.findViewById(R.id.title)
        mStartGameButton = view.findViewById(R.id.start_game_button)
        mSettingsGameButton = view.findViewById(R.id.settings_game_button)
        mSettingsGameButton!!.isSoundEffectsEnabled = false
        mSettingsGameButton!!.setOnClickListener { PopupManager.showPopupSettings() }
        mGooglePlayGameButton = view.findViewById(R.id.google_play_button)
        mGooglePlayGameButton!!.setOnClickListener {
            Toast.makeText(activity,
                    "Leaderboards will be available in the next game updates",
                    Toast.LENGTH_LONG).show()
        }
        mStartButtonLights = view.findViewById(R.id.start_game_button_lights)
        mTooltip = view.findViewById(R.id.tooltip)
        mStartGameButton!!.setOnClickListener {
            // animate title from place and navigation buttons from place
            animateAllAssetsOff(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    Shared.eventBus.notify(StartEvent())
                }
            })
        }

        startLightsAnimation()
        startTootipAnimation()

        // play background music
        Music.playBackgroundMusic()
        return view
    }

    protected fun animateAllAssetsOff(adapter: AnimatorListenerAdapter) {
        // title
        // 120dp + 50dp + buffer(30dp)
        val titleAnimator = ObjectAnimator.ofFloat(mTitle,
                "translationY", Utils.px(-200).toFloat())
        titleAnimator.setInterpolator(AccelerateInterpolator(2f))
        titleAnimator.setDuration(300)

        // lights
        val lightsAnimatorX = ObjectAnimator.ofFloat(mStartButtonLights,
                "scaleX", 0f)
        val lightsAnimatorY = ObjectAnimator.ofFloat(mStartButtonLights,
                "scaleY", 0f)

        // tooltip
        val tooltipAnimator = ObjectAnimator.ofFloat(mTooltip, "alpha",
                0f)
        tooltipAnimator.duration = 100

        // settings button
        val settingsAnimator = ObjectAnimator.ofFloat(mSettingsGameButton,
                "translationY", Utils.px(120).toFloat())
        settingsAnimator.interpolator = AccelerateInterpolator(2f)
        settingsAnimator.duration = 300

        // google play button
        val googlePlayAnimator = ObjectAnimator.ofFloat(mGooglePlayGameButton,
                "translationY", Utils.px(120).toFloat())
        googlePlayAnimator.interpolator = AccelerateInterpolator(2f)
        googlePlayAnimator.duration = 300

        // start button
        val startButtonAnimator = ObjectAnimator.ofFloat(mStartGameButton,
                "translationY", Utils.px(130).toFloat())
        startButtonAnimator.interpolator = AccelerateInterpolator(2f)
        startButtonAnimator.setDuration(300)

        val animatorSet = AnimatorSet()
        animatorSet.playTogether(titleAnimator, lightsAnimatorX, lightsAnimatorY, tooltipAnimator,
                settingsAnimator, googlePlayAnimator, startButtonAnimator)
        animatorSet.addListener(adapter)
        animatorSet.start()
    }

    private fun startTootipAnimation() {
        val scaleY = ObjectAnimator.ofFloat(mTooltip, "scaleY", 0.8f)
        scaleY.duration = 200
        val scaleYBack = ObjectAnimator.ofFloat(mTooltip, "scaleY", 1f)
        scaleYBack.duration = 500
        scaleYBack.interpolator = BounceInterpolator()
        val animatorSet = AnimatorSet()
        animatorSet.startDelay = 1000
        animatorSet.playSequentially(scaleY, scaleYBack)
        animatorSet.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                animatorSet.startDelay = 2000
                animatorSet.start()
            }
        })
        mTooltip!!.setLayerType(View.LAYER_TYPE_HARDWARE, null)
        animatorSet.start()
    }

    private fun startLightsAnimation() {
        val animator = ObjectAnimator.ofFloat(mStartButtonLights,
                "rotation", 0f, 360f)
        animator.interpolator = LinearInterpolator()
        animator.duration = 6000
        animator.repeatCount = ValueAnimator.INFINITE
        mStartButtonLights!!.setLayerType(View.LAYER_TYPE_HARDWARE, null)
        animator.start()
    }

}

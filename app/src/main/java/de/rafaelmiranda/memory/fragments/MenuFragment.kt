package de.rafaelmiranda.memory.fragments

import android.animation.*
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import de.rafaelmiranda.memory.R
import de.rafaelmiranda.memory.common.MemoryDb
import de.rafaelmiranda.memory.common.Music
import de.rafaelmiranda.memory.engine.Engine
import de.rafaelmiranda.memory.ui.PopupManager
import de.rafaelmiranda.memory.utils.PreferencesUtil
import de.rafaelmiranda.memory.utils.Utils

class MenuFragment : Fragment() {

    private lateinit var mTitle: ImageView
    private lateinit var mStartGameButton: ImageView
    private lateinit var mStartButtonLights: ImageView
    private lateinit var mSettingsGameButton: ImageView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.menu_fragment, container, false)
        mTitle = view.findViewById(R.id.title)
        mStartGameButton = view.findViewById(R.id.start_game_button)
        mSettingsGameButton = view.findViewById(R.id.settings_game_button)
        mSettingsGameButton.isSoundEffectsEnabled = false
        mSettingsGameButton.setOnClickListener { PopupManager.showPopupSettings() }
        mStartButtonLights = view.findViewById(R.id.start_game_button_lights)
        mStartGameButton.setOnClickListener {
            // animate title from place and navigation buttons from place
            animateAllAssetsOff(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    val directedGame = PreferencesUtil(context!!).isDirectedGame()
                    Engine.onStartPressed(fragmentManager!!, directedGame)
                }
            })
        }

        // the wonderful mesmerizing lights animation
        startLightsAnimation()

        // play background music if any, and there isn't any. HA!
        Music.playBackgroundMusic()

        // to be safe, that when someone closes a game, we should also close the session
        // I just hope this doesn't break anything last minute. This would be B. A. D.
        MemoryDb.endSession()

        return view
    }

    protected fun animateAllAssetsOff(adapter: AnimatorListenerAdapter) {
        // title
        // 120dp + 50dp + buffer(30dp)
        val titleAnimator = ObjectAnimator.ofFloat(mTitle,
                "translationY", Utils.px(-200).toFloat())
        titleAnimator.interpolator = AccelerateInterpolator(2f)
        titleAnimator.duration = 300

        // lights
        val lightsAnimatorX = ObjectAnimator.ofFloat(mStartButtonLights,
                "scaleX", 0f)
        val lightsAnimatorY = ObjectAnimator.ofFloat(mStartButtonLights,
                "scaleY", 0f)

        // settings button
        val settingsAnimator = ObjectAnimator.ofFloat(mSettingsGameButton,
                "translationY", Utils.px(120).toFloat())
        settingsAnimator.interpolator = AccelerateInterpolator(2f)
        settingsAnimator.duration = 300

        // start button
        val startButtonAnimator = ObjectAnimator.ofFloat(mStartGameButton,
                "translationY", Utils.px(130).toFloat())
        startButtonAnimator.interpolator = AccelerateInterpolator(2f)
        startButtonAnimator.duration = 300

        val animatorSet = AnimatorSet()
        animatorSet.playTogether(titleAnimator, lightsAnimatorX, lightsAnimatorY,
                settingsAnimator, startButtonAnimator)
        animatorSet.addListener(adapter)
        animatorSet.start()
    }

    private fun startLightsAnimation() {
        val animator = ObjectAnimator.ofFloat(mStartButtonLights,
                "rotation", 0f, 360f)
        animator.interpolator = LinearInterpolator()
        animator.duration = 6000
        animator.repeatCount = ValueAnimator.INFINITE
        mStartButtonLights.setLayerType(View.LAYER_TYPE_HARDWARE, null)
        animator.start()
    }

}

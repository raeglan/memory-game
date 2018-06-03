package de.rafaelmiranda.memory.ui

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.graphics.Color
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.RelativeLayout.LayoutParams
import de.rafaelmiranda.memory.R
import de.rafaelmiranda.memory.common.Shared
import de.rafaelmiranda.memory.types.GameType

object PopupManager {

    var isShown: Boolean = false

    fun showPopupSettings() {
        isShown = true
        val popupContainer = Shared.activity.findViewById<RelativeLayout>(R.id.popup_container)
        popupContainer.removeAllViews()

        // background
        val imageView = ImageView(Shared.context)
        imageView.setBackgroundColor(Color.parseColor("#88555555"))
        imageView.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        imageView.isClickable = true
        imageView.setOnClickListener { PopupManager.closePopup() }
        popupContainer.addView(imageView)

        // popup
        val popupSettingsView = PopupSettingsView(Shared.context)
        val width = Shared.context.resources.getDimensionPixelSize(R.dimen.popup_settings_width)
        val height = Shared.context.resources.getDimensionPixelSize(R.dimen.popup_settings_height)
        val params = RelativeLayout.LayoutParams(width, height)
        params.addRule(RelativeLayout.CENTER_IN_PARENT)
        popupContainer.addView(popupSettingsView, params)

        // animate all together
        val scaleXAnimator = ObjectAnimator.ofFloat(popupSettingsView,
                "scaleX", 0f, 1f)
        val scaleYAnimator = ObjectAnimator.ofFloat(popupSettingsView,
                "scaleY", 0f, 1f)
        val alphaAnimator = ObjectAnimator.ofFloat(imageView,
                "alpha", 0f, 1f)
        val animatorSet = AnimatorSet()
        animatorSet.playTogether(scaleXAnimator, scaleYAnimator, alphaAnimator)
        animatorSet.duration = 500
        animatorSet.interpolator = DecelerateInterpolator(2f)
        animatorSet.start()
    }

    fun showPopupWon(@GameType.GameId gameType: Int) {
        isShown = true
        val popupContainer = Shared.activity.findViewById<RelativeLayout>(R.id.popup_container)
        popupContainer.removeAllViews()

        // popup with optional extra options
        val popupWonView = PopupWonView(Shared.context)
        if (gameType == GameType.ID_AUDITORY)
            popupWonView.setSumGame()

        // the whole params thingys
        val height = Shared.context.resources.getDimensionPixelSize(R.dimen.popup_height)
        val params = RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        params.addRule(RelativeLayout.CENTER_IN_PARENT)
        popupContainer.addView(popupWonView, params)

        // animate all together
        val scaleXAnimator = ObjectAnimator.ofFloat(popupWonView, "scaleX", 0f, 1f)
        val scaleYAnimator = ObjectAnimator.ofFloat(popupWonView, "scaleY", 0f, 1f)
        val animatorSet = AnimatorSet()
        animatorSet.playTogether(scaleXAnimator, scaleYAnimator)
        animatorSet.duration = 500
        animatorSet.interpolator = DecelerateInterpolator(2f)
        popupWonView.setLayerType(View.LAYER_TYPE_HARDWARE, null)
        animatorSet.start()
    }

    fun closePopup() {
        isShown = false
        val popupContainer = Shared.activity.findViewById<RelativeLayout>(R.id.popup_container)
        val childCount = popupContainer.childCount
        if (childCount > 0) {
            var background: View? = null
            val viewPopup: View
            if (childCount == 1) {
                viewPopup = popupContainer.getChildAt(0)
            } else {
                background = popupContainer.getChildAt(0)
                viewPopup = popupContainer.getChildAt(1)
            }

            val animatorSet = AnimatorSet()
            val scaleXAnimator = ObjectAnimator.ofFloat(viewPopup, "scaleX", 0f)
            val scaleYAnimator = ObjectAnimator.ofFloat(viewPopup, "scaleY", 0f)
            if (childCount > 1) {
                val alphaAnimator = ObjectAnimator.ofFloat(background!!, "alpha", 0f)
                animatorSet.playTogether(scaleXAnimator, scaleYAnimator, alphaAnimator)
            } else {
                animatorSet.playTogether(scaleXAnimator, scaleYAnimator)
            }
            animatorSet.duration = 300
            animatorSet.interpolator = AccelerateInterpolator(2f)
            animatorSet.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    popupContainer.removeAllViews()
                }
            })
            animatorSet.start()
        }
    }
}

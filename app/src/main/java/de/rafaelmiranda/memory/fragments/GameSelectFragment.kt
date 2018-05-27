package de.rafaelmiranda.memory.fragments

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import de.rafaelmiranda.memory.R
import de.rafaelmiranda.memory.common.Shared
import de.rafaelmiranda.memory.events.GameTypeSelectedEvent
import de.rafaelmiranda.memory.themes.GameType
import de.rafaelmiranda.memory.themes.Types
import org.greenrobot.eventbus.EventBus

class GameSelectFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = LayoutInflater.from(Shared.context).inflate(R.layout.theme_select_fragment, container, false)
        val normal = view.findViewById<View>(R.id.theme_animals_container)
        val auditory = view.findViewById<View>(R.id.theme_monsters_container)
        val visual = view.findViewById<View>(R.id.theme_emoji_container)

        val themeNormal = Types.createTheme(GameType.ID_NORMAL)
        val themeAuditory = Types.createTheme(GameType.ID_AUDITORY)
        val themeVisual = Types.createTheme(GameType.ID_VISUAL_BLUR)

        normal.setOnClickListener { EventBus.getDefault().post(GameTypeSelectedEvent(themeNormal)) }

        auditory.setOnClickListener { EventBus.getDefault().post(GameTypeSelectedEvent(themeAuditory)) }

        visual.setOnClickListener { EventBus.getDefault().post(GameTypeSelectedEvent(themeVisual)) }

        /*
         * Improve performance first!!!
         */
        animateShow(normal)
        animateShow(auditory)
        animateShow(visual)

        return view
    }

    private fun animateShow(view: View) {
        val animatorScaleX = ObjectAnimator.ofFloat(view, "scaleX", 0.5f, 1f)
        val animatorScaleY = ObjectAnimator.ofFloat(view, "scaleY", 0.5f, 1f)
        val animatorSet = AnimatorSet()
        animatorSet.duration = 300
        animatorSet.playTogether(animatorScaleX, animatorScaleY)
        animatorSet.interpolator = DecelerateInterpolator(2f)
        view.setLayerType(View.LAYER_TYPE_HARDWARE, null)
        animatorSet.start()
    }
}

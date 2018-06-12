package de.rafaelmiranda.memory.fragments

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.widget.Switch
import de.rafaelmiranda.memory.R
import de.rafaelmiranda.memory.common.Shared
import de.rafaelmiranda.memory.engine.Engine
import de.rafaelmiranda.memory.types.Assistants
import de.rafaelmiranda.memory.types.GameType
import de.rafaelmiranda.memory.types.Types

class GameSelectFragment : Fragment() {

    lateinit var replay: Switch
    lateinit var zoom: Switch

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = LayoutInflater.from(Shared.context).inflate(R.layout.theme_select_fragment, container, false)
        val normal = view.findViewById<View>(R.id.type_normal)
        val auditory = view.findViewById<View>(R.id.type_auditory)
        val visual = view.findViewById<View>(R.id.type_visual)

        // the cheats
        replay = view.findViewById(R.id.switch_replay_cheat)
        zoom = view.findViewById(R.id.switch_zoom_cheat)

        val typeNormal = Types.createType(GameType.ID_NORMAL)
        val typeAuditory = Types.createType(GameType.ID_AUDITORY)
        val typeVisual = Types.createType(GameType.ID_VISUAL_BLUR)

        normal.setOnClickListener { Engine.startGame(fragmentManager!!, typeNormal, getAssistants()) }

        auditory.setOnClickListener { Engine.startGame(fragmentManager!!, typeAuditory, getAssistants()) }

        visual.setOnClickListener { Engine.startGame(fragmentManager!!, typeVisual, getAssistants()) }

        /*
         * Improve performance first!!!
         */
        animateShow(normal)
        animateShow(auditory)
        animateShow(visual)

        return view
    }

    /**
     * Checks the checkboxes(hmm, actually switches) and creates the assistant type containing
     * everything.
     */
    private fun getAssistants(): Assistants {
        val assistants = Assistants()
        assistants.replayAllFlips = replay.isChecked
        assistants.zoomInOnFlip = zoom.isChecked

        return assistants
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

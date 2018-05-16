package de.rafaelmiranda.memory.fragments

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.graphics.Typeface
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.BounceInterpolator
import android.widget.TextView
import de.rafaelmiranda.memory.R
import de.rafaelmiranda.memory.common.Memory
import de.rafaelmiranda.memory.common.Shared
import de.rafaelmiranda.memory.events.DifficultySelectedEvent
import de.rafaelmiranda.memory.themes.Theme
import de.rafaelmiranda.memory.ui.DifficultyView
import org.greenrobot.eventbus.EventBus

class DifficultySelectFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = LayoutInflater.from(Shared.context).inflate(R.layout.difficulty_select_fragment, container, false)
        val theme = Shared.engine.selectedTheme

        // setting the images
        setDifficultyForTheme(view, theme!!)

        return view

    }

    private fun setDifficultyForTheme(view: View, theme: Theme) {

        // getting the views
        val difficulty1 = view.findViewById<DifficultyView>(R.id.select_difficulty_1)
        val difficulty2 = view.findViewById<DifficultyView>(R.id.select_difficulty_2)
        val difficulty3 = view.findViewById<DifficultyView>(R.id.select_difficulty_3)
        val difficulty4 = view.findViewById<DifficultyView>(R.id.select_difficulty_4)
        val difficulty5 = view.findViewById<DifficultyView>(R.id.select_difficulty_5)
        val difficulty6 = view.findViewById<DifficultyView>(R.id.select_difficulty_6)

        // setting the click listeners
        setOnClick(difficulty1, 1)
        setOnClick(difficulty2, 2)
        setOnClick(difficulty3, 3)
        setOnClick(difficulty4, 4)
        setOnClick(difficulty5, 5)
        setOnClick(difficulty6, 6)

        // setting images
        if (theme.id == Theme.ID_ANIMAL_VISUAL) {
            difficulty1.setImage(R.drawable.visual_theme_blur)
            difficulty2.setImage(R.drawable.visual_theme_retinopathy)
            difficulty3.visibility = View.INVISIBLE
            difficulty4.visibility = View.INVISIBLE
            difficulty5.visibility = View.INVISIBLE
            difficulty6.visibility = View.INVISIBLE
        } else {
            difficulty1.setDifficulty(1, Memory.getHighStars(theme.id, 1))

            difficulty2.setDifficulty(2, Memory.getHighStars(theme.id, 2))

            difficulty3.setDifficulty(3, Memory.getHighStars(theme.id, 3))

            difficulty4.setDifficulty(4, Memory.getHighStars(theme.id, 4))

            difficulty5.setDifficulty(5, Memory.getHighStars(theme.id, 5))

            difficulty6.setDifficulty(6, Memory.getHighStars(theme.id, 6))
        }

        animate(difficulty1, difficulty2, difficulty3, difficulty4, difficulty5, difficulty6)

        // now for the text
        val type = Typeface.createFromAsset(Shared.context.assets, "fonts/grobold.ttf")

        // getting views
        val text1 = view.findViewById<TextView>(R.id.time_difficulty_1)
        val text2 = view.findViewById<TextView>(R.id.time_difficulty_2)
        val text3 = view.findViewById<TextView>(R.id.time_difficulty_3)
        val text4 = view.findViewById<TextView>(R.id.time_difficulty_4)
        val text5 = view.findViewById<TextView>(R.id.time_difficulty_5)
        val text6 = view.findViewById<TextView>(R.id.time_difficulty_6)

        // if we are on the visual mode, no need to display a time.
        if (theme.id == Theme.ID_ANIMAL_VISUAL) {
            text1.visibility = View.INVISIBLE
            text2.visibility = View.INVISIBLE
            text3.visibility = View.INVISIBLE
            text4.visibility = View.INVISIBLE
            text5.visibility = View.INVISIBLE
            text6.visibility = View.INVISIBLE
        }

        text1.gravity = Gravity.CENTER_HORIZONTAL or Gravity.CENTER_VERTICAL
        text1.typeface = type
        text1.text = getBestTimeForStage(theme.id, 1)

        text2.gravity = Gravity.CENTER_HORIZONTAL or Gravity.CENTER_VERTICAL
        text2.typeface = type
        text2.text = getBestTimeForStage(theme.id, 2)

        text3.gravity = Gravity.CENTER_HORIZONTAL or Gravity.CENTER_VERTICAL
        text3.typeface = type
        text3.text = getBestTimeForStage(theme.id, 3)

        text4.gravity = Gravity.CENTER_HORIZONTAL or Gravity.CENTER_VERTICAL
        text4.typeface = type
        text4.text = getBestTimeForStage(theme.id, 4)

        text5.gravity = Gravity.CENTER_HORIZONTAL or Gravity.CENTER_VERTICAL
        text5.typeface = type
        text5.text = getBestTimeForStage(theme.id, 5)

        text6.gravity = Gravity.CENTER_HORIZONTAL or Gravity.CENTER_VERTICAL
        text6.typeface = type
        text6.text = getBestTimeForStage(theme.id, 6)
    }

    private fun getBestTimeForStage(theme: Int, difficulty: Int): String {
        val bestTime = Memory.getBestTime(theme, difficulty)
        if (bestTime != -1) {
            val minutes = bestTime % 3600 / 60
            val seconds = bestTime % 60
            return String.format("BEST : %02d:%02d", minutes, seconds)
        } else {
            return "BEST : -"
        }
    }

    private fun animate(vararg view: View) {
        val animatorSet = AnimatorSet()
        val builder = animatorSet.play(AnimatorSet())
        for (i in view.indices) {
            val scaleX = ObjectAnimator.ofFloat(view[i], "scaleX", 0.8f, 1f)
            val scaleY = ObjectAnimator.ofFloat(view[i], "scaleY", 0.8f, 1f)
            builder.with(scaleX).with(scaleY)
        }
        animatorSet.duration = 500
        animatorSet.interpolator = BounceInterpolator()
        animatorSet.start()
    }

    private fun setOnClick(view: View, difficulty: Int) {
        view.setOnClickListener { EventBus.getDefault().post(DifficultySelectedEvent(difficulty)) }
    }


}

package de.rafaelmiranda.memory.fragments

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.widget.ImageView
import de.rafaelmiranda.memory.R
import de.rafaelmiranda.memory.common.Memory
import de.rafaelmiranda.memory.common.Shared
import de.rafaelmiranda.memory.events.ThemeSelectedEvent
import de.rafaelmiranda.memory.themes.Theme
import de.rafaelmiranda.memory.themes.Themes
import org.greenrobot.eventbus.EventBus
import java.util.*

class ThemeSelectFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = LayoutInflater.from(Shared.context).inflate(R.layout.theme_select_fragment, container, false)
        val normal = view.findViewById<View>(R.id.theme_animals_container)
        val auditory = view.findViewById<View>(R.id.theme_monsters_container)
        val visual = view.findViewById<View>(R.id.theme_emoji_container)

        val themeNormal = Themes.createTheme(Theme.ID_ANIMAL)
        val themeAuditory = Themes.createTheme(Theme.ID_ANIMAL_AUDITORY)
        val themeVisual = Themes.createTheme(Theme.ID_ANIMAL_VISUAL_BLUR)

        normal.setOnClickListener { EventBus.getDefault().post(ThemeSelectedEvent(themeNormal)) }

        auditory.setOnClickListener { EventBus.getDefault().post(ThemeSelectedEvent(themeAuditory)) }

        visual.setOnClickListener { EventBus.getDefault().post(ThemeSelectedEvent(themeVisual)) }

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

    /**
     * For now I don't really need to show stars here, I may add it later.
     */
    private fun setStars(imageView: ImageView, theme: Theme, type: String) {
        var sum = 0
        for (difficulty in 1..6) {
            sum += Memory.getHighStars(theme.id, difficulty)
        }
        val num = sum / 6
        if (num != 0) {
            val drawableResourceName = String.format(Locale.US, type + "_theme_star_%d", num)
            val drawableResourceId = Shared.context.resources.getIdentifier(drawableResourceName, "drawable", Shared.context.packageName)
            imageView.setImageResource(drawableResourceId)
        }
    }
}

package de.rafaelmiranda.memory.dialogs

import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import de.rafaelmiranda.memory.R
import de.rafaelmiranda.memory.themes.GameType

/**
 * @author  rafael
 * @since   27.05.18
 */
class IntroDialog : DialogFragment() {

    companion object {

        const val KEY_GAME_ID = "gameID"

        fun newInstance(@GameType.GameId gameId: Int): IntroDialog {
            val bundle = Bundle(1)
            bundle.putInt(KEY_GAME_ID, gameId)
            return IntroDialog().apply { this.arguments = bundle }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.popup_intro, container, false)

        val gameId = arguments?.getInt(KEY_GAME_ID)

        // getting text views.
        val name: TextView = view.findViewById(R.id.name)
        val description: TextView = view.findViewById(R.id.description)

        // setting them up with wonderfully written text.
        when (gameId) {
            GameType.ID_NORMAL -> {
                name.setText(R.string.normal_game)
                description.setText(R.string.normal_game_description)
            }
            GameType.ID_AUDITORY -> {
                name.setText(R.string.auditory_distraction_game)
                description.setText(R.string.auditory_distraction_game_description)
            }
            GameType.ID_VISUAL_BLUR -> {
                name.setText(R.string.visual_blur_game)
                description.setText(R.string.visual_blur_game_description)
            }
            else -> throw IllegalArgumentException("GameId $gameId not found.")
        }

        // setting our one and only button.
        view.findViewById<ImageView>(R.id.button_next)
                .setOnClickListener {
                    dismiss()
                }

        return view
    }
}
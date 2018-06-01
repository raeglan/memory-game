package de.rafaelmiranda.memory.dialogs

import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import de.rafaelmiranda.memory.R
import de.rafaelmiranda.memory.types.GameType

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

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = LayoutInflater.from(context)
                .inflate(R.layout.popup_intro, null)

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

        val dialog = Dialog(context!!, R.style.ThemeOverlay_AppCompat_Dialog)
        dialog.setContentView(view)
        return dialog
    }
}
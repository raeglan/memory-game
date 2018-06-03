package de.rafaelmiranda.memory.dialogs

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import de.rafaelmiranda.memory.R
import de.rafaelmiranda.memory.types.GameType


/**
 * The wonderful intro dialog, which is henceforth the source of truth for all Memory games.
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

        // creating the bastard
        val dialog = Dialog(context!!, R.style.ThemeOverlay_AppCompat_Dialog)
        dialog.setContentView(view)
        val window = dialog.window

        // making it look fabulous
        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        // making it immersive
        window.setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE)
        window.decorView.systemUiVisibility = activity!!.window.decorView.systemUiVisibility

        dialog.setOnShowListener {
            //Clear the not focusable flag from the window
            window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE)

            //Update the WindowManager with the new attributes (no nicer way I know of to do this)..
            val wm = activity!!.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            wm.updateViewLayout(window.decorView, window.attributes)
        }

        return dialog
    }

}
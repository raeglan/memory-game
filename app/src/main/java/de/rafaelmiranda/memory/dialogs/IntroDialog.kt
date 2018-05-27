package de.rafaelmiranda.memory.dialogs

import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import de.rafaelmiranda.memory.themes.GameType

/**
 * @author  rafael
 * @since   27.05.18
 */
class IntroDialog : DialogFragment() {

    companion object {
        fun newInstance(@GameType.GameId gameId: Int) {
            // TODO: Create instance.
        }
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return super.onCreateView(inflater, container, savedInstanceState)
    }
}
package de.rafaelmiranda.memory.engine

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import de.rafaelmiranda.memory.R

object ScreenController {

    fun openFragment(
            fragmentManager: FragmentManager,
            fragment: Fragment,
            addToBackStack: Boolean = true) {

        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragment_container, fragment)

        // The menu and game should be kept out of the back stack. They are both our ends.
        if (addToBackStack)
            fragmentTransaction.addToBackStack(fragment.tag)

        fragmentTransaction.commit()
    }
}

package de.rafaelmiranda.memory.engine

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import de.rafaelmiranda.memory.R
import de.rafaelmiranda.memory.common.Shared
import de.rafaelmiranda.memory.fragments.GameFragment
import de.rafaelmiranda.memory.fragments.GameSelectFragment
import de.rafaelmiranda.memory.fragments.GameSettingsFragment
import de.rafaelmiranda.memory.fragments.MenuFragment

object ScreenController {
    private var mFragmentManager: FragmentManager? = null
    private val openedScreens = LinkedHashMap<Int, Screen>()


    enum class Screen {
        MENU,
        GAME,
        GAME_SELECT,
        GAME_SETTINGS
    }

    fun openScreen(screen: Screen) {
        mFragmentManager = Shared.activity.supportFragmentManager
        val fragment = getFragment(screen)
        val fragmentTransaction = mFragmentManager!!.beginTransaction()
        fragmentTransaction.replace(R.id.fragment_container, fragment)
        fragmentTransaction.commit()
        openedScreens[screen.ordinal] = screen
    }

    /**
     * Pops the currently opened screen and opens the next one.
     *
     * @return true if no screen is currently on the back stack. Telling the activity it should
     * finish
     */
    fun onBack(): Boolean {
        if (openedScreens.size > 1) {

            val lastScreenKey = openedScreens.entries.last().key

            val lastScreen = openedScreens.remove(lastScreenKey) ?: return true
            openScreen(lastScreen)
            return false
        }
        return true
    }

    private fun getFragment(screen: Screen): Fragment {
        // todo: This should be better written. Nothing against a good quick and dirty solution.
        return when (screen) {
            ScreenController.Screen.MENU -> MenuFragment()
            ScreenController.Screen.GAME -> {
                GameFragment.newInstance(Shared.engine.selectedGameType!!.id)
            }
            ScreenController.Screen.GAME_SELECT -> GameSelectFragment()
            ScreenController.Screen.GAME_SETTINGS -> GameSettingsFragment()
        }
    }
}

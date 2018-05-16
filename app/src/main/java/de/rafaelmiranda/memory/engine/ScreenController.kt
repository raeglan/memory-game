package de.rafaelmiranda.memory.engine

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import de.rafaelmiranda.memory.R
import de.rafaelmiranda.memory.common.Shared
import de.rafaelmiranda.memory.events.ResetBackgroundEvent
import de.rafaelmiranda.memory.fragments.*
import org.greenrobot.eventbus.EventBus
import java.util.*

object ScreenController {
    private var mFragmentManager: FragmentManager? = null
    private val openedScreens = ArrayList<Screen>()


    enum class Screen {
        MENU,
        GAME,
        DIFFICULTY,
        THEME_SELECT,
        GAME_SETTINGS
    }

    fun openScreen(screen: Screen) {
        mFragmentManager = Shared.activity!!.supportFragmentManager

        if (screen == Screen.GAME && openedScreens[openedScreens.size - 1] == Screen.GAME) {
            openedScreens.removeAt(openedScreens.size - 1)
        } else if (screen == Screen.DIFFICULTY && openedScreens[openedScreens.size - 1] == Screen.GAME) {
            openedScreens.removeAt(openedScreens.size - 1)
            openedScreens.removeAt(openedScreens.size - 1)
        }
        val fragment = getFragment(screen)
        val fragmentTransaction = mFragmentManager!!.beginTransaction()
        fragmentTransaction.replace(R.id.fragment_container, fragment)
        fragmentTransaction.commit()
        openedScreens.add(screen)
    }

    /**
     * Pops the currently opened screen and opens the next one.
     *
     * @return true if no screen is currently on the back stack. Telling the activity it should
     * finish
     */
    fun onBack(): Boolean {
        if (openedScreens.size > 0) {
            val screenToRemove = openedScreens[openedScreens.size - 1]
            openedScreens.removeAt(openedScreens.size - 1)
            if (openedScreens.size == 0) {
                return true
            }
            val screen = openedScreens[openedScreens.size - 1]
            openedScreens.removeAt(openedScreens.size - 1)
            openScreen(screen)
            if ((screen == Screen.THEME_SELECT || screen == Screen.MENU) && (screenToRemove == Screen.DIFFICULTY || screenToRemove == Screen.GAME)) {
                EventBus.getDefault().post(ResetBackgroundEvent())
            }
            return false
        }
        return true
    }

    private fun getFragment(screen: Screen): Fragment? {
        return when (screen) {
            ScreenController.Screen.MENU -> MenuFragment()
            ScreenController.Screen.DIFFICULTY -> DifficultySelectFragment()
            ScreenController.Screen.GAME -> GameFragment()
            ScreenController.Screen.THEME_SELECT -> ThemeSelectFragment()
            ScreenController.Screen.GAME_SETTINGS -> GameSettingsFragment()
        }
    }

    val lastScreen: Screen
        get() = openedScreens[openedScreens.size - 1]
}

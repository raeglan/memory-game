package de.rafaelmiranda.memory.engine;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import de.rafaelmiranda.memory.R;
import de.rafaelmiranda.memory.common.Shared;
import de.rafaelmiranda.memory.events.ui.ResetBackgroundEvent;
import de.rafaelmiranda.memory.fragments.DifficultySelectFragment;
import de.rafaelmiranda.memory.fragments.GameFragment;
import de.rafaelmiranda.memory.fragments.GameSettingsFragment;
import de.rafaelmiranda.memory.fragments.MenuFragment;
import de.rafaelmiranda.memory.fragments.ThemeSelectFragment;

import java.util.ArrayList;
import java.util.List;

public class ScreenController {

    private static ScreenController mInstance = null;
    private static List<Screen> openedScreens = new ArrayList<>();
    private FragmentManager mFragmentManager;

    private ScreenController() {
    }

    public static ScreenController getInstance() {
        if (mInstance == null) {
            mInstance = new ScreenController();
        }
        return mInstance;
    }

    public enum Screen {
        MENU,
        GAME,
        DIFFICULTY,
        THEME_SELECT,
        GAME_SETTINGS
    }

    public static Screen getLastScreen() {
        return openedScreens.get(openedScreens.size() - 1);
    }

    public void openScreen(Screen screen) {
        mFragmentManager = Shared.activity.getSupportFragmentManager();

        if (screen == Screen.GAME && openedScreens.get(openedScreens.size() - 1) == Screen.GAME) {
            openedScreens.remove(openedScreens.size() - 1);
        } else if (screen == Screen.DIFFICULTY && openedScreens.get(openedScreens.size() - 1) == Screen.GAME) {
            openedScreens.remove(openedScreens.size() - 1);
            openedScreens.remove(openedScreens.size() - 1);
        }
        Fragment fragment = getFragment(screen);
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
        openedScreens.add(screen);
    }

    /**
     * Pops the currently opened screen and opens the next one.
     *
     * @return true if no screen is currently on the back stack. Telling the activity it should
     * finish
     */
    public boolean onBack() {
        if (openedScreens.size() > 0) {
            Screen screenToRemove = openedScreens.get(openedScreens.size() - 1);
            openedScreens.remove(openedScreens.size() - 1);
            if (openedScreens.size() == 0) {
                return true;
            }
            Screen screen = openedScreens.get(openedScreens.size() - 1);
            openedScreens.remove(openedScreens.size() - 1);
            openScreen(screen);
            if ((screen == Screen.THEME_SELECT || screen == Screen.MENU) &&
                    (screenToRemove == Screen.DIFFICULTY || screenToRemove == Screen.GAME)) {
                Shared.eventBus.notify(new ResetBackgroundEvent());
            }
            return false;
        }
        return true;
    }

    private Fragment getFragment(Screen screen) {
        switch (screen) {
            case MENU:
                return new MenuFragment();
            case DIFFICULTY:
                return new DifficultySelectFragment();
            case GAME:
                return new GameFragment();
            case THEME_SELECT:
                return new ThemeSelectFragment();
            case GAME_SETTINGS:
                return new GameSettingsFragment();
            default:
                break;
        }
        return null;
    }
}

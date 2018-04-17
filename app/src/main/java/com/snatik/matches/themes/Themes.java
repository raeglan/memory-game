package com.snatik.matches.themes;

import android.graphics.Bitmap;

import com.snatik.matches.common.Shared;
import com.snatik.matches.utils.Utils;

import java.util.ArrayList;

public class Themes {

    public static String URI_DRAWABLE = "drawable://";

    public static Theme createTheme(@Theme.ThemeId int themeId) {
        switch (themeId) {
            case Theme.ID_ANIMAL:
            case Theme.ID_ANIMAL_VISUAL:
            case Theme.ID_ANIMAL_AUDITORY:
                Theme animalTheme = createAnimalsTheme();
                animalTheme.id = themeId;
                return animalTheme;
            case Theme.ID_MONSTER:
                return createMosterTheme();
            case Theme.ID_EMOJI:
                return createEmojiTheme();
            default:
                throw new IllegalArgumentException("Theme " + themeId + " not found.");
        }
    }


    public static Theme createAnimalsTheme() {
        Theme theme = new Theme();
        theme.id = Theme.ID_ANIMAL;
        theme.name = "Animals";
        theme.backgroundImageUrl = URI_DRAWABLE + "back_animals";
        theme.tileImageUrls = new ArrayList<>();
        // 40 drawables
        for (int i = 1; i <= 28; i++) {
            theme.tileImageUrls.add(URI_DRAWABLE + String.format("animals_%d", i));
        }
        return theme;
    }

    public static Theme createMosterTheme() {
        Theme theme = new Theme();
        theme.id = Theme.ID_MONSTER;
        theme.name = "Mosters";
        theme.backgroundImageUrl = URI_DRAWABLE + "back_horror";
        theme.tileImageUrls = new ArrayList<>();
        // 40 drawables
        for (int i = 1; i <= 40; i++) {
            theme.tileImageUrls.add(URI_DRAWABLE + String.format("mosters_%d", i));
        }
        return theme;
    }

    public static Theme createEmojiTheme() {
        Theme theme = new Theme();
        theme.id = Theme.ID_EMOJI;
        theme.name = "Emoji";
        theme.backgroundImageUrl = URI_DRAWABLE + "background";
        theme.tileImageUrls = new ArrayList<>();
        // 40 drawables
        for (int i = 1; i <= 40; i++) {
            theme.tileImageUrls.add(URI_DRAWABLE + String.format("emoji_%d", i));
        }
        return theme;
    }

    public static Bitmap getBackgroundImage(Theme theme) {
        String drawableResourceName = theme.backgroundImageUrl.substring(Themes.URI_DRAWABLE.length());
        int drawableResourceId = Shared.context.getResources().getIdentifier(drawableResourceName, "drawable", Shared.context.getPackageName());
        Bitmap bitmap = Utils.scaleDown(drawableResourceId, Utils.screenWidth(), Utils.screenHeight());
        return bitmap;
    }

}

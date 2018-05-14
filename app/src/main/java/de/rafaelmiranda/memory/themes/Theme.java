package de.rafaelmiranda.memory.themes;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;

public class Theme {

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({ID_ANIMAL, ID_ANIMAL_AUDITORY, ID_ANIMAL_VISUAL, ID_MONSTER, ID_EMOJI})
    public @interface ThemeId {
    }

    public static final int ID_ANIMAL = 1;
    public static final int ID_ANIMAL_AUDITORY = 101;
    public static final int ID_ANIMAL_VISUAL = 102;
    public static final int ID_MONSTER = 2;
    public static final int ID_EMOJI = 3;


    public @ThemeId int id;
    public String name;
    public String backgroundImageUrl;
    public List<String> tileImageUrls;
    public List<String> adKeywords;
    public String backgroundSoundUrl;
    public String clickSoundUrl;
}

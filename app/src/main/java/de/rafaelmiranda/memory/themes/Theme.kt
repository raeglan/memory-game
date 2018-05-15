package de.rafaelmiranda.memory.themes

import android.support.annotation.IntDef

import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy

class Theme {


    @ThemeId
    var id: Int = 0
    var name: String? = null
    lateinit var backgroundImageUrl: String
    lateinit var tileImageUrls: List<String>
    var adKeywords: List<String>? = null
    var backgroundSoundUrl: String? = null
    var clickSoundUrl: String? = null

    @Retention(RetentionPolicy.SOURCE)
    @IntDef(ID_ANIMAL, ID_ANIMAL_AUDITORY, ID_ANIMAL_VISUAL, ID_MONSTER, ID_EMOJI)
    annotation class ThemeId

    companion object {

        const val ID_ANIMAL = 1
        const val ID_ANIMAL_AUDITORY = 101
        const val ID_ANIMAL_VISUAL = 102
        const val ID_MONSTER = 2
        const val ID_EMOJI = 3
    }
}

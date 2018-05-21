package de.rafaelmiranda.memory.themes

import android.support.annotation.IntDef

class Theme {


    @ThemeId
    var id: Int = 0
    var name: String? = null
    lateinit var backgroundImageUrl: String
    lateinit var tileImageUrls: List<String>

    @Retention(AnnotationRetention.SOURCE)
    @IntDef(ID_ANIMAL, ID_ANIMAL_AUDITORY, ID_ANIMAL_VISUAL_BLUR)
    annotation class ThemeId

    companion object {
        const val ID_ANIMAL = 1
        const val ID_ANIMAL_AUDITORY = 101
        const val ID_ANIMAL_VISUAL_BLUR = 102
    }
}

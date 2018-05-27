package de.rafaelmiranda.memory.themes

import android.support.annotation.IntDef

class GameType {

    @GameId
    var id: Int = -1
    var name: String? = null
    lateinit var backgroundImageUrl: String
    lateinit var tileImageUrls: List<String>

    @Retention(AnnotationRetention.SOURCE)
    @IntDef(ID_NORMAL, ID_AUDITORY, ID_VISUAL_BLUR)
    annotation class GameId

    companion object {
        const val ID_NORMAL = 100
        const val ID_AUDITORY = 101
        const val ID_VISUAL_BLUR = 102
    }
}
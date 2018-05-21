package de.rafaelmiranda.memory.model

import de.rafaelmiranda.memory.themes.Theme

enum class Impairment { NONE, VISUAL_BLUR, VISUAL_RETINOPATHY, AUDITORY_SUM_10 }

class BoardConfiguration(val difficulty: Int, @Theme.ThemeId theme: Int) {
    val numTiles: Int
    val numTilesInRow: Int
    val numRows: Int
    val time: Int
    val impairment: Impairment

    init {

        numTiles = 14
        numTilesInRow = 5
        numRows = 3
        time = 60 * 5

        impairment = when (theme) {
            Theme.ID_ANIMAL_AUDITORY -> Impairment.AUDITORY_SUM_10
            Theme.ID_ANIMAL_VISUAL_BLUR -> Impairment.VISUAL_BLUR
            else -> Impairment.NONE
        }
    }
}

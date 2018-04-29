package com.snatik.matches.model

import android.annotation.SuppressLint
import com.snatik.matches.themes.Theme

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

        if (theme == Theme.ID_ANIMAL_VISUAL) {
            impairment = when (difficulty) {
                1 -> Impairment.VISUAL_BLUR
                else -> Impairment.VISUAL_RETINOPATHY
            }
        } else {
            @SuppressLint("SwitchIntDef")
            impairment = when (theme) {
                Theme.ID_ANIMAL_AUDITORY -> Impairment.AUDITORY_SUM_10
                else -> Impairment.NONE
            }
        }
    }
}

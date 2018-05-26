package de.rafaelmiranda.memory.utils

import android.content.Context
import android.graphics.Typeface
import android.util.SparseArray
import android.widget.TextView
import de.rafaelmiranda.memory.R

object FontLoader {

    const val GROBOLD = R.font.grobold

    private val fonts = SparseArray<Typeface>()
    private var fontsLoaded = false

    enum class Font constructor(val intValue: Int) {
        GROBOLD(FontLoader.GROBOLD);
    }

    /**
     * Set the given font into the array of text views
     *
     * @param context
     * - the current context
     * @param textViews
     * - array of text views to set
     */
    fun setTypeface(context: Context, textViews: Array<TextView>, font: Font) {
        setTypeFaceToTextViews(context, textViews, font, Typeface.NORMAL)
    }

    private fun setTypeFaceToTextViews(context: Context, textViews: Array<TextView>, font: Font, fontStyle: Int) {

        val currentFont = context.resources.getFont(font.intValue)

        for (i in textViews.indices) {
            textViews[i].setTypeface(currentFont, fontStyle)
        }
    }

}

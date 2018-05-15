package de.rafaelmiranda.memory.utils

import android.content.Context
import android.graphics.Typeface
import android.util.SparseArray
import android.widget.TextView

object FontLoader {

    val GROBOLD = 0

    private val fonts = SparseArray<Typeface>()
    private var fontsLoaded = false

    enum class Font private constructor(val intValue: Int, val path: String) {
        GROBOLD(FontLoader.GROBOLD, "fonts/grobold.ttf");


        companion object {

            fun getByVal(`val`: Int): String? {
                for (font in values()) {
                    if (font.intValue == `val`) {
                        return font.path
                    }
                }
                return null
            }
        }
    }

    fun loadFonts(context: Context) {
        for (i in 0 until Font.values().size) {
            fonts.put(i, Typeface.createFromAsset(context.assets, Font.getByVal(i)))
        }
        fontsLoaded = true
    }

    /**
     * Returns a loaded custom font based on it's identifier.
     *
     * @param context
     * - the current context
     *
     * @return Typeface object of the requested font.
     */
    fun getTypeface(context: Context, font: Font): Typeface {
        if (!fontsLoaded) {
            loadFonts(context)
        }
        return fonts.get(font.intValue)
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

    /**
     * Set the given bold font into the array of text views
     *
     * @param context
     * - the current context
     * @param textViews
     * - array of text views to set
     */
    fun setBoldTypeface(context: Context, textViews: Array<TextView>, font: Font) {
        setTypeFaceToTextViews(context, textViews, font, Typeface.BOLD)
    }

    private fun setTypeFaceToTextViews(context: Context, textViews: Array<TextView>, font: Font, fontStyle: Int) {
        if (!fontsLoaded) {
            loadFonts(context)
        }
        val currentFont = fonts.get(font.intValue)

        for (i in textViews.indices) {
            textViews[i].setTypeface(currentFont, fontStyle)
        }
    }

}

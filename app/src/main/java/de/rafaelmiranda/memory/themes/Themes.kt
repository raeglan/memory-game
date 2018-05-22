package de.rafaelmiranda.memory.themes

import android.graphics.Bitmap
import de.rafaelmiranda.memory.common.Shared
import de.rafaelmiranda.memory.utils.Utils
import java.util.*


object Themes {

    const val URI_DRAWABLE = "drawable://"

    private const val NORMAL_SET_URI = "animals_%d"
    private const val BLUR_SET_URI = "animals_blur_%d"

    fun createTheme(@Theme.ThemeId themeId: Int): Theme {
        val theme = Theme()
        theme.id = themeId
        theme.name = "Animals"
        theme.tileImageUrls = createCardsSet(themeId)
        return theme
    }

    private fun createCardsSet(themeId: Int): List<String> {
        val tileImageUrls = ArrayList<String>()
        val baseCardsUri = if (themeId == Theme.ID_VISUAL_BLUR) BLUR_SET_URI else NORMAL_SET_URI
        // 40 drawables
        for (i in 1..28) {
            tileImageUrls.add(URI_DRAWABLE + String.format(baseCardsUri, i))
        }
        return tileImageUrls
    }


    fun getBackgroundImage(theme: Theme): Bitmap {
        val drawableResourceName = theme.backgroundImageUrl.substring(Themes.URI_DRAWABLE.length)
        val drawableResourceId = Shared.context.resources.getIdentifier(drawableResourceName, "drawable", Shared.context.packageName)
        return Utils.scaleDown(drawableResourceId, Utils.screenWidth(), Utils.screenHeight())
    }

}

package de.rafaelmiranda.memory.themes

import android.graphics.Bitmap
import de.rafaelmiranda.memory.common.Shared
import de.rafaelmiranda.memory.utils.Utils
import java.util.*

object Themes {

    var URI_DRAWABLE = "drawable://"

    fun createTheme(@Theme.ThemeId themeId: Int): Theme {
        when (themeId) {
            Theme.ID_ANIMAL, Theme.ID_ANIMAL_VISUAL_BLUR, Theme.ID_ANIMAL_AUDITORY -> {
                val animalTheme = createAnimalsTheme()
                animalTheme.id = themeId
                return animalTheme
            }
            else -> throw IllegalArgumentException("Theme $themeId not found.")
        }
    }


    fun createAnimalsTheme(): Theme {
        val theme = Theme()
        theme.id = Theme.ID_ANIMAL
        theme.name = "Animals"
        theme.backgroundImageUrl = URI_DRAWABLE + "back_animals"
        val tileImageUrls = ArrayList<String>()
        // 40 drawables
        for (i in 1..28) {
            tileImageUrls.add(URI_DRAWABLE + String.format("animals_%d", i))
        }
        theme.tileImageUrls = tileImageUrls
        return theme
    }



    fun getBackgroundImage(theme: Theme): Bitmap {
        val drawableResourceName = theme.backgroundImageUrl.substring(Themes.URI_DRAWABLE.length)
        val drawableResourceId = Shared.context.resources.getIdentifier(drawableResourceName, "drawable", Shared.context.packageName)
        return Utils.scaleDown(drawableResourceId, Utils.screenWidth(), Utils.screenHeight())
    }

}

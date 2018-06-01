package de.rafaelmiranda.memory.types

import android.graphics.Bitmap
import de.rafaelmiranda.memory.common.Shared
import de.rafaelmiranda.memory.utils.Utils
import java.util.*


object Types {

    const val URI_DRAWABLE = "drawable://"

    private const val NORMAL_SET_URI = "animals_%d"
    private const val BLUR_SET_URI = "animals_blur_%d"
    private const val NORMAL_BACKGROUND_URI = "back_animals"

    fun createTheme(@GameType.GameId themeId: Int): GameType {
        val theme = GameType()
        theme.id = themeId
        theme.name = "Animals"
        theme.tileImageUrls = createCardsSet(themeId)
        theme.backgroundImageUrl = URI_DRAWABLE + NORMAL_BACKGROUND_URI

        return theme
    }

    private fun createCardsSet(@GameType.GameId themeId: Int): List<String> {
        val tileImageUrls = ArrayList<String>()
        val baseCardsUri = if (themeId == GameType.ID_VISUAL_BLUR) BLUR_SET_URI else NORMAL_SET_URI
        // 40 drawables
        for (i in 1..28) {
            tileImageUrls.add(URI_DRAWABLE + String.format(baseCardsUri, i))
        }
        return tileImageUrls
    }


    fun getBackgroundImage(gameType: GameType): Bitmap {
        val drawableResourceName = gameType.backgroundImageUrl.substring(Types.URI_DRAWABLE.length)
        val drawableResourceId = Shared.context.resources.getIdentifier(drawableResourceName, "drawable", Shared.context.packageName)
        return Utils.scaleDown(drawableResourceId, Utils.screenWidth(), Utils.screenHeight())
    }

}

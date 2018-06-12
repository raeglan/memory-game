package de.rafaelmiranda.memory.model

import android.util.SparseArray
import de.rafaelmiranda.memory.common.Shared
import de.rafaelmiranda.memory.types.Types

/**
 * Before game starts, engine build new board
 *
 * @author sromku
 */
class BoardArrangement {

    /**
     * Maps a placement id to a tile image.
     */
    var tileUrls: SparseArray<String>? = null

    /**
     * A list of all currently placed cards mapped to their unique placement IDs.
     */
    var cards: SparseArray<Card>? = null

    /**
     * @param id The id is the number between 0 and number of possible tiles of
     * this gameType
     * @return The Bitmap of the tile
     */
    fun getTileResId(id: Int): Int {
        val string = tileUrls!!.get(id)
        val drawableResourceName = string.substring(Types.URI_DRAWABLE.length)
        return Shared
                .context
                .resources
                .getIdentifier(drawableResourceName,
                        "drawable", Shared.context.packageName)
        // val bitmap = Utils.scaleDown(drawableResourceId, size, size)
        // return Utils.crop(bitmap, size, size)
    }

    fun isPair(card1: Card, card2: Card): Boolean {
        return card1.pairId == card2.pairId
    }

}

package com.snatik.matches.model;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.util.SparseArray;

import com.snatik.matches.common.Shared;
import com.snatik.matches.themes.Themes;
import com.snatik.matches.utils.Utils;

/**
 * Before game starts, engine build new board
 *
 * @author sromku
 */
public class BoardArrangement {

    /**
     * Maps a placement id to a tile image.
     */
    public SparseArray<String> tileUrls;

    /**
     * A list of all currently placed cards mapped to their unique placement IDs.
     */
    public SparseArray<Card> cards;

    /**
     * @param id The id is the number between 0 and number of possible tiles of
     *           this theme
     * @return The Bitmap of the tile
     */
    public Bitmap getTileBitmap(int id, int size) {
        String string = tileUrls.get(id);
        if (string.contains(Themes.URI_DRAWABLE)) {
            String drawableResourceName = string.substring(Themes.URI_DRAWABLE.length());
            int drawableResourceId = Shared.context.getResources().getIdentifier(drawableResourceName, "drawable", Shared.context.getPackageName());
            Bitmap bitmap = Utils.scaleDown(drawableResourceId, size, size);
            return Utils.crop(bitmap, size, size);
        }
        return null;
    }

    public boolean isPair(@NonNull Card card1, @NonNull Card card2) {
        return card1.getPairId() == card2.getPairId();
    }

}

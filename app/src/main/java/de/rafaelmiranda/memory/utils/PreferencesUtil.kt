package de.rafaelmiranda.memory.utils

import android.content.Context
import android.support.v7.preference.PreferenceManager
import de.rafaelmiranda.memory.R
import de.rafaelmiranda.memory.model.AssistedGame
import de.rafaelmiranda.memory.types.Assistants
import de.rafaelmiranda.memory.types.GameType

/**
 * A collection of wonderful helpers for our shared preferences
 */
class PreferencesUtil(val context: Context) {

    /**
     * If this is a guided game or not. Returning whatever is the default from bools.xml
     */
    fun isDirectedGame(): Boolean {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        val default = context.resources.getBoolean(R.bool.pref_directed_default)
        return preferences.getBoolean(context.getString(R.string.pref_directed_game), default)
    }

    /**
     * Gets the list of all the games selected on the menu. An empty list will be return if nothing
     * was selected.
     */
    fun getGuidedGameList(): List<AssistedGame> {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        val gameList = ArrayList<AssistedGame>()

        val selected = preferences.getStringSet(context.getString(R.string.pref_selected_games),
                emptySet())

        // Normals
        if (selected.contains("N"))
            gameList.add(AssistedGame(GameType.ID_NORMAL))
        if (selected.contains("NZ"))
            gameList.add(AssistedGame(GameType.ID_NORMAL, Assistants().apply { zoomInOnFlip = true }))
        if (selected.contains("NR"))
            gameList.add(AssistedGame(GameType.ID_NORMAL, Assistants().apply { replayAllFlips = true }))

        // Visuals
        if (selected.contains("V"))
            gameList.add(AssistedGame(GameType.ID_VISUAL_BLUR))
        if (selected.contains("VZ"))
            gameList.add(AssistedGame(GameType.ID_VISUAL_BLUR, Assistants().apply { zoomInOnFlip = true }))
        if (selected.contains("VR"))
            gameList.add(AssistedGame(GameType.ID_VISUAL_BLUR, Assistants().apply { replayAllFlips = true }))

        // Auditory
        if (selected.contains("A"))
            gameList.add(AssistedGame(GameType.ID_AUDITORY))
        if (selected.contains("AZ"))
            gameList.add(AssistedGame(GameType.ID_AUDITORY, Assistants().apply { zoomInOnFlip = true }))
        if (selected.contains("AR"))
            gameList.add(AssistedGame(GameType.ID_AUDITORY, Assistants().apply { replayAllFlips = true }))

        return gameList
    }
}
package de.rafaelmiranda.memory.utils

import android.content.Context
import android.support.v7.preference.PreferenceManager
import de.rafaelmiranda.memory.R

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
}
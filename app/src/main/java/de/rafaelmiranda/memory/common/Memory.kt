package de.rafaelmiranda.memory.common

import android.content.Context

object Memory {

    private const val SHARED_PREFERENCES_NAME = "de.rafaelmiranda.memory"
    private const val HIGH_START_KEY = "theme_%d_difficulty_%d"
    private const val BEST_TIME_KEY = "themetime_%d_difficultytime_%d"

    fun save(theme: Int, difficulty: Int, stars: Int) {

        val highStars = getHighStars(theme, difficulty)

        if (stars > highStars) {
            val sharedPreferences = Shared.context.getSharedPreferences(SHARED_PREFERENCES_NAME,
                    Context.MODE_PRIVATE)
            val edit = sharedPreferences.edit()
            val key = String.format(HIGH_START_KEY, theme, difficulty)
            edit.putInt(key, stars).apply()
        }
    }

    fun saveTime(theme: Int, difficulty: Int, passedSecs: Int) {

        val bestTime = getBestTime(theme, difficulty)

        if (passedSecs < bestTime || bestTime == -1) {
            val sharedPreferences = Shared.context.getSharedPreferences(SHARED_PREFERENCES_NAME,
                    Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            val timeKey = String.format(BEST_TIME_KEY, theme, difficulty)
            editor.putInt(timeKey, passedSecs)
            editor.apply()
        }
    }

    fun getHighStars(theme: Int, difficulty: Int): Int {

        val sharedPreferences = Shared.context.getSharedPreferences(SHARED_PREFERENCES_NAME,
                Context.MODE_PRIVATE)
        val key = String.format(HIGH_START_KEY, theme, difficulty)
        return sharedPreferences.getInt(key, 0)
    }

    fun getBestTime(theme: Int, difficulty: Int): Int {

        val sharedPreferences = Shared.context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
        val key = String.format(BEST_TIME_KEY, theme, difficulty)
        return sharedPreferences.getInt(key, -1)
    }

}

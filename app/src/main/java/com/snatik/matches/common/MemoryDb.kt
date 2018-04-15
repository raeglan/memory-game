package com.snatik.matches.common

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.os.AsyncTask
import com.snatik.matches.common.MemoryContract.LogTable
import com.snatik.matches.themes.Theme


/**
 * The Memory DB helper and Util class
 */
class MemoryDbHelper(context: Context) :
        SQLiteOpenHelper(context,
                MemoryContract.DB_NAME,
                null,
                MemoryContract.DB_VERSION) {

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(MemoryContract.LogTable.CREATE_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        TODO("Implement an upgrade function when needed.")
    }
}

/**
 * The persistent memory for our memory game.
 */
object MemoryDb {

    /**
     * Adds the log of this game to our DB. The log has the timestamp of each action and which
     * card was flipped.
     */
    fun addGameLog(context: Context, @Theme.ThemeId gameType: Int, difficulty: Int,
                   gameLog: List<Pair<Long, String>>) {
        // we actually only want completed games here, but just to be safe.
        if (gameLog.size > 2) {

            // For the sake of simplicity the game only "starts" when the first card is displayed.
            val startTime = gameLog[0].first
            val endTime = gameLog[gameLog.size - 1].first

            // the logs will be saved as comma separated values inside our DB.
            val timeLogString = gameLog
                    .joinToString(separator = ",", transform = { pair -> pair.first.toString() })
            val gameLogString = gameLog
                    .joinToString(separator = ",", transform = { pair -> pair.second })

            // each of the values to its respective column
            val contentValues = ContentValues()
            contentValues.put(LogTable.COLUMN_GAME_TYPE, gameType)
            contentValues.put(LogTable.COLUMN_START_TIME, startTime)
            contentValues.put(LogTable.COLUMN_END_TIME, endTime)
            contentValues.put(LogTable.COLUMN_GAME_LOG, gameLogString)
            contentValues.put(LogTable.COLUMN_TIME_LOG, timeLogString)

            saveGameLogAsync(context, contentValues)
        }
    }

    private fun saveGameLogAsync(context: Context, contentValues: ContentValues) {
        object : AsyncTask<ContentValues, Void, Unit>() {
            override fun doInBackground(vararg p0: ContentValues?) {
                // now we get to persisting stuff. Exciting!
                val db = MemoryDbHelper(context).writableDatabase
                db?.insert(LogTable.TABLE_NAME, null, contentValues)
            }
        }.execute()
    }
}

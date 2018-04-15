package com.snatik.matches.common

import android.provider.BaseColumns

/**
 * The contract that defines how our DB is going to look like.
 */
object MemoryContract {

    const val DB_VERSION = 1
    const val DB_NAME = "MemoryGame"

    object LogTable : BaseColumns {
        const val TABLE_NAME = "logs"
        const val COLUMN_GAME_TYPE = "gameType"
        const val COLUMN_GAME_DIFFICULTY = "difficulty"
        const val COLUMN_START_TIME = "startTime"
        const val COLUMN_END_TIME = "endTime"
        const val COLUMN_GAME_LOG = "gameLog"
        const val COLUMN_TIME_LOG = "timeLog"

        const val CREATE_TABLE =
                "CREATE TABLE ${LogTable.TABLE_NAME} (" +
                        "${BaseColumns._ID} INTEGER PRIMARY KEY," +
                        "${LogTable.COLUMN_GAME_TYPE} INTEGER," +
                        "${LogTable.COLUMN_GAME_DIFFICULTY} INTEGER," +
                        "${LogTable.COLUMN_START_TIME} INTEGER," +
                        "${LogTable.COLUMN_END_TIME} INTEGER," +
                        "${LogTable.COLUMN_GAME_LOG} TEXT," +
                        "${LogTable.COLUMN_TIME_LOG} TEXT)"

        const val DELETE_TABLE =
                "DROP TABLE IF EXISTS ${LogTable.TABLE_NAME}"
    }
}
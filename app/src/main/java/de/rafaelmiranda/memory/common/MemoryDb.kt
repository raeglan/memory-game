package de.rafaelmiranda.memory.common

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import de.rafaelmiranda.memory.themes.Theme
import java.util.*
import kotlin.collections.HashMap

/**
 * The persistent memory for our memory game.
 */
object MemoryDb {

    @Suppress("unused")
    private const val TAG = "MemoryDb"

    const val COLLECTION_LOGS = "logs"
    const val COLLECTION_USERS = "users"

    // LOGS
    const val FIELD_GAME_LOG = "gameLog"
    const val FIELD_GAME_TYPE = "gameType"
    const val FIELD_TIME_LOG = "timeLog"
    const val FIELD_USER = "user"

    // USERS
    const val FIELD_NAME = "name"

    /**
     * Adds the log of this game to our DB. The log has the timestamp of each action and which
     * card was flipped.
     */
    fun addGameLog(@Theme.ThemeId gameType: Int, gameLog: List<Pair<Long, String>>,
                   user: String? = null) {

        val fireDb = FirebaseFirestore.getInstance()

        // the logs will be saved as comma separated values inside our DB.
        val timeLog = gameLog.map { pair -> Date(pair.first) }
        val movesLog = gameLog.map { pair -> pair.second }

        // each of the values to its respective fields
        val log = HashMap<String, Any>(5)
        log[FIELD_GAME_LOG] = movesLog
        log[FIELD_GAME_TYPE] = gameType
        log[FIELD_TIME_LOG] = timeLog
        if (user == null) {
            log[FIELD_USER] = fireDb.collection(COLLECTION_USERS).document("rafael")
        }
        // and now adding to our DB
        fireDb.collection(COLLECTION_LOGS)
                .add(log)
                .addOnSuccessListener { document -> Log.v(TAG, "New document with id: ${document.id}") }
                .addOnFailureListener { e -> e.printStackTrace() }
    }
}

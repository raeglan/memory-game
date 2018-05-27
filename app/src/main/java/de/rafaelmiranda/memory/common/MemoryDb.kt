package de.rafaelmiranda.memory.common

import android.util.Log
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import de.rafaelmiranda.memory.events.SessionStarted
import de.rafaelmiranda.memory.model.GameState
import org.greenrobot.eventbus.EventBus
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
    const val COLLECTION_SESSIONS = "sessions"

    // FIELDS
    const val FIELD_GAME_LOG = "gameLog" // STRING LIST
    const val FIELD_GAME_TYPE = "gameType" // INT
    const val FIELD_TIME_LOG = "timeLog" // DATE LIST
    const val FIELD_USER = "user" // USERS REFERENCE
    const val FIELD_SUM = "sum" // INT
    const val FIELD_SUM_USER_ANSWER = "sumUserAnswer" // INT
    const val FIELD_SESSION_LOGS = "logs" // {GAME_TYPE: LOGS REFERENCE}
    const val FIELD_START_TIME = "startTime" // DATE
    const val FIELD_NAME = "name" // STRING

    // LOGS: GAME_LOG, GAME_TYPE, TIME_LOG, USER, START_TIME, SUM, SUM_USER_ANSWER
    // SESSIONS: START_TIME(date), USER(ref), SESSION_LOGS(ref list)
    // USERS: NAME(string)

    // DEFAULT VALUES
    const val DEFAULT_USER = "unknown"

    // for session tracking
    private var sessionId: String? = null

    /**
     * Adds the log of this game to our DB. The log has the timestamp of each action and which
     * card was flipped.
     */
    fun addGameLog(gameState: GameState, user: String? = null) {

        val gameLog = gameState.gameLog
        val gameType = gameState.gameTypeId
        val fireDb = FirebaseFirestore.getInstance()
        val session = sessionId

        // the logs will be saved as comma separated values inside our DB.
        val timeLog = gameLog.map { action -> Date(action.timestamp) }
        val movesLog = gameLog.map { action -> action.move }
        val startTime = timeLog[0]

        // each of the values to its respective fields
        val log = HashMap<String, Any>(7)
        log[FIELD_GAME_LOG] = movesLog
        log[FIELD_GAME_TYPE] = gameType
        log[FIELD_TIME_LOG] = timeLog
        log[FIELD_START_TIME] = startTime
        log[FIELD_SUM] = gameState.numberSum
        log[FIELD_SUM_USER_ANSWER] = gameState.numberSumUserAnswer

        if (user == null) {
            log[FIELD_USER] = fireDb.collection(COLLECTION_USERS).document(DEFAULT_USER)
        }

        // and now adding to our DB
        fireDb.collection(COLLECTION_LOGS)
                .add(log)
                .addOnSuccessListener {
                    Log.v(TAG, "New log with id ${it.id} added")
                    if (session != null)
                        addToSession(gameType, session, it)
                }
                .addOnFailureListener { e -> e.printStackTrace() }
    }

    /**
     * If a session is currently in progress we should add this match to it.
     */
    private fun addToSession(gameType: Int, session: String, logReference: DocumentReference) {
        FirebaseFirestore
                .getInstance()
                .collection(COLLECTION_SESSIONS)
                .document(session)
                .update(
                        "$FIELD_SESSION_LOGS.$gameType", logReference
                )
                .addOnSuccessListener {
                    Log.v(TAG, "The log with id ${logReference.id} " +
                            "was added to session $session")
                }
                .addOnFailureListener { it.printStackTrace() }
    }

    /**
     * Starts a session in our backend and gets an ID.
     */
    fun startSession(user: String? = null) {
        val db = FirebaseFirestore.getInstance()

        // getting time
        val startTime = Date(System.currentTimeMillis())

        // setting user
        val userName = user ?: DEFAULT_USER
        val userReference = db.collection(COLLECTION_USERS).document(userName)

        // saving everything
        val session = HashMap<String, Any>(3)
        session[FIELD_USER] = userReference
        session[FIELD_START_TIME] = startTime

        db.collection(COLLECTION_SESSIONS)
                .add(session)
                .addOnSuccessListener {
                    sessionId = it.id
                    EventBus.getDefault().post(SessionStarted())
                }
                .addOnFailureListener {
                    it.printStackTrace()
                    SessionStarted(false)
                }
    }

    /**
     * Marks that the session ended. What a world. What a horrible world.
     */
    fun endSession() {
        Log.v(TAG, "Session $sessionId closed.")
        sessionId = null
    }
}

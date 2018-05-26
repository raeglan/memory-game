package de.rafaelmiranda.memory.model

import de.rafaelmiranda.memory.themes.Theme

/**
 * To save all the needed game state stuff(for example the sum)
 */
class GameState(
        /**
         * The id of the chosen game.
         */
        @Theme.ThemeId val gameTypeId: Int) {

    /**
     * The log of each chosen card in this game, this will be saved in a form of pairId.cardNumber in
     * the order it was chosen.
     */
    val gameLog: ArrayList<GameTimeMovesPair> = ArrayList()

    /**
     * The sum of all the numbers our AI said, yeah, I called the Random Thread from Java an AI,
     * deal with it.
     */
    var numberSum = -1

    /**
     * The sum our users gave. Will normally be wrong I guess.
     */
    var numberSumUserAnswer = -1
}

/**
 * To store the timestamp and which move was made at that time.
 * Moves are coded in a way of pair id + pair number like so: "1.2" for the second card of pair
 * number 1.
 */
data class GameTimeMovesPair(val timestamp: Long, val move: String)

package de.rafaelmiranda.memory.model

/**
 * A JSON file describing the game settings. With list of cards to be found.
 *
 * @param version Which version of the game is being played.
 * @param gameSets A list of all the cards being available to use.
 */
data class GameSettings(var version: Int = 0, var gameSets: List<GameSet> = ArrayList())

/**
 * A description for a game set of cards, this has the set's name and where to find the base file
 * a base file should be the name all the cards have with a number appended.
 * <p> If the files are named "foto_set_0, foto_set_1, foto_set_n" then the base file name is
 * "foto_set_"
 */
data class GameSet(var setName: String = "", var baseFileName: String = "", var count: Int = -1)
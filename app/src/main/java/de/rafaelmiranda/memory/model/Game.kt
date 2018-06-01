package de.rafaelmiranda.memory.model

import de.rafaelmiranda.memory.types.Assistants
import de.rafaelmiranda.memory.types.GameType

/**
 * This is instance of active playing game and all its quirks.
 */
class Game {

    /**
     * The board configuration
     */
    lateinit var boardConfiguration: BoardConfiguration

    /**
     * The board arrangment
     */
    lateinit var boardArrangement: BoardArrangement

    /**
     * The selected gameType
     */
    lateinit var gameType: GameType

    /**
     * All the help being used.
     */
    lateinit var assistants: Assistants
}

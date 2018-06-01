package de.rafaelmiranda.memory.model

import de.rafaelmiranda.memory.types.GameType

/**
 * This is instance of active playing game
 *
 * @author sromku
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
}

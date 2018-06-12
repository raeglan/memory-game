package de.rafaelmiranda.memory.model

import de.rafaelmiranda.memory.types.Assistants
import de.rafaelmiranda.memory.types.GameType

/**
 * For coupling the game ids with
 */
data class AssistedGame(@GameType.GameId val gameId: Int, val assistants: Assistants = Assistants())
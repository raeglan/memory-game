package de.rafaelmiranda.memory.events.engine

import de.rafaelmiranda.memory.events.AbstractEvent
import de.rafaelmiranda.memory.events.EventObserver
import de.rafaelmiranda.memory.model.GameState

/**
 * When the 'back to menu' was pressed.
 */
class GameWonEvent(var gameState: GameState) : AbstractEvent() {

    override fun fire(eventObserver: EventObserver) {
        eventObserver.onEvent(this)
    }

}

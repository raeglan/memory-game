package de.rafaelmiranda.memory.events.engine

import de.rafaelmiranda.memory.events.AbstractEvent
import de.rafaelmiranda.memory.events.EventObserver

/**
 * When the 'back to menu' was pressed.
 */
class HidePairCardsEvent(var id1: Int, var id2: Int) : AbstractEvent() {

    override fun fire(eventObserver: EventObserver) {
        eventObserver.onEvent(this)
    }
}

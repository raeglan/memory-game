package de.rafaelmiranda.memory.events.engine

import de.rafaelmiranda.memory.events.AbstractEvent
import de.rafaelmiranda.memory.events.EventObserver

/**
 * When the 'back to menu' was pressed.
 */
class FlipDownCardsEvent : AbstractEvent() {

    override fun fire(eventObserver: EventObserver) {
        eventObserver.onEvent(this)
    }


}

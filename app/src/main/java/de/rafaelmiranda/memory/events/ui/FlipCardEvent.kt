package de.rafaelmiranda.memory.events.ui

import de.rafaelmiranda.memory.events.AbstractEvent
import de.rafaelmiranda.memory.events.EventObserver
import de.rafaelmiranda.memory.model.Card

/**
 * When the 'back to menu' was pressed.
 */
class FlipCardEvent(val card: Card) : AbstractEvent() {

    override fun fire(eventObserver: EventObserver) {
        eventObserver.onEvent(this)
    }

}

package de.rafaelmiranda.memory.events.ui

import de.rafaelmiranda.memory.events.AbstractEvent
import de.rafaelmiranda.memory.events.EventObserver

/**
 * When the 'back to menu' was pressed.
 */
class StartEvent : AbstractEvent() {


    override fun fire(eventObserver: EventObserver) {
        eventObserver.onEvent(this)
    }
}

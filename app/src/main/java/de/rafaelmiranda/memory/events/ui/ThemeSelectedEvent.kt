package de.rafaelmiranda.memory.events.ui

import de.rafaelmiranda.memory.events.AbstractEvent
import de.rafaelmiranda.memory.events.EventObserver
import de.rafaelmiranda.memory.themes.Theme

class ThemeSelectedEvent(val theme: Theme) : AbstractEvent() {


    override fun fire(eventObserver: EventObserver) {
        eventObserver.onEvent(this)
    }


}

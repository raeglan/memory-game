package de.rafaelmiranda.memory.events

abstract class AbstractEvent : Event {

    abstract fun fire(eventObserver: EventObserver)

}

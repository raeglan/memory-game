package de.rafaelmiranda.memory.events

import android.os.Handler
import java.util.*

/**
 * The gateway of all events running in the game from ui to engine components
 * and back.
 *
 */
object EventBus {

    private val mHandler: Handler = Handler()
    private val events = Collections.synchronizedMap(HashMap<String, ArrayList<EventObserver>>())
    private val obj = Any()

    @Synchronized
    fun listen(eventType: String, eventObserver: EventObserver) {
        if (!events.containsKey(eventType)) {
            events[eventType] = ArrayList()
        }

        events[eventType]?.add(eventObserver)
    }

    @Synchronized
    fun unlisten(eventType: String, eventObserver: EventObserver) {
        events[eventType]?.remove(eventObserver)
    }

    fun notify(event: Event) {
        synchronized(obj) {
            val observers = events[event::javaClass.name]
            if (observers != null) {
                for (observer in observers) {
                    val abstractEvent = event as AbstractEvent
                    abstractEvent.fire(observer)
                }
            }
        }
    }

    fun notify(event: Event, delay: Long) {
        mHandler.postDelayed({ this@EventBus.notify(event) }, delay)
    }
}

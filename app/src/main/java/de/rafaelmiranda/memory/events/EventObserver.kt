package de.rafaelmiranda.memory.events

import de.rafaelmiranda.memory.events.engine.FlipDownCardsEvent
import de.rafaelmiranda.memory.events.engine.GameWonEvent
import de.rafaelmiranda.memory.events.engine.HidePairCardsEvent
import de.rafaelmiranda.memory.events.ui.*


interface EventObserver {

    fun onEvent(event: FlipCardEvent)

    fun onEvent(event: DifficultySelectedEvent)

    fun onEvent(event: HidePairCardsEvent)

    fun onEvent(event: FlipDownCardsEvent)

    fun onEvent(event: StartEvent)

    fun onEvent(event: ThemeSelectedEvent)

    fun onEvent(event: GameWonEvent)

    fun onEvent(event: BackGameEvent)

    fun onEvent(event: NextGameEvent)

    fun onEvent(event: ResetBackgroundEvent)

}

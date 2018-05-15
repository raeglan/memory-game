package de.rafaelmiranda.memory.fragments

import android.support.v4.app.Fragment
import de.rafaelmiranda.memory.events.EventObserver
import de.rafaelmiranda.memory.events.engine.FlipDownCardsEvent
import de.rafaelmiranda.memory.events.engine.GameWonEvent
import de.rafaelmiranda.memory.events.engine.HidePairCardsEvent
import de.rafaelmiranda.memory.events.ui.*

open class BaseFragment : Fragment(), EventObserver {

    override fun onEvent(event: FlipCardEvent) {
        throw UnsupportedOperationException()
    }

    override fun onEvent(event: DifficultySelectedEvent) {
        throw UnsupportedOperationException()
    }

    override fun onEvent(event: HidePairCardsEvent) {
        throw UnsupportedOperationException()
    }

    override fun onEvent(event: FlipDownCardsEvent) {
        throw UnsupportedOperationException()
    }

    override fun onEvent(event: StartEvent) {
        throw UnsupportedOperationException()
    }

    override fun onEvent(event: ThemeSelectedEvent) {
        throw UnsupportedOperationException()
    }

    override fun onEvent(event: GameWonEvent) {
        throw UnsupportedOperationException()
    }

    override fun onEvent(event: BackGameEvent) {
        throw UnsupportedOperationException()
    }

    override fun onEvent(event: NextGameEvent) {
        throw UnsupportedOperationException()
    }

    override fun onEvent(event: ResetBackgroundEvent) {
        throw UnsupportedOperationException()
    }

}

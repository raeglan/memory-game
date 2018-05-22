package de.rafaelmiranda.memory.events

import de.rafaelmiranda.memory.model.Card
import de.rafaelmiranda.memory.themes.Theme

/**
 * Just a file containing all the events this wonderful world uses.
 */
class FlipDownCardsEvent

class GameWonEvent

class HidePairCardsEvent(var id1: Int, var id2: Int)

class BackGameEvent

class FlipCardEvent(val card: Card)

class NextGameEvent

class ResetBackgroundEvent

class StartEvent

class ThemeSelectedEvent(val theme: Theme)


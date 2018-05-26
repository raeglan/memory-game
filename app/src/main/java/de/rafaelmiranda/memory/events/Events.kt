package de.rafaelmiranda.memory.events

import de.rafaelmiranda.memory.model.Card
import de.rafaelmiranda.memory.themes.Theme

/**
 * Just a file containing all the events this wonderful world uses.
 */
class FlipDownCardsEvent

class GameWonEvent(@Theme.ThemeId val gameType: Int)

class HidePairCardsEvent(var id1: Int, var id2: Int)

class BackGameEvent

class FlipCardEvent(val card: Card)

class NextEvent

class QuestionsAnsweredEvent(val sumAnswer: Int = -1)

class ResetBackgroundEvent

class StartEvent(val directedGame: Boolean = false)

class GameTypeSelectedEvent(val theme: Theme)

/**
 * Tells the game a session was successfully started.
 */
class SessionStarted(val success: Boolean = true)


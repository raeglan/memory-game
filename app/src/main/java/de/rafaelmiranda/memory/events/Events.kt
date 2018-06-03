package de.rafaelmiranda.memory.events

import de.rafaelmiranda.memory.model.Card
import de.rafaelmiranda.memory.types.Assistants
import de.rafaelmiranda.memory.types.GameType

/**
 * Just a file containing all the events this wonderful world uses.
 */
class FlipDownCardsEvent

class GameWonEvent(@GameType.GameId val gameType: Int)

class HidePairCardsEvent(var id1: Int, var id2: Int)

class BackEvent

class FlipCardEvent(val card: Card)

class FlipAllCardsEvent

class NextEvent

class QuestionsAnsweredEvent(val sumAnswer: Int = -1)

class StartEvent(val directedGame: Boolean = false)

class OpenSettingsEvent

class GameTypeSelectedEvent(val gameType: GameType, val assistants: Assistants? = null)

/**
 * Tells the game a session was successfully started.
 */
class SessionStarted(val success: Boolean = true)


package de.rafaelmiranda.memory.events

import de.rafaelmiranda.memory.model.Card
import de.rafaelmiranda.memory.types.GameType

/**
 * Just a file containing all the events this wonderful world uses.
 */
class FlipDownCardsEvent

class GameWonEvent(@GameType.GameId val gameType: Int)

class HidePairCardsEvent(var id1: Int, var id2: Int)

class BackEvent

class FlipCardEvent(val card: Card)

/**
 * This tells the board view to replay all previous flips in order. The more the merrier, and longer
 * and, of course, more annoying.
 */
class ReplayLastCardsEvent(val lastCards: List<Int>)

class NextEvent

class QuestionsAnsweredEvent(val sumAnswer: Int = -1)

class OpenSettingsEvent

/**
 * Tells the game a session was successfully started.
 */
class SessionStarted(val success: Boolean = true)


package de.rafaelmiranda.memory.common

import android.content.Context
import android.speech.tts.TextToSpeech
import de.rafaelmiranda.memory.MainActivity
import de.rafaelmiranda.memory.engine.Engine
import de.rafaelmiranda.memory.model.GameSettings

object Shared {

    // TODO: CHANGE THIS STUFF, CLASSES SHOULD NEVER BE PASSED LIKE THAT, you dummy.

    lateinit var context: Context
    lateinit var activity: MainActivity// it's fine for this app, but better move to weak reference
    lateinit var engine: Engine
    var tts: TextToSpeech? = null
    lateinit var gameSettings: GameSettings

}

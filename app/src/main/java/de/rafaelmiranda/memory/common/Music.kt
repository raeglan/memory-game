package de.rafaelmiranda.memory.common

import android.media.MediaPlayer
import android.speech.tts.TextToSpeech
import de.rafaelmiranda.memory.R
import java.util.*

object Music {

    var OFF = false

    fun playCorrect() {
        if (!OFF) {
            val mp = MediaPlayer.create(Shared.context, R.raw.correct_answer)
            mp.setOnCompletionListener {
                it.reset()
                it.release()
            }
            mp.start()
        }
    }

    fun playRandomNumber() {
        val random = Random()
        val randomNumber = random.nextInt(10) + 1
        Shared.tts?.speak(randomNumber.toString(), TextToSpeech.QUEUE_ADD, null)
    }

    fun playBackgroundMusic() {
        // TODO
    }

    fun showStar() {
        if (!OFF) {
            val mp = MediaPlayer.create(Shared.context, R.raw.star)
            mp.setOnCompletionListener {
                it.reset()
                it.release()
            }
            mp.start()
        }
    }
}

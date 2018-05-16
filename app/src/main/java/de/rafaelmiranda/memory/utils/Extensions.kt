package de.rafaelmiranda.memory.utils

import android.os.Handler


/**
 * A collection of extension functions for Kotlin. Beware, these are not for noobs and might return
 * a Banana.
 */


/**
 * Inverting the order of post delayed allows kotlin to use its wonderful lambda syntax.
 */
fun Handler.postDelayed(delayMillis: Long, runnable: () -> Unit) {
    this.postDelayed(runnable, delayMillis)
}
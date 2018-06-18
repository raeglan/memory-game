package de.rafaelmiranda.memory.types

import android.os.Parcel
import android.os.Parcelable

/**
 * Define the different kind of assistance a game might have. <br><br>
 * These could be seen as the cheat codes of my memory game. So, prepare your rosebuds, type in
 * 'iddqd', and don't forget to go up, up, down, down, left, right, left, and right.
 */
class Assistants() : Parcelable {

    /**
     * Flips all cards face up for a short time when the button is pressed. Only one OnFlip action
     * is activated by this.
     */
    var replayAllFlips: Boolean = false

    /**
     * Every time a card is flipped it will be zoomed by an annoyingly slow animation.
     */
    var zoomInOnFlip: Boolean = false

    /**
     * If any assist is on.
     */
    fun isAssisted() = !zoomInOnFlip && !replayAllFlips

    constructor(parcel: Parcel) : this() {
        replayAllFlips = parcel.readByte() != 0.toByte()
        zoomInOnFlip = parcel.readByte() != 0.toByte()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeByte(if (replayAllFlips) 1 else 0)
        parcel.writeByte(if (zoomInOnFlip) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Assistants> {
        override fun createFromParcel(parcel: Parcel): Assistants {
            return Assistants(parcel)
        }

        override fun newArray(size: Int): Array<Assistants?> {
            return arrayOfNulls(size)
        }
    }
}
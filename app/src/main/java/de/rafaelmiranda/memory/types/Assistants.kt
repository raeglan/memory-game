package de.rafaelmiranda.memory.types

/**
 * Define the different kind of assistance a game might have.
 * <p>
 *     These could be seen as the cheat codes of my memory game. So, prepare your rosebuds, type in
 *     'iddqd', and don't forget to go up, up, down, down, left, right, left, and right.
 */
class Assistants {
    /**
     * Flips all cards face up for a short time when the button is pressed. Only one OnFlip action
     * is activated by this.
     */
    var flipAllCards: Boolean = false

    /**
     * Every time a card is flipped it will be zoomed by an annoyingly slow animation.
     */
    var zoomInOnFlip: Boolean = false
}
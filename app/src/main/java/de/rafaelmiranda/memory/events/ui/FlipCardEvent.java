package de.rafaelmiranda.memory.events.ui;

import de.rafaelmiranda.memory.events.AbstractEvent;
import de.rafaelmiranda.memory.events.EventObserver;
import de.rafaelmiranda.memory.model.Card;

/**
 * When the 'back to menu' was pressed.
 */
public class FlipCardEvent extends AbstractEvent {

    public static final String TYPE = FlipCardEvent.class.getName();

    public final Card card;

    public FlipCardEvent(Card card) {
        this.card = card;
    }

    @Override
    protected void fire(EventObserver eventObserver) {
        eventObserver.onEvent(this);
    }

    @Override
    public String getType() {
        return TYPE;
    }

}

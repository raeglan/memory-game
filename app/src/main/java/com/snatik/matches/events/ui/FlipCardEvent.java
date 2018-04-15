package com.snatik.matches.events.ui;

import com.snatik.matches.events.AbstractEvent;
import com.snatik.matches.events.EventObserver;
import com.snatik.matches.model.Card;

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

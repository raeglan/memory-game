package com.snatik.matches.events.engine;

import com.snatik.matches.events.AbstractEvent;
import com.snatik.matches.events.EventObserver;
import com.snatik.matches.model.Card;

/**
 * When the 'back to menu' was pressed.
 */
public class HidePairCardsEvent extends AbstractEvent {

	public static final String TYPE = HidePairCardsEvent.class.getName();
	public Card card1;
	public Card card0;

	public HidePairCardsEvent(int id1, int id2) {
		this.id1 = id1;
		this.id2 = id2;
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

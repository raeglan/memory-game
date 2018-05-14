package de.rafaelmiranda.memory.events.ui;

import de.rafaelmiranda.memory.events.AbstractEvent;
import de.rafaelmiranda.memory.events.EventObserver;
import de.rafaelmiranda.memory.themes.Theme;

public class ThemeSelectedEvent extends AbstractEvent {

	public static final String TYPE = ThemeSelectedEvent.class.getName();
	public final Theme theme;

	public ThemeSelectedEvent(Theme theme) {
		this.theme = theme;
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

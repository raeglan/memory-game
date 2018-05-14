package de.rafaelmiranda.memory.events;

import de.rafaelmiranda.memory.events.engine.FlipDownCardsEvent;
import de.rafaelmiranda.memory.events.engine.GameWonEvent;
import de.rafaelmiranda.memory.events.engine.HidePairCardsEvent;
import de.rafaelmiranda.memory.events.ui.BackGameEvent;
import de.rafaelmiranda.memory.events.ui.DifficultySelectedEvent;
import de.rafaelmiranda.memory.events.ui.FlipCardEvent;
import de.rafaelmiranda.memory.events.ui.NextGameEvent;
import de.rafaelmiranda.memory.events.ui.ResetBackgroundEvent;
import de.rafaelmiranda.memory.events.ui.StartEvent;
import de.rafaelmiranda.memory.events.ui.ThemeSelectedEvent;


public interface EventObserver {

	void onEvent(FlipCardEvent event);

	void onEvent(DifficultySelectedEvent event);

	void onEvent(HidePairCardsEvent event);

	void onEvent(FlipDownCardsEvent event);

	void onEvent(StartEvent event);

	void onEvent(ThemeSelectedEvent event);

	void onEvent(GameWonEvent event);

	void onEvent(BackGameEvent event);

	void onEvent(NextGameEvent event);

	void onEvent(ResetBackgroundEvent event);

}

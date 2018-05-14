package de.rafaelmiranda.memory.fragments;

import android.support.v4.app.Fragment;

import de.rafaelmiranda.memory.events.EventObserver;
import de.rafaelmiranda.memory.events.engine.FlipDownCardsEvent;
import de.rafaelmiranda.memory.events.engine.GameWonEvent;
import de.rafaelmiranda.memory.events.engine.HidePairCardsEvent;
import de.rafaelmiranda.memory.events.ui.BackGameEvent;
import de.rafaelmiranda.memory.events.ui.FlipCardEvent;
import de.rafaelmiranda.memory.events.ui.NextGameEvent;
import de.rafaelmiranda.memory.events.ui.ResetBackgroundEvent;
import de.rafaelmiranda.memory.events.ui.ThemeSelectedEvent;
import de.rafaelmiranda.memory.events.ui.DifficultySelectedEvent;
import de.rafaelmiranda.memory.events.ui.StartEvent;

public class BaseFragment extends Fragment implements EventObserver {

    @Override
    public void onEvent(FlipCardEvent event) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void onEvent(DifficultySelectedEvent event) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void onEvent(HidePairCardsEvent event) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void onEvent(FlipDownCardsEvent event) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void onEvent(StartEvent event) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void onEvent(ThemeSelectedEvent event) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void onEvent(GameWonEvent event) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void onEvent(BackGameEvent event) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void onEvent(NextGameEvent event) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void onEvent(ResetBackgroundEvent event) {
        throw new UnsupportedOperationException();
    }

}

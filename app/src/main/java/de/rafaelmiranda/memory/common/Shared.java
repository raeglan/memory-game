package de.rafaelmiranda.memory.common;

import android.content.Context;
import android.speech.tts.TextToSpeech;

import de.rafaelmiranda.memory.MainActivity;
import de.rafaelmiranda.memory.engine.Engine;
import de.rafaelmiranda.memory.events.EventBus;
import de.rafaelmiranda.memory.model.GameSettings;

public class Shared {

    public static Context context;
    public static MainActivity activity; // it's fine for this app, but better move to weak reference
    public static Engine engine;
    public static EventBus eventBus;
    public static TextToSpeech tts;
    public static GameSettings gameSettings;

}

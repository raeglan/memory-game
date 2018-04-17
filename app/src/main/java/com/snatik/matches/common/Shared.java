package com.snatik.matches.common;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.FragmentActivity;

import com.snatik.matches.engine.Engine;
import com.snatik.matches.events.EventBus;
import com.snatik.matches.model.GameSettings;

public class Shared {

    public static Context context;
    public static FragmentActivity activity; // it's fine for this app, but better move to weak reference
    public static Engine engine;
    public static EventBus eventBus;
    public static TextToSpeech tts;
    public static GameSettings gameSettings;

}

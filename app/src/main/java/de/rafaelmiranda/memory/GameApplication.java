package de.rafaelmiranda.memory;

import android.app.Application;

import de.rafaelmiranda.memory.utils.FontLoader;

public class GameApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();
		FontLoader.loadFonts(this);
	}
}

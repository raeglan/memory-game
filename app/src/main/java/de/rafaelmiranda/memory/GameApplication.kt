package de.rafaelmiranda.memory

import android.app.Application

import de.rafaelmiranda.memory.utils.FontLoader

class GameApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        FontLoader.loadFonts(this)
    }
}

package de.rafaelmiranda.memory.ui

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView

import de.rafaelmiranda.memory.R
import de.rafaelmiranda.memory.common.Music
import de.rafaelmiranda.memory.engine.ScreenController
import de.rafaelmiranda.memory.utils.FontLoader

class PopupSettingsView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) : LinearLayout(context, attrs) {

    private val mSoundImage: ImageView
    private val mSoundText: TextView

    init {
        orientation = LinearLayout.VERTICAL
        setBackgroundResource(R.drawable.settings_popup)
        LayoutInflater.from(getContext()).inflate(R.layout.popup_settings_view, this, true)
        mSoundText = findViewById(R.id.sound_off_text)
        val rateView = findViewById<TextView>(R.id.rate_text)
        FontLoader.setTypeface(context, arrayOf(mSoundText, rateView), FontLoader.Font.GROBOLD)
        mSoundImage = findViewById(R.id.sound_image)
        val soundOff = findViewById<View>(R.id.sound_off)
        soundOff.setOnClickListener {
            Music.OFF = !Music.OFF
            setMusicButton()
        }
        val settings = findViewById<View>(R.id.rate)
        settings.setOnClickListener {
            ScreenController.openScreen(ScreenController.Screen.GAME_SETTINGS)
            PopupManager.closePopup()
        }
        setMusicButton()
    }

    private fun setMusicButton() {
        if (Music.OFF) {
            mSoundText.setText(R.string.sound_off)
            mSoundImage.setImageResource(R.drawable.button_music_off)
        } else {
            mSoundText.setText(R.string.sound_on)
            mSoundImage.setImageResource(R.drawable.button_music_on)
        }
    }
}

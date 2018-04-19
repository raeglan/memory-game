package com.snatik.matches.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.snatik.matches.R;
import com.snatik.matches.common.Music;
import com.snatik.matches.engine.ScreenController;
import com.snatik.matches.events.EventBus;
import com.snatik.matches.utils.FontLoader;
import com.snatik.matches.utils.FontLoader.Font;

public class PopupSettingsView extends LinearLayout {

    private ImageView mSoundImage;
    private TextView mSoundText;

    public PopupSettingsView(Context context) {
        this(context, null);
    }

    public PopupSettingsView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOrientation(LinearLayout.VERTICAL);
        setBackgroundResource(R.drawable.settings_popup);
        LayoutInflater.from(getContext()).inflate(R.layout.popup_settings_view, this, true);
        mSoundText = findViewById(R.id.sound_off_text);
        TextView rateView = findViewById(R.id.rate_text);
        FontLoader.setTypeface(context, new TextView[]{mSoundText, rateView}, Font.GROBOLD);
        mSoundImage = findViewById(R.id.sound_image);
        View soundOff = findViewById(R.id.sound_off);
        soundOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Music.OFF = !Music.OFF;
                setMusicButton();
            }
        });
        View settings = findViewById(R.id.rate);
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ScreenController.getInstance().openScreen(ScreenController.Screen.GAME_SETTINGS);
                PopupManager.closePopup();
            }
        });
        setMusicButton();
    }

    private void setMusicButton() {
        if (Music.OFF) {
            mSoundText.setText(R.string.sound_off);
            mSoundImage.setImageResource(R.drawable.button_music_off);
        } else {
            mSoundText.setText(R.string.sound_on);
            mSoundImage.setImageResource(R.drawable.button_music_on);
        }
    }
}

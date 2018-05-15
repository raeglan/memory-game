package de.rafaelmiranda.memory;


import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.ImageView;

import com.google.gson.Gson;

import de.rafaelmiranda.memory.common.Shared;
import de.rafaelmiranda.memory.engine.Engine;
import de.rafaelmiranda.memory.engine.ScreenController;
import de.rafaelmiranda.memory.events.EventBus;
import de.rafaelmiranda.memory.events.ui.BackGameEvent;
import de.rafaelmiranda.memory.model.GameSettings;
import de.rafaelmiranda.memory.ui.PopupManager;
import de.rafaelmiranda.memory.utils.JsonUtils;
import de.rafaelmiranda.memory.utils.Utils;

public class MainActivity extends FragmentActivity implements TextToSpeech.OnInitListener {

    private final static long BLINK_TIME = 150;

    private ImageView mBackgroundImage;
    private View eegBlinkView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Shared.context = getApplicationContext();
        Shared.engine = Engine.getInstance();
        Shared.eventBus = EventBus.getInstance();

        setContentView(R.layout.activity_main);
        mBackgroundImage = findViewById(R.id.background_image);
        eegBlinkView = findViewById(R.id.v_eeg_blink);

        Shared.activity = this;
        Shared.engine.start();
        Shared.engine.setBackgroundImageView(mBackgroundImage);
        Shared.tts = new TextToSpeech(this, this);

        // getting the game settings from the Json file
        Gson gson = new Gson();
        String settingsJson = JsonUtils.INSTANCE
                .getJsonStringFromRaw(this, R.raw.game_settings);
        Shared.gameSettings = gson.fromJson(settingsJson, GameSettings.class);

        // makes sure we have that precious real state by hiding the soft keys
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }

        // set background
        setBackgroundImage();

        // set menu
        ScreenController.getInstance().openScreen(ScreenController.Screen.MENU);
    }

    @Override
    protected void onDestroy() {
        Shared.engine.stop();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (PopupManager.isShown()) {
            PopupManager.closePopup();
            if (ScreenController.getLastScreen() == ScreenController.Screen.GAME) {
                Shared.eventBus.notify(new BackGameEvent());
            }
        } else if (ScreenController.getInstance().onBack()) {
            super.onBackPressed();
        }
    }

    public void blinkEegLight() {
        eegBlinkView.setBackgroundColor(Color.WHITE);

        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    Thread.sleep(BLINK_TIME);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        eegBlinkView.setBackgroundColor(Color.BLACK);
                    }
                });
            }
        }).start();
    }

    private void setBackgroundImage() {
        Bitmap bitmap = Utils.scaleDown(R.drawable.background, Utils.screenWidth(), Utils.screenHeight());
        bitmap = Utils.crop(bitmap, Utils.screenHeight(), Utils.screenWidth());
        bitmap = Utils.downscaleBitmap(bitmap, 2);
        mBackgroundImage.setImageBitmap(bitmap);
    }

    /**
     * called when the TextToSpeech finished initializing.
     *
     * @param status can be SUCCESS or ERROR
     */
    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.ERROR)
            Shared.tts = null;
    }
}

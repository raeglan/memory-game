package de.rafaelmiranda.memory.common;

import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.speech.tts.TextToSpeech;

import de.rafaelmiranda.memory.R;

import java.util.Random;

public class Music {

    public static boolean OFF = false;

    public static void playCorrect() {
        if (!OFF) {
            MediaPlayer mp = MediaPlayer.create(Shared.context, R.raw.correct_answer);
            mp.setOnCompletionListener(new OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mp.reset();
                    mp.release();
                    mp = null;
                }

            });
            mp.start();
        }
    }

    public static void playRandomNumber() {
        Random random = new Random();
        int randomNumber = random.nextInt(10) + 1;
        if (Shared.tts != null)
            Shared.tts.speak(String.valueOf(randomNumber), TextToSpeech.QUEUE_ADD, null);
    }

    public static void playBackgroundMusic() {
        // TODO
    }

    public static void showStar() {
        if (!OFF) {
            MediaPlayer mp = MediaPlayer.create(Shared.context, R.raw.star);
            mp.setOnCompletionListener(new OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mp.reset();
                    mp.release();
                    mp = null;
                }

            });
            mp.start();
        }
    }
}

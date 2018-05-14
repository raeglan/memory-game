package de.rafaelmiranda.memory.fragments;

import android.animation.AnimatorSet;
import android.animation.AnimatorSet.Builder;
import android.animation.ObjectAnimator;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;
import android.widget.TextView;

import de.rafaelmiranda.memory.R;
import de.rafaelmiranda.memory.common.Memory;
import de.rafaelmiranda.memory.common.Shared;
import de.rafaelmiranda.memory.events.ui.DifficultySelectedEvent;
import de.rafaelmiranda.memory.themes.Theme;
import de.rafaelmiranda.memory.ui.DifficultyView;

public class DifficultySelectFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = LayoutInflater.from(Shared.context).inflate(R.layout.difficulty_select_fragment, container, false);
        Theme theme = Shared.engine.getSelectedTheme();

        // setting the images
        setDifficultyForTheme(view, theme);

        return view;

    }

    private void setDifficultyForTheme(View view, Theme theme) {

        // getting the views
        DifficultyView difficulty1 = view.findViewById(R.id.select_difficulty_1);
        DifficultyView difficulty2 = view.findViewById(R.id.select_difficulty_2);
        DifficultyView difficulty3 = view.findViewById(R.id.select_difficulty_3);
        DifficultyView difficulty4 = view.findViewById(R.id.select_difficulty_4);
        DifficultyView difficulty5 = view.findViewById(R.id.select_difficulty_5);
        DifficultyView difficulty6 = view.findViewById(R.id.select_difficulty_6);

        // setting the click listeners
        setOnClick(difficulty1, 1);
        setOnClick(difficulty2, 2);
        setOnClick(difficulty3, 3);
        setOnClick(difficulty4, 4);
        setOnClick(difficulty5, 5);
        setOnClick(difficulty6, 6);

        // setting images
        if (theme.id == Theme.ID_ANIMAL_VISUAL) {
            difficulty1.setImage(R.drawable.visual_theme_blur);
            difficulty2.setImage(R.drawable.visual_theme_retinopathy);
            difficulty3.setVisibility(View.INVISIBLE);
            difficulty4.setVisibility(View.INVISIBLE);
            difficulty5.setVisibility(View.INVISIBLE);
            difficulty6.setVisibility(View.INVISIBLE);
        } else {
            difficulty1.setDifficulty(1, Memory.getHighStars(theme.id, 1));

            difficulty2.setDifficulty(2, Memory.getHighStars(theme.id, 2));

            difficulty3.setDifficulty(3, Memory.getHighStars(theme.id, 3));

            difficulty4.setDifficulty(4, Memory.getHighStars(theme.id, 4));

            difficulty5.setDifficulty(5, Memory.getHighStars(theme.id, 5));

            difficulty6.setDifficulty(6, Memory.getHighStars(theme.id, 6));
        }

        animate(difficulty1, difficulty2, difficulty3, difficulty4, difficulty5, difficulty6);

        // now for the text
        Typeface type = Typeface.createFromAsset(Shared.context.getAssets(), "fonts/grobold.ttf");

        // getting views
        TextView text1 = view.findViewById(R.id.time_difficulty_1);
        TextView text2 = view.findViewById(R.id.time_difficulty_2);
        TextView text3 = view.findViewById(R.id.time_difficulty_3);
        TextView text4 = view.findViewById(R.id.time_difficulty_4);
        TextView text5 = view.findViewById(R.id.time_difficulty_5);
        TextView text6 = view.findViewById(R.id.time_difficulty_6);

        // if we are on the visual mode, no need to display a time.
        if (theme.id == Theme.ID_ANIMAL_VISUAL) {
            text1.setVisibility(View.INVISIBLE);
            text2.setVisibility(View.INVISIBLE);
            text3.setVisibility(View.INVISIBLE);
            text4.setVisibility(View.INVISIBLE);
            text5.setVisibility(View.INVISIBLE);
            text6.setVisibility(View.INVISIBLE);
        }

        text1.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
        text1.setTypeface(type);
        text1.setText(getBestTimeForStage(theme.id, 1));

        text2.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
        text2.setTypeface(type);
        text2.setText(getBestTimeForStage(theme.id, 2));

        text3.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
        text3.setTypeface(type);
        text3.setText(getBestTimeForStage(theme.id, 3));

        text4.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
        text4.setTypeface(type);
        text4.setText(getBestTimeForStage(theme.id, 4));

        text5.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
        text5.setTypeface(type);
        text5.setText(getBestTimeForStage(theme.id, 5));

        text6.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
        text6.setTypeface(type);
        text6.setText(getBestTimeForStage(theme.id, 6));
    }

    private String getBestTimeForStage(int theme, int difficulty) {
        int bestTime = Memory.getBestTime(theme, difficulty);
        if (bestTime != -1) {
            int minutes = (bestTime % 3600) / 60;
            int seconds = (bestTime) % 60;
            String result = String.format("BEST : %02d:%02d", minutes, seconds);
            return result;
        } else {
            String result = "BEST : -";
            return result;
        }
    }

    private void animate(View... view) {
        AnimatorSet animatorSet = new AnimatorSet();
        Builder builder = animatorSet.play(new AnimatorSet());
        for (int i = 0; i < view.length; i++) {
            ObjectAnimator scaleX = ObjectAnimator.ofFloat(view[i], "scaleX", 0.8f, 1f);
            ObjectAnimator scaleY = ObjectAnimator.ofFloat(view[i], "scaleY", 0.8f, 1f);
            builder.with(scaleX).with(scaleY);
        }
        animatorSet.setDuration(500);
        animatorSet.setInterpolator(new BounceInterpolator());
        animatorSet.start();
    }

    private void setOnClick(View view, final int difficulty) {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Shared.eventBus.notify(new DifficultySelectedEvent(difficulty));
            }
        });
    }


}

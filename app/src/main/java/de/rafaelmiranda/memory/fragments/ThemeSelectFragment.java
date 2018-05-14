package de.rafaelmiranda.memory.fragments;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;

import de.rafaelmiranda.memory.R;
import de.rafaelmiranda.memory.common.Memory;
import de.rafaelmiranda.memory.common.Shared;
import de.rafaelmiranda.memory.events.ui.ThemeSelectedEvent;
import de.rafaelmiranda.memory.themes.Theme;
import de.rafaelmiranda.memory.themes.Themes;

import java.util.Locale;

public class ThemeSelectFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = LayoutInflater.from(Shared.context).inflate(R.layout.theme_select_fragment, container, false);
        View normal = view.findViewById(R.id.theme_animals_container);
        View auditory = view.findViewById(R.id.theme_monsters_container);
        View visual = view.findViewById(R.id.theme_emoji_container);

        final Theme themeNormal = Themes.createTheme(Theme.ID_ANIMAL);
        final Theme themeAuditory = Themes.createTheme(Theme.ID_ANIMAL_AUDITORY);
        final Theme themeVisual = Themes.createTheme(Theme.ID_ANIMAL_VISUAL);

        normal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Shared.eventBus.notify(new ThemeSelectedEvent(themeNormal));
            }
        });

        auditory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Shared.eventBus.notify(new ThemeSelectedEvent(themeAuditory));
            }
        });

        visual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Shared.eventBus.notify(new ThemeSelectedEvent(themeVisual));
            }
        });

        /*
         * Improve performance first!!!
         */
        animateShow(normal);
        animateShow(auditory);
        animateShow(visual);

        return view;
    }

    private void animateShow(View view) {
        ObjectAnimator animatorScaleX = ObjectAnimator.ofFloat(view, "scaleX", 0.5f, 1f);
        ObjectAnimator animatorScaleY = ObjectAnimator.ofFloat(view, "scaleY", 0.5f, 1f);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(300);
        animatorSet.playTogether(animatorScaleX, animatorScaleY);
        animatorSet.setInterpolator(new DecelerateInterpolator(2));
        view.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        animatorSet.start();
    }

    /**
     * For now I don't really need to show stars here, I may add it later.
     */
    private void setStars(ImageView imageView, Theme theme, String type) {
        int sum = 0;
        for (int difficulty = 1; difficulty <= 6; difficulty++) {
            sum += Memory.getHighStars(theme.id, difficulty);
        }
        int num = sum / 6;
        if (num != 0) {
            String drawableResourceName = String.format(Locale.US, type + "_theme_star_%d", num);
            int drawableResourceId = Shared.context.getResources().getIdentifier(drawableResourceName, "drawable", Shared.context.getPackageName());
            imageView.setImageResource(drawableResourceId);
        }
    }
}

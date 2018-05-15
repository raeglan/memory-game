package de.rafaelmiranda.memory.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.graphics.Color;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

import de.rafaelmiranda.memory.R;
import de.rafaelmiranda.memory.common.Shared;
import de.rafaelmiranda.memory.model.GameState;

public class PopupManager {

    public static void showPopupSettings() {
        RelativeLayout popupContainer = Shared.INSTANCE.getActivity().findViewById(R.id.popup_container);
        popupContainer.removeAllViews();

        // background
        ImageView imageView = new ImageView(Shared.INSTANCE.getContext());
        imageView.setBackgroundColor(Color.parseColor("#88555555"));
        imageView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        imageView.setClickable(true);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupManager.closePopup();
            }
        });
        popupContainer.addView(imageView);

        // popup
        PopupSettingsView popupSettingsView = new PopupSettingsView(Shared.INSTANCE.getContext());
        int width = Shared.INSTANCE.getContext().getResources().getDimensionPixelSize(R.dimen.popup_settings_width);
        int height = Shared.INSTANCE.getContext().getResources().getDimensionPixelSize(R.dimen.popup_settings_height);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(width, height);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        popupContainer.addView(popupSettingsView, params);

        // animate all together
        ObjectAnimator scaleXAnimator = ObjectAnimator.ofFloat(popupSettingsView,
                "scaleX", 0f, 1f);
        ObjectAnimator scaleYAnimator = ObjectAnimator.ofFloat(popupSettingsView,
                "scaleY", 0f, 1f);
        ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(imageView,
                "alpha", 0f, 1f);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(scaleXAnimator, scaleYAnimator, alphaAnimator);
        animatorSet.setDuration(500);
        animatorSet.setInterpolator(new DecelerateInterpolator(2));
        animatorSet.start();
    }

    public static void showPopupWon(GameState gameState) {
        RelativeLayout popupContainer = Shared.INSTANCE.getActivity().findViewById(R.id.popup_container);
        popupContainer.removeAllViews();

        // popup
        PopupWonView popupWonView = new PopupWonView(Shared.INSTANCE.getContext());
        popupWonView.setGameState(gameState);
        int width = Shared.INSTANCE.getContext().getResources().getDimensionPixelSize(R.dimen.popup_won_width);
        int height = Shared.INSTANCE.getContext().getResources().getDimensionPixelSize(R.dimen.popup_won_height);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(width, height);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        popupContainer.addView(popupWonView, params);

        // animate all together
        ObjectAnimator scaleXAnimator = ObjectAnimator.ofFloat(popupWonView, "scaleX", 0f, 1f);
        ObjectAnimator scaleYAnimator = ObjectAnimator.ofFloat(popupWonView, "scaleY", 0f, 1f);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(scaleXAnimator, scaleYAnimator);
        animatorSet.setDuration(500);
        animatorSet.setInterpolator(new DecelerateInterpolator(2));
        popupWonView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        animatorSet.start();
    }

    public static void closePopup() {
        final RelativeLayout popupContainer = Shared.INSTANCE.getActivity().findViewById(R.id.popup_container);
        int childCount = popupContainer.getChildCount();
        if (childCount > 0) {
            View background = null;
            View viewPopup;
            if (childCount == 1) {
                viewPopup = popupContainer.getChildAt(0);
            } else {
                background = popupContainer.getChildAt(0);
                viewPopup = popupContainer.getChildAt(1);
            }

            AnimatorSet animatorSet = new AnimatorSet();
            ObjectAnimator scaleXAnimator = ObjectAnimator.ofFloat(viewPopup, "scaleX", 0f);
            ObjectAnimator scaleYAnimator = ObjectAnimator.ofFloat(viewPopup, "scaleY", 0f);
            if (childCount > 1) {
                ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(background, "alpha", 0f);
                animatorSet.playTogether(scaleXAnimator, scaleYAnimator, alphaAnimator);
            } else {
                animatorSet.playTogether(scaleXAnimator, scaleYAnimator);
            }
            animatorSet.setDuration(300);
            animatorSet.setInterpolator(new AccelerateInterpolator(2));
            animatorSet.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    popupContainer.removeAllViews();
                }
            });
            animatorSet.start();
        }
    }

    public static boolean isShown() {
        RelativeLayout popupContainer = Shared.INSTANCE.getActivity().findViewById(R.id.popup_container);
        return popupContainer.getChildCount() > 0;
    }
}

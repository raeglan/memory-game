package de.rafaelmiranda.memory.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;
import android.widget.LinearLayout;

import de.rafaelmiranda.memory.R;
import de.rafaelmiranda.memory.common.Shared;
import de.rafaelmiranda.memory.events.ui.FlipCardEvent;
import de.rafaelmiranda.memory.model.BoardArrangement;
import de.rafaelmiranda.memory.model.BoardConfiguration;
import de.rafaelmiranda.memory.model.Card;
import de.rafaelmiranda.memory.model.Game;
import de.rafaelmiranda.memory.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class BoardView extends LinearLayout {

    private LinearLayout.LayoutParams mRowLayoutParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
    private LinearLayout.LayoutParams mTileLayoutParams;
    private int mScreenWidth;
    private int mScreenHeight;
    private BoardConfiguration mBoardConfiguration;
    private BoardArrangement mBoardArrangement;
    private SparseArray<TileView> mViewReference;
    private List<Integer> flippedUp = new ArrayList<>();
    private boolean mLocked = false;
    private int mSize;

    public BoardView(Context context) {
        this(context, null);
    }

    public BoardView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        setOrientation(LinearLayout.VERTICAL);
        setGravity(Gravity.CENTER);
        int margin = getResources().getDimensionPixelSize(R.dimen.margine_top);
        int padding = getResources().getDimensionPixelSize(R.dimen.board_padding);
        mScreenHeight = getResources().getDisplayMetrics().heightPixels - margin - padding * 2;
        mScreenWidth = getResources().getDisplayMetrics().widthPixels - padding * 2 - Utils.px(20);
        mViewReference = new SparseArray<>();
        setClipToPadding(false);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }

    public static BoardView fromXml(Context context, ViewGroup parent) {
        return (BoardView) LayoutInflater.from(context).inflate(R.layout.board_view, parent, false);
    }

    public void setBoard(Game game) {
        mBoardConfiguration = game.boardConfiguration;
        mBoardArrangement = game.boardArrangement;
        // calc prefered tiles in width and height
        int singleMargin = getResources().getDimensionPixelSize(R.dimen.card_margin);
        float density = getResources().getDisplayMetrics().density;
        singleMargin = Math.max((int) (1 * density), (int) (singleMargin - mBoardConfiguration.getDifficulty() * 2 * density));
        int sumMargin = 0;
        for (int row = 0; row < mBoardConfiguration.getNumRows(); row++) {
            sumMargin += singleMargin * 2;
        }
        int tilesHeight = (mScreenHeight - sumMargin) / mBoardConfiguration.getNumRows();
        int tilesWidth = (mScreenWidth - sumMargin) / mBoardConfiguration.getNumTilesInRow();
        mSize = Math.min(tilesHeight, tilesWidth);

        mTileLayoutParams = new LinearLayout.LayoutParams(mSize, mSize);
        mTileLayoutParams.setMargins(singleMargin, singleMargin, singleMargin, singleMargin);

        // build the ui
        buildBoard();
    }

    /**
     * Build the board
     */
    private void buildBoard() {

        for (int row = 0; row < mBoardConfiguration.getNumRows(); row++) {
            // add row
            addBoardRow(row);
        }

        setClipChildren(false);
    }

    private void addBoardRow(int rowNum) {

        LinearLayout linearLayout = new LinearLayout(getContext());
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout.setGravity(Gravity.CENTER);

        for (int tile = 0; tile < mBoardConfiguration.getNumTilesInRow(); tile++) {
            addTile(rowNum * mBoardConfiguration.getNumTilesInRow() + tile, linearLayout);
        }

        // add to this view
        addView(linearLayout, mRowLayoutParams);
        linearLayout.setClipChildren(false);
    }

    private void addTile(final int placementId, ViewGroup parent) {
        final TileView tileView = TileView.fromXml(getContext(), parent);
        tileView.setLayoutParams(mTileLayoutParams);
        parent.addView(tileView);
        parent.setClipChildren(false);
        mViewReference.put(placementId, tileView);

        if (placementId < mBoardConfiguration.getNumTiles()) {
            new AsyncTask<Void, Void, Bitmap>() {

                @Override
                protected Bitmap doInBackground(Void... params) {
                    return mBoardArrangement.getTileBitmap(placementId, mSize);
                }

                @Override
                protected void onPostExecute(Bitmap result) {
                    tileView.setTileImage(result);
                }
            }.execute();

            tileView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (!mLocked && tileView.isFlippedDown()) {
                        tileView.flipUp();
                        flippedUp.add(placementId);
                        if (flippedUp.size() == 2) {
                            mLocked = true;
                        }
                        Card chosenCard = mBoardArrangement.cards.get(placementId);
                        Shared.eventBus.notify(new FlipCardEvent(chosenCard));
                    }
                }
            });

            ObjectAnimator scaleXAnimator = ObjectAnimator.ofFloat(tileView, "scaleX", 0.8f, 1f);
            scaleXAnimator.setInterpolator(new BounceInterpolator());
            ObjectAnimator scaleYAnimator = ObjectAnimator.ofFloat(tileView, "scaleY", 0.8f, 1f);
            scaleYAnimator.setInterpolator(new BounceInterpolator());
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.playTogether(scaleXAnimator, scaleYAnimator);
            animatorSet.setDuration(500);
            tileView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
            animatorSet.start();
        } else {
            tileView.setVisibility(View.INVISIBLE);
        }
    }

    public void flipDownAll() {
        for (Integer id : flippedUp) {
            mViewReference.get(id).flipDown();
        }
        flippedUp.clear();
        mLocked = false;
    }

    public void hideCards(int id1, int id2) {
        animateHide(mViewReference.get(id1));
        animateHide(mViewReference.get(id2));
        flippedUp.clear();
        mLocked = false;
    }

    protected void animateHide(final TileView v) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(v, "alpha", 0f);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                v.setLayerType(View.LAYER_TYPE_NONE, null);
                v.setVisibility(View.INVISIBLE);
            }
        });
        animator.setDuration(100);
        v.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        animator.start();
    }

}

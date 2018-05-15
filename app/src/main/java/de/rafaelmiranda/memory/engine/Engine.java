package de.rafaelmiranda.memory.engine;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.SparseArray;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.rafaelmiranda.memory.R;
import de.rafaelmiranda.memory.common.Memory;
import de.rafaelmiranda.memory.common.MemoryDb;
import de.rafaelmiranda.memory.common.Music;
import de.rafaelmiranda.memory.common.Shared;
import de.rafaelmiranda.memory.events.EventObserverAdapter;
import de.rafaelmiranda.memory.events.engine.FlipDownCardsEvent;
import de.rafaelmiranda.memory.events.engine.GameWonEvent;
import de.rafaelmiranda.memory.events.engine.HidePairCardsEvent;
import de.rafaelmiranda.memory.events.ui.BackGameEvent;
import de.rafaelmiranda.memory.events.ui.DifficultySelectedEvent;
import de.rafaelmiranda.memory.events.ui.FlipCardEvent;
import de.rafaelmiranda.memory.events.ui.NextGameEvent;
import de.rafaelmiranda.memory.events.ui.ResetBackgroundEvent;
import de.rafaelmiranda.memory.events.ui.StartEvent;
import de.rafaelmiranda.memory.events.ui.ThemeSelectedEvent;
import de.rafaelmiranda.memory.model.BoardArrangement;
import de.rafaelmiranda.memory.model.BoardConfiguration;
import de.rafaelmiranda.memory.model.Card;
import de.rafaelmiranda.memory.model.Game;
import de.rafaelmiranda.memory.model.GameState;
import de.rafaelmiranda.memory.model.Impairment;
import de.rafaelmiranda.memory.themes.Theme;
import de.rafaelmiranda.memory.themes.Themes;
import de.rafaelmiranda.memory.ui.PopupManager;
import de.rafaelmiranda.memory.utils.Clock;
import de.rafaelmiranda.memory.utils.Utils;
import kotlin.Pair;

public class Engine extends EventObserverAdapter {

    private static Engine mInstance = null;
    private Game mPlayingGame = null;
    private Card mFlippedCard = null;
    private int mToFlip = -1;
    private ScreenController mScreenController;
    private Theme mSelectedTheme;
    private ImageView mBackgroundImage;
    private Handler mHandler;

    /**
     * The log of each chosen card in this game, this will be saved in a form of pairId.cardNumber in
     * the order it was chosen.
     * The first of the pair is a timestamp of when the move was made, and the second the move
     * itself
     */
    private final ArrayList<Pair<Long, String>> gameLog = new ArrayList<>();

    private Engine() {
        mScreenController = ScreenController.getInstance();
        mHandler = new Handler();
    }

    public static Engine getInstance() {
        if (mInstance == null) {
            mInstance = new Engine();
        }
        return mInstance;
    }

    public void start() {
        Shared.eventBus.listen(DifficultySelectedEvent.TYPE, this);
        Shared.eventBus.listen(FlipCardEvent.TYPE, this);
        Shared.eventBus.listen(StartEvent.TYPE, this);
        Shared.eventBus.listen(ThemeSelectedEvent.TYPE, this);
        Shared.eventBus.listen(BackGameEvent.TYPE, this);
        Shared.eventBus.listen(NextGameEvent.TYPE, this);
        Shared.eventBus.listen(ResetBackgroundEvent.TYPE, this);
    }

    public void stop() {
        mPlayingGame = null;
        mBackgroundImage.setImageDrawable(null);
        mBackgroundImage = null;
        mHandler.removeCallbacksAndMessages(null);
        mHandler = null;

        Shared.eventBus.unlisten(DifficultySelectedEvent.TYPE, this);
        Shared.eventBus.unlisten(FlipCardEvent.TYPE, this);
        Shared.eventBus.unlisten(StartEvent.TYPE, this);
        Shared.eventBus.unlisten(ThemeSelectedEvent.TYPE, this);
        Shared.eventBus.unlisten(BackGameEvent.TYPE, this);
        Shared.eventBus.unlisten(NextGameEvent.TYPE, this);
        Shared.eventBus.unlisten(ResetBackgroundEvent.TYPE, this);

        mInstance = null;
    }

    @Override
    public void onEvent(ResetBackgroundEvent event) {
        Drawable drawable = mBackgroundImage.getDrawable();
        if (drawable != null) {
            ((TransitionDrawable) drawable).reverseTransition(2000);
        } else {
            new AsyncTask<Void, Void, Bitmap>() {

                @Override
                protected Bitmap doInBackground(Void... params) {
                    Bitmap bitmap = Utils.scaleDown(R.drawable.background, Utils.screenWidth(), Utils.screenHeight());
                    return bitmap;
                }

                protected void onPostExecute(Bitmap bitmap) {
                    mBackgroundImage.setImageBitmap(bitmap);
                }

                ;

            }.execute();
        }
    }

    @Override
    public void onEvent(StartEvent event) {
        mScreenController.openScreen(ScreenController.Screen.THEME_SELECT);
    }

    @Override
    public void onEvent(NextGameEvent event) {
        PopupManager.closePopup();
        int difficulty = mPlayingGame.boardConfiguration.getDifficulty();
        if (mPlayingGame.gameState.achievedStars == 3 && difficulty < 6) {
            difficulty++;
        }
        Shared.eventBus.notify(new DifficultySelectedEvent(difficulty));
    }

    @Override
    public void onEvent(BackGameEvent event) {
        PopupManager.closePopup();
        mScreenController.openScreen(ScreenController.Screen.DIFFICULTY);
    }

    @Override
    public void onEvent(ThemeSelectedEvent event) {
        mSelectedTheme = event.theme;
        mScreenController.openScreen(ScreenController.Screen.DIFFICULTY);
        AsyncTask<Void, Void, TransitionDrawable> task = new AsyncTask<Void, Void, TransitionDrawable>() {

            @Override
            protected TransitionDrawable doInBackground(Void... params) {
                Bitmap bitmap = Utils.scaleDown(R.drawable.background, Utils.screenWidth(), Utils.screenHeight());
                Bitmap backgroundImage = Themes.getBackgroundImage(mSelectedTheme);
                backgroundImage = Utils.crop(backgroundImage, Utils.screenHeight(), Utils.screenWidth());
                Drawable backgrounds[] = new Drawable[2];
                backgrounds[0] = new BitmapDrawable(Shared.context.getResources(), bitmap);
                backgrounds[1] = new BitmapDrawable(Shared.context.getResources(), backgroundImage);
                TransitionDrawable crossfader = new TransitionDrawable(backgrounds);
                return crossfader;
            }

            @Override
            protected void onPostExecute(TransitionDrawable result) {
                super.onPostExecute(result);
                mBackgroundImage.setImageDrawable(result);
                result.startTransition(2000);
            }
        };
        task.execute();
    }

    /**
     * This is the event that notifies a game starting.
     *
     * @param event the difficulty event with the level chosen.
     */
    @Override
    public void onEvent(DifficultySelectedEvent event) {
        mFlippedCard = null;
        mPlayingGame = new Game();
        mPlayingGame.boardConfiguration = new BoardConfiguration(event.difficulty, mSelectedTheme.id);
        mPlayingGame.theme = mSelectedTheme;
        mToFlip = mPlayingGame.boardConfiguration.getNumTiles();

        // arrange board
        arrangeBoard();

        // start the screen
        mScreenController.openScreen(ScreenController.Screen.GAME);
    }

    private void arrangeBoard() {
        // setting up the necessary stuff
        BoardConfiguration boardConfiguration = mPlayingGame.boardConfiguration;
        BoardArrangement boardArrangement = new BoardArrangement();
        // the game log should be empty starting a new game.
        gameLog.clear();

        // build pairs
        // result {0,1,2,...n} // n-number of tiles
        List<Integer> placementIds = new ArrayList<>();
        for (int i = 0; i < boardConfiguration.getNumTiles(); i++) {
            placementIds.add(i);
        }
        // shuffle
        // result {4,10,2,39,...}
        Collections.shuffle(placementIds);

        // place the board
        // For a scientific study, a random game set is not a good idea.
        // Instead we take the set first and then randomize the position.
        // Psych! This we really do want some randomness in there.
        List<String> tileImageUrls = mPlayingGame.theme.tileImageUrls;
        Collections.shuffle(tileImageUrls);

        boardArrangement.cards = new SparseArray<>();
        boardArrangement.tileUrls = new SparseArray<>();

        int pairId = 0;
        for (int i = 0; i + 1 < placementIds.size(); i += 2) {

            int firstCardPlacement = placementIds.get(i);
            int secondCardPlacement = placementIds.get(i + 1);

            Card firstCard = new Card(firstCardPlacement, pairId, 1);
            boardArrangement.cards.put(firstCardPlacement, firstCard);
            boardArrangement.tileUrls.put(firstCardPlacement, tileImageUrls.get(pairId));

            Card secondCard = new Card(secondCardPlacement, pairId, 2);
            boardArrangement.cards.put(secondCardPlacement, secondCard);
            boardArrangement.tileUrls.put(secondCardPlacement, tileImageUrls.get(pairId));

            pairId++;
        }

        mPlayingGame.boardArrangement = boardArrangement;
    }

    @Override
    public void onEvent(FlipCardEvent event) {
        Card card = event.card;

        Shared.activity.blinkEegLight();

        // adding the event to the log
        long timestamp = System.currentTimeMillis();
        String move = card.getPairId() + "." + card.getCardNumber();
        gameLog.add(new Pair<>(timestamp, move));

        // if auditory obstacles were chosen then say a random number out loud
        if (mPlayingGame.boardConfiguration.getImpairment() == Impairment.AUDITORY_SUM_10) {
            Music.playRandomNumber();
        }

        if (mFlippedCard == null) {
            mFlippedCard = card;
        } else {
            if (mPlayingGame.boardArrangement.isPair(mFlippedCard, card)) {

                // send event - hide id1, id2
                Shared.eventBus.notify(new HidePairCardsEvent(mFlippedCard.getPlacementId(),
                        card.getPlacementId()), 1000);

                // play music
                mHandler.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        Music.playCorrect();
                    }
                }, 1000);
                mToFlip -= 2;
                if (mToFlip == 0) {
                    int passedSeconds = (int) (Clock.getInstance().getPassedTime() / 1000);
                    Clock.getInstance().pause();
                    int totalTime = mPlayingGame.boardConfiguration.getTime();
                    GameState gameState = new GameState();
                    mPlayingGame.gameState = gameState;
                    // remained seconds
                    gameState.remainedSeconds = totalTime - passedSeconds;
                    gameState.passedSeconds = passedSeconds;

                    // calc stars
                    if (passedSeconds <= totalTime / 2) {
                        gameState.achievedStars = 3;
                    } else if (passedSeconds <= totalTime - totalTime / 5) {
                        gameState.achievedStars = 2;
                    } else if (passedSeconds < totalTime) {
                        gameState.achievedStars = 1;
                    } else {
                        gameState.achievedStars = 0;
                    }

                    // calc score
                    gameState.achievedScore = mPlayingGame.boardConfiguration.getDifficulty() * gameState.remainedSeconds * mPlayingGame.theme.id;

                    // save to memory
                    Memory.save(mPlayingGame.theme.id, mPlayingGame.boardConfiguration.getDifficulty(), gameState.achievedStars);
                    Memory.saveTime(mPlayingGame.theme.id, mPlayingGame.boardConfiguration.getDifficulty(), gameState.passedSeconds);
                    // and save the logs as well
                    MemoryDb.INSTANCE.addGameLog(mPlayingGame.theme.id,
                            mPlayingGame.boardConfiguration.getDifficulty(), gameLog);

                    Shared.eventBus.notify(new GameWonEvent(gameState), 1200);
                }
            } else {
                // Log.i("my_tag", "Flip: all down");
                // send event - flip all down
                Shared.eventBus.notify(new FlipDownCardsEvent(), 1000);
            }
            mFlippedCard = null;
        }
    }

    public Game getActiveGame() {
        return mPlayingGame;
    }

    public Theme getSelectedTheme() {
        return mSelectedTheme;
    }

    public void setBackgroundImageView(ImageView backgroundImage) {
        mBackgroundImage = backgroundImage;
    }
}

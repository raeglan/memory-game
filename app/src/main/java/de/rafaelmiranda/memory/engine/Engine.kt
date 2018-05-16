package de.rafaelmiranda.memory.engine

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.TransitionDrawable
import android.os.AsyncTask
import android.os.Handler
import android.util.SparseArray
import android.widget.ImageView
import de.rafaelmiranda.memory.R
import de.rafaelmiranda.memory.common.Memory
import de.rafaelmiranda.memory.common.MemoryDb
import de.rafaelmiranda.memory.common.Music
import de.rafaelmiranda.memory.common.Shared
import de.rafaelmiranda.memory.events.*
import de.rafaelmiranda.memory.model.*
import de.rafaelmiranda.memory.themes.Theme
import de.rafaelmiranda.memory.themes.Themes
import de.rafaelmiranda.memory.ui.PopupManager
import de.rafaelmiranda.memory.utils.Clock
import de.rafaelmiranda.memory.utils.Utils
import de.rafaelmiranda.memory.utils.postDelayed
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.util.*

@SuppressLint("StaticFieldLeak")
object Engine {
    var activeGame: Game? = null
        private set
    private var mFlippedCard: Card? = null
    private var mToFlip = -1
    var selectedTheme: Theme? = null
        private set
    private var mBackgroundImage: ImageView? = null
    private var mHandler: Handler? = null

    /**
     * The log of each chosen card in this game, this will be saved in a form of pairId.cardNumber in
     * the order it was chosen.
     * The first of the pair is a timestamp of when the move was made, and the second the move
     * itself
     */
    private val gameLog = ArrayList<Pair<Long, String>>()

    init {
        mHandler = Handler()
    }

    fun start() {
        EventBus.getDefault().register(this)
    }

    fun stop() {
        EventBus.getDefault().unregister(this)
        activeGame = null
        mBackgroundImage!!.setImageDrawable(null)
        mBackgroundImage = null
        mHandler!!.removeCallbacksAndMessages(null)
        mHandler = null
    }

    @Subscribe
    fun onResetBackgroundEvent(event: ResetBackgroundEvent) {
        val drawable = mBackgroundImage!!.drawable
        if (drawable != null) {
            (drawable as TransitionDrawable).reverseTransition(2000)
        } else {
            object : AsyncTask<Void, Void, Bitmap>() {

                override fun doInBackground(vararg params: Void): Bitmap {
                    return Utils.scaleDown(R.drawable.background, Utils.screenWidth(), Utils.screenHeight())
                }

                override fun onPostExecute(bitmap: Bitmap) {
                    mBackgroundImage!!.setImageBitmap(bitmap)
                }

            }.execute()
        }
    }

    @Subscribe
    fun onStartEvent(event: StartEvent) {
        ScreenController.openScreen(ScreenController.Screen.THEME_SELECT)
    }

    @Subscribe
    fun onNextGameEvent(event: NextGameEvent) {
        PopupManager.closePopup()
        var difficulty = activeGame!!.boardConfiguration.difficulty
        if (activeGame!!.gameState?.achievedStars == 3 && difficulty < 6) {
            difficulty++
        }
        EventBus.getDefault().post(DifficultySelectedEvent(difficulty))
    }

    @Subscribe
    fun onBackGameEvent(event: BackGameEvent) {
        PopupManager.closePopup()
        ScreenController.openScreen(ScreenController.Screen.DIFFICULTY)
    }

    @Subscribe
    fun onThemeSelectedEvent(event: ThemeSelectedEvent) {
        selectedTheme = event.theme
        ScreenController.openScreen(ScreenController.Screen.DIFFICULTY)
        val task = object : AsyncTask<Void, Void, TransitionDrawable>() {

            override fun doInBackground(vararg params: Void): TransitionDrawable {
                val bitmap = Utils.scaleDown(R.drawable.background, Utils.screenWidth(), Utils.screenHeight())
                var backgroundImage = Themes.getBackgroundImage(selectedTheme!!)
                backgroundImage = Utils.crop(backgroundImage, Utils.screenHeight(), Utils.screenWidth())
                val backgrounds = arrayOfNulls<Drawable>(2)
                backgrounds[0] = BitmapDrawable(Shared.context!!.resources, bitmap)
                backgrounds[1] = BitmapDrawable(Shared.context!!.resources, backgroundImage)
                return TransitionDrawable(backgrounds)
            }

            override fun onPostExecute(result: TransitionDrawable) {
                super.onPostExecute(result)
                mBackgroundImage!!.setImageDrawable(result)
                result.startTransition(2000)
            }
        }
        task.execute()
    }

    /**
     * This is the event that notifies a game starting.
     *
     * @param event the difficulty event with the level chosen.
     */
    @Subscribe
    fun onDifficultySelectedEvent(event: DifficultySelectedEvent) {
        mFlippedCard = null
        activeGame = Game()
        activeGame!!.boardConfiguration = BoardConfiguration(event.difficulty, selectedTheme!!.id)
        activeGame!!.theme = selectedTheme!!
        mToFlip = activeGame!!.boardConfiguration.numTiles

        // arrange board
        arrangeBoard()

        // start the screen
        ScreenController.openScreen(ScreenController.Screen.GAME)
    }

    private fun arrangeBoard() {
        // setting up the necessary stuff
        val boardConfiguration = activeGame!!.boardConfiguration
        val boardArrangement = BoardArrangement()
        // the game log should be empty starting a new game.
        gameLog.clear()

        // build pairs
        // result {0,1,2,...n} // n-number of tiles
        val placementIds = ArrayList<Int>()
        for (i in 0 until boardConfiguration.numTiles) {
            placementIds.add(i)
        }
        // shuffle
        // result {4,10,2,39,...}
        Collections.shuffle(placementIds)

        // place the board
        // For a scientific study, a random game set is not a good idea.
        // Instead we take the set first and then randomize the position.
        // Psych! This we really do want some randomness in there.
        val tileImageUrls = activeGame!!.theme!!.tileImageUrls
        Collections.shuffle(tileImageUrls)

        boardArrangement.cards = SparseArray()
        boardArrangement.tileUrls = SparseArray()

        var pairId = 0
        var i = 0
        while (i + 1 < placementIds.size) {

            val firstCardPlacement = placementIds[i]
            val secondCardPlacement = placementIds[i + 1]

            val firstCard = Card(firstCardPlacement, pairId, 1)
            boardArrangement.cards!!.put(firstCardPlacement, firstCard)
            boardArrangement.tileUrls!!.put(firstCardPlacement, tileImageUrls[pairId])

            val secondCard = Card(secondCardPlacement, pairId, 2)
            boardArrangement.cards!!.put(secondCardPlacement, secondCard)
            boardArrangement.tileUrls!!.put(secondCardPlacement, tileImageUrls[pairId])

            pairId++
            i += 2
        }

        activeGame!!.boardArrangement = boardArrangement
    }

    @Subscribe
    fun onFlipCardEvent(event: FlipCardEvent) {
        val card = event.card

        Shared.activity!!.blinkEegLight()

        // adding the event to the log
        val timestamp = System.currentTimeMillis()
        val move = card.pairId.toString() + "." + card.cardNumber
        gameLog.add(Pair(timestamp, move))

        // if auditory obstacles were chosen then say a random number out loud
        if (activeGame!!.boardConfiguration.impairment === Impairment.AUDITORY_SUM_10) {
            Music.playRandomNumber()
        }

        if (mFlippedCard == null) {
            mFlippedCard = card
        } else {
            if (activeGame!!.boardArrangement.isPair(mFlippedCard!!, card)) {

                // send event - hide id1, id2
                mHandler?.postDelayed(1000) {
                    EventBus.getDefault().post(HidePairCardsEvent(mFlippedCard!!.placementId,
                            card.placementId))
                }


                // play music
                mHandler!!.postDelayed({ Music.playCorrect() }, 1000)
                mToFlip -= 2
                if (mToFlip == 0) {
                    val passedSeconds = (Clock.passedTime / 1000).toInt()
                    Clock.pause()
                    val totalTime = activeGame!!.boardConfiguration.time
                    val gameState = GameState()
                    activeGame!!.gameState = gameState
                    // remained seconds
                    gameState.remainedSeconds = totalTime - passedSeconds
                    gameState.passedSeconds = passedSeconds

                    // calc stars
                    when {
                        passedSeconds <= totalTime / 2 -> gameState.achievedStars = 3
                        passedSeconds <= totalTime - totalTime / 5 -> gameState.achievedStars = 2
                        passedSeconds < totalTime -> gameState.achievedStars = 1
                        else -> gameState.achievedStars = 0
                    }

                    // calc score
                    gameState.achievedScore = activeGame!!.boardConfiguration.difficulty * gameState.remainedSeconds * activeGame!!.theme.id

                    // save to memory
                    Memory.save(activeGame!!.theme.id, activeGame!!.boardConfiguration.difficulty, gameState.achievedStars)
                    Memory.saveTime(activeGame!!.theme.id, activeGame!!.boardConfiguration.difficulty, gameState.passedSeconds)
                    // and save the logs as well
                    MemoryDb.addGameLog(activeGame!!.theme.id,
                            activeGame!!.boardConfiguration.difficulty, gameLog)

                    mHandler?.postDelayed(1200) {
                        EventBus.getDefault().post(GameWonEvent(gameState))
                    }
                }
            } else {
                // Log.i("my_tag", "Flip: all down");
                // send event - flip all down
                mHandler?.postDelayed(1000) {
                    EventBus.getDefault().post(FlipDownCardsEvent())
                }
            }
            mFlippedCard = null
        }
    }

    fun setBackgroundImageView(backgroundImage: ImageView) {
        mBackgroundImage = backgroundImage
    }
}

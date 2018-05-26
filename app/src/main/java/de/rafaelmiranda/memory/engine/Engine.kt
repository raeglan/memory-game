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
import de.rafaelmiranda.memory.common.MemoryDb
import de.rafaelmiranda.memory.common.Music
import de.rafaelmiranda.memory.common.Shared
import de.rafaelmiranda.memory.events.*
import de.rafaelmiranda.memory.model.*
import de.rafaelmiranda.memory.themes.Theme
import de.rafaelmiranda.memory.themes.Themes
import de.rafaelmiranda.memory.ui.PopupManager
import de.rafaelmiranda.memory.utils.Utils
import de.rafaelmiranda.memory.utils.postDelayed
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

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
     * The order in which things will play out when in directed mode.
     */
    private val gameOrderList = ArrayList<Int>()
    private var directedGame: Boolean = false

    /**
     * The state of our game, including all the cool stuff, like game logs and sums.
     */
    private var gameState: GameState? = null

    init {
        mHandler = Handler()
    }

    fun start() {
        EventBus.getDefault().register(this)
    }

    fun stop() {
        EventBus.getDefault().unregister(this)
        activeGame = null
        gameState = null
        mBackgroundImage!!.setImageDrawable(null)
        mBackgroundImage = null
        mHandler!!.removeCallbacksAndMessages(null)
        mHandler = null
    }

    private fun arrangeBoard() {
        // setting up the necessary stuff
        val boardConfiguration = activeGame!!.boardConfiguration
        val boardArrangement = BoardArrangement()

        // build pairs
        // result {0,1,2,...n} // n-number of tiles
        val placementIds = ArrayList<Int>()
        for (i in 0 until boardConfiguration.numTiles) {
            placementIds.add(i)
        }
        // shuffle
        // result {4,10,2,39,...}
        placementIds.shuffle()

        // place the board
        // For a scientific study, a random game set is not a good idea.
        // Instead we take the set first and then randomize the position.
        // Psych! This we really do want some randomness in there.
        val tileImageUrls = activeGame!!.theme.tileImageUrls.shuffled()

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

    @Suppress("unused", "UNUSED_PARAMETER")
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onResetBackgroundEvent(event: ResetBackgroundEvent) {
        val drawable = mBackgroundImage!!.drawable
        if (drawable != null) {
            (drawable as TransitionDrawable).reverseTransition(2000)
        } else {
            object : AsyncTask<Void, Void, Bitmap>() {

                override fun doInBackground(vararg params: Void): Bitmap {
                    return Utils.scaleDown(R.drawable.background, Utils.screenWidth(),
                            Utils.screenHeight())
                }

                override fun onPostExecute(bitmap: Bitmap) {
                    mBackgroundImage!!.setImageBitmap(bitmap)
                }

            }.execute()
        }
    }

    @Suppress("UNUSED_PARAMETER", "unused")
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onStartEvent(event: StartEvent) {
        if (event.directedGame) {

        } else {

            ScreenController.openScreen(ScreenController.Screen.GAME_SELECT)
        }
    }

    @Suppress("unused", "UNUSED_PARAMETER")
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onNextEvent(event: NextEvent) {
        PopupManager.closePopup()
        // todo: Change the next game to select a new impairment.
        val theme = selectedTheme
        if (theme != null) {
            EventBus.getDefault().post(GameTypeSelectedEvent(theme))
        } else {
            EventBus.getDefault().post(BackGameEvent())
        }
    }

    @Suppress("unused", "UNUSED_PARAMETER")
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onBackGameEvent(event: BackGameEvent) {
        PopupManager.closePopup()
        ScreenController.openScreen(ScreenController.Screen.GAME_SELECT)
    }

    @Suppress("unused")
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onGameTypeSelectedEvent(event: GameTypeSelectedEvent) {
        // initializing those variables YO.
        selectedTheme = event.theme
        mFlippedCard = null

        // setting the active game
        val game = Game()
        game.boardConfiguration = BoardConfiguration()
        game.theme = selectedTheme!!
        mToFlip = game.boardConfiguration.numTiles
        activeGame = game

        // the game has just began, so a new game state should be initialized.
        val currentGameState = GameState(event.theme.id)
        // if the game is an auditory one we have something to sum
        if (currentGameState.gameTypeId == Theme.ID_AUDITORY) {
            currentGameState.numberSumUserAnswer = 0
            currentGameState.numberSum = 0
        }
        gameState = currentGameState

        // arrange board
        arrangeBoard()

        // start the screen
        ScreenController.openScreen(ScreenController.Screen.GAME)

        if (event.theme.backgroundImageUrl != selectedTheme?.backgroundImageUrl) {
            val task = object : AsyncTask<Void, Void, TransitionDrawable>() {

                override fun doInBackground(vararg params: Void): TransitionDrawable {
                    val bitmap = Utils.scaleDown(R.drawable.background, Utils.screenWidth(),
                            Utils.screenHeight())
                    var backgroundImage = Themes.getBackgroundImage(selectedTheme!!)
                    backgroundImage = Utils.crop(backgroundImage, Utils.screenHeight(),
                            Utils.screenWidth())
                    val backgrounds = arrayOfNulls<Drawable>(2)
                    backgrounds[0] = BitmapDrawable(Shared.context.resources, bitmap)
                    backgrounds[1] = BitmapDrawable(Shared.context.resources, backgroundImage)
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
    }

    @Suppress("unused")
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onFlipCardEvent(event: FlipCardEvent) {

        val currentGameState = gameState
                ?: throw IllegalStateException("Game State should not be null")
        val card = event.card

        Shared.activity.blinkEegLight()

        // adding the event to the log
        val timestamp = System.currentTimeMillis()
        val move = card.pairId.toString() + "." + card.cardNumber
        currentGameState.gameLog.add(GameTimeMovesPair(timestamp, move))

        // if auditory obstacles were chosen then say a random number out loud
        if (selectedTheme?.id == Theme.ID_AUDITORY) {
            currentGameState.numberSum += Music.playRandomNumber()
        }

        val previouslyFlippedCard = mFlippedCard
        if (previouslyFlippedCard == null) {
            mFlippedCard = card
        } else {
            if (activeGame!!.boardArrangement.isPair(previouslyFlippedCard, card)) {

                // send event - hide id1, id2
                mHandler?.postDelayed(1000) {
                    EventBus.getDefault().post(HidePairCardsEvent(previouslyFlippedCard.placementId,
                            card.placementId))
                }

                // play music
                mHandler!!.postDelayed({ Music.playCorrect() }, 1000)
                mToFlip -= 2
                if (mToFlip == 0) {
                    mHandler?.postDelayed(1200) {
                        EventBus.getDefault().post(GameWonEvent(currentGameState.gameTypeId))
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

        mHandler?.postDelayed(1200) {
            EventBus.getDefault().post(GameWonEvent(currentGameState.gameTypeId))
        }
    }

    /**
     * This receives from the end screen all the user answers and sends it along with all the other
     * data to our backend.
     */
    @Suppress("unused")
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onQuestionsAnsweredEvent(event: QuestionsAnsweredEvent) {
        val currentGameState = gameState
                ?: throw IllegalStateException("Game state should not be null")
        currentGameState.numberSumUserAnswer = event.sumAnswer
        // and save the logs as well
        MemoryDb.addGameLog(currentGameState)
    }

    fun setBackgroundImageView(backgroundImage: ImageView) {
        mBackgroundImage = backgroundImage
    }
}

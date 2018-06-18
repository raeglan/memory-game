package de.rafaelmiranda.memory.engine

import android.annotation.SuppressLint
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.TransitionDrawable
import android.os.AsyncTask
import android.os.Handler
import android.support.v4.app.FragmentManager
import android.util.SparseArray
import android.widget.ImageView
import com.google.firebase.Timestamp
import de.rafaelmiranda.memory.R
import de.rafaelmiranda.memory.common.MemoryDb
import de.rafaelmiranda.memory.common.Music
import de.rafaelmiranda.memory.common.Shared
import de.rafaelmiranda.memory.events.*
import de.rafaelmiranda.memory.fragments.GameFragment
import de.rafaelmiranda.memory.fragments.GameSelectFragment
import de.rafaelmiranda.memory.model.*
import de.rafaelmiranda.memory.types.Assistants
import de.rafaelmiranda.memory.types.GameType
import de.rafaelmiranda.memory.types.Types
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
    var selectedGameType: GameType? = null
        private set
    private var mBackgroundImage: ImageView? = null
    private var mHandler: Handler? = null

    /**
     * The order in which things will play out when in directed mode.
     */
    private val directedGameList = ArrayList<AssistedGame>()

    private var directedGame: Boolean = false

    /**
     * The state of our game, including all the cool stuff, like game logs and sums.
     */
    private var gameState: GameState? = null

    private val zoomTime: Long by lazy {
        Shared.context.resources.getInteger(R.integer.zoom_in_time).toLong()
    }

    fun start() {
        mHandler = Handler()
        EventBus.getDefault().register(this)
    }

    fun stop() {
        EventBus.getDefault().unregister(this)
        activeGame = null
        gameState = null
        mBackgroundImage!!.setImageDrawable(null)
        mBackgroundImage = null
        if (mHandler != null) {
            mHandler!!.removeCallbacksAndMessages(null)
            mHandler = null
        }
    }

    private fun arrangeBoard() {
        // setting up the necessary stuff
        val boardConfiguration = activeGame!!.boardConfiguration
        val boardArrangement = BoardArrangement()

        // build pairs
        // result {0,1,2,...n} // n-number of tiles
        val placementIds = (0 until boardConfiguration.numTiles).toMutableList()
        // shuffle
        // result {4,10,2,39,...}
        placementIds.shuffle()

        // place the board
        // For a scientific study, a random game set is not a good idea.
        // Instead we take the set first and then randomize the position.
        // Psych! This we really do want some randomness in there.
        val tileImageUrls = activeGame!!.gameType.tileImageUrls
        val selectedTiles = (1..tileImageUrls.size).shuffled()

        boardArrangement.cards = SparseArray()
        boardArrangement.tileUrls = SparseArray()

        var pairNumber = 0
        var i = 0
        while (i + 1 < placementIds.size) {

            val pairImageId = selectedTiles[pairNumber]
            val imageUrl = tileImageUrls[pairImageId - 1]
            val firstCardPlacement = placementIds[i]
            val secondCardPlacement = placementIds[i + 1]

            val firstCard = Card(firstCardPlacement, pairImageId, 1)
            boardArrangement.cards!!.put(firstCardPlacement, firstCard)
            boardArrangement.tileUrls!!.put(firstCardPlacement, imageUrl)

            val secondCard = Card(secondCardPlacement, pairImageId, 2)
            boardArrangement.cards!!.put(secondCardPlacement, secondCard)
            boardArrangement.tileUrls!!.put(secondCardPlacement, imageUrl)

            pairNumber++
            i += 2
        }

        activeGame!!.boardArrangement = boardArrangement
    }

    fun onStartPressed(fragmentManager: FragmentManager, directed: Boolean, gamesList: List<AssistedGame>) {
        directedGame = directed
        if (directedGame) {

            MemoryDb.startSession()
            directedGameList.clear()
            directedGameList.addAll(gamesList.shuffled())
            val firstGame = directedGameList.removeAt(0)
            val gameType = Types.createType(firstGame.gameId)

            startGame(fragmentManager, gameType, newGame = true, assistants = firstGame.assistants)
        } else {
            ScreenController.openFragment(fragmentManager, GameSelectFragment(), true)
        }
    }

    /**
     * Starts the next game on the list, if none we get back to the main screen.
     */
    fun onNext(fragmentManager: FragmentManager) {
        PopupManager.closePopup()
        val gameType = selectedGameType
        when {
            directedGame -> {
                if (directedGameList.isNotEmpty()) {
                    val nextGame = directedGameList.removeAt(0)
                    startGame(fragmentManager, Types.createType(nextGame.gameId),
                            newGame = false, assistants = nextGame.assistants)
                } else {
                    MemoryDb.endSession()
                    EventBus.getDefault().post(BackEvent())
                }
            }
            gameType != null -> startGame(fragmentManager, gameType, newGame = false)
            else -> EventBus.getDefault().post(BackEvent())
        }
    }

    /**
     * Starts a new game for the gameType given.
     */
    fun startGame(
            fragmentManager: FragmentManager,
            gameType: GameType,
            assistants: Assistants? = null,
            newGame: Boolean = true) {
        // initializing those variables YO.
        selectedGameType = gameType
        mFlippedCard = null

        // setting the active game
        val game = Game()
        game.boardConfiguration = BoardConfiguration()
        game.gameType = gameType
        game.assistants = assistants ?: Assistants()
        mToFlip = game.boardConfiguration.numTiles
        activeGame = game

        // the game has just began, so a new game state should be initialized.
        val currentGameState = GameState(gameType.id)
        // if the game is an auditory one we have something to sum
        if (currentGameState.gameTypeId == GameType.ID_AUDITORY) {
            currentGameState.numberSumUserAnswer = 0
            currentGameState.numberSum = 0
        }
        gameState = currentGameState

        // arrange board
        arrangeBoard()

        // Open the fragment
        val gameFragment = GameFragment.newInstance(gameType.id, assistants)
        ScreenController.openFragment(fragmentManager, gameFragment, addToBackStack = newGame)

        if (gameType.backgroundImageUrl != selectedGameType?.backgroundImageUrl) {
            val task = object : AsyncTask<Void, Void, TransitionDrawable>() {

                override fun doInBackground(vararg params: Void): TransitionDrawable {

                    val bitmap = Utils.scaleDown(R.drawable.background, Utils.screenWidth(),
                            Utils.screenHeight())
                    var backgroundImage = Types.getBackgroundImage(selectedGameType!!)
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

        // for the surplus time that should be added, maybe.
        val surplus = if (activeGame?.assistants?.zoomInOnFlip == true) zoomTime else 0L

        Shared.activity.blinkEegLight()

        // adding the event to the log
        val timestamp = Timestamp.now()
        val move = card.pairId.toString() + "." + card.cardNumber
        currentGameState.gameLog.add(GameTimeMovesPair(timestamp, move))

        // if auditory obstacles were chosen then say a random number out loud
        if (selectedGameType?.id == GameType.ID_AUDITORY) {
            currentGameState.numberSum += Music.playRandomNumber()
        }

        val previouslyFlippedCard = mFlippedCard
        if (previouslyFlippedCard == null) {
            mFlippedCard = card
        } else {
            if (activeGame!!.boardArrangement.isPair(previouslyFlippedCard, card)) {

                // send event - hide id1, id2
                mHandler?.postDelayed(1000 + surplus) {
                    EventBus.getDefault().post(HidePairCardsEvent(previouslyFlippedCard.placementId,
                            card.placementId))
                }

                // play music
                mHandler!!.postDelayed({ Music.playCorrect() }, 1000 + surplus)
                mToFlip -= 2
                if (mToFlip == 0) {
                    mHandler?.postDelayed(1200 + surplus) {
                        EventBus.getDefault().post(GameWonEvent(currentGameState.gameTypeId))
                    }
                }
            } else {
                // Log.i("my_tag", "Flip: all down");
                // send event - flip all down
                mHandler?.postDelayed(1000 + surplus) {
                    EventBus.getDefault().post(FlipDownCardsEvent())
                }
            }
            mFlippedCard = null
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
        val assistants = activeGame?.assistants ?: Assistants()
        MemoryDb.addGameLog(currentGameState, assistants = assistants)
    }

    fun setBackgroundImageView(backgroundImage: ImageView) {
        mBackgroundImage = backgroundImage
    }
}

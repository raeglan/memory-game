package de.rafaelmiranda.memory.ui

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.util.SparseArray
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.BounceInterpolator
import android.widget.ImageView
import android.widget.LinearLayout
import de.rafaelmiranda.memory.R
import de.rafaelmiranda.memory.events.FlipCardEvent
import de.rafaelmiranda.memory.model.BoardArrangement
import de.rafaelmiranda.memory.model.BoardConfiguration
import de.rafaelmiranda.memory.model.Game
import de.rafaelmiranda.memory.types.Assistants
import de.rafaelmiranda.memory.utils.Utils
import org.greenrobot.eventbus.EventBus

open class BoardView @JvmOverloads constructor(context: Context, attributeSet: AttributeSet? = null) : LinearLayout(context, attributeSet) {

    private val mRowLayoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
    private var mTileLayoutParams: LinearLayout.LayoutParams? = null
    private val mScreenWidth: Int
    private val mScreenHeight: Int
    private var mBoardConfiguration: BoardConfiguration? = null
    private var mBoardArrangement: BoardArrangement? = null
    private val mTileViews: SparseArray<TileView>
    private val flippedUp = ArrayList<Int>()
    private var locked = false
    private var mSize: Int = 0
    private var assistants: Assistants? = null
    private lateinit var enlargeImage: ImageView

    /** this saves a list of all previously flipped cards, in order, excluding the ones already out
    / of the game.
     **/
    private val replayCardsList = ArrayList<Int>()

    /**
     * How long the cards stay up on replay mode
     */
    private val replayUpTime by lazy {
        context.resources.getInteger(R.integer.replay_flip_up_time).toLong()
    }

    private val zoomUpTime by lazy {
        context.resources.getInteger(R.integer.zoom_in_time).toLong()

    }

    private val flipDuration by lazy {
        context.resources.getInteger(R.integer.flip_duration).toLong()
    }

    private val uiThreadHandler = Handler(Looper.getMainLooper())


    init {
        orientation = LinearLayout.VERTICAL
        gravity = Gravity.CENTER
        val margin = resources.getDimensionPixelSize(R.dimen.margin_top)
        val padding = resources.getDimensionPixelSize(R.dimen.board_padding)
        mScreenHeight = resources.displayMetrics.heightPixels - margin - padding * 2
        mScreenWidth = resources.displayMetrics.widthPixels - padding * 2 - Utils.px(20)
        mTileViews = SparseArray()
        clipToPadding = false
    }

    fun setBoard(game: Game, enlargeImage: ImageView) {
        mBoardConfiguration = game.boardConfiguration
        mBoardArrangement = game.boardArrangement
        assistants = game.assistants
        this.enlargeImage = enlargeImage

        // Calculating width and height.
        val singleMargin = resources.getDimensionPixelSize(R.dimen.card_margin)
        var sumMargin = 0
        for (row in 0 until mBoardConfiguration!!.numRows) {
            sumMargin += singleMargin * 2
        }
        val tilesHeight = (mScreenHeight - sumMargin) / mBoardConfiguration!!.numRows
        val tilesWidth = (mScreenWidth - sumMargin) / mBoardConfiguration!!.numTilesInRow
        mSize = Math.min(tilesHeight, tilesWidth)

        mTileLayoutParams = LinearLayout.LayoutParams(mSize, mSize)
        mTileLayoutParams!!.setMargins(singleMargin, singleMargin, singleMargin, singleMargin)

        // build the ui
        buildBoard()
    }

    /**
     * Build the board
     */
    private fun buildBoard() {

        for (row in 0 until mBoardConfiguration!!.numRows) {
            // add row
            addBoardRow(row)
        }

        clipChildren = false
    }

    private fun addBoardRow(rowNum: Int) {

        val linearLayout = LinearLayout(context)
        linearLayout.orientation = LinearLayout.HORIZONTAL
        linearLayout.gravity = Gravity.CENTER

        for (tile in 0 until mBoardConfiguration!!.numTilesInRow) {
            addTile(rowNum * mBoardConfiguration!!.numTilesInRow + tile, linearLayout)
        }

        // add to this view
        addView(linearLayout, mRowLayoutParams)
        linearLayout.clipChildren = false
    }

    private fun addTile(placementId: Int, parent: ViewGroup) {
        val tileView = TileView.fromXml(context, parent)
        tileView.layoutParams = mTileLayoutParams
        parent.addView(tileView)
        parent.clipChildren = false
        mTileViews.put(placementId, tileView)

        if (placementId < mBoardConfiguration!!.numTiles) {

            // putting the image inside the thingy.
            val resId = mBoardArrangement!!.getTileResId(placementId)
            tileView.setTileImage(resId, mSize)

            tileView.setOnClickListener {
                if (!locked && tileView.isFlippedDown) {
                    tileView.flipUp()
                    flippedUp.add(placementId)
                    replayCardsList.add(placementId)
                    if (flippedUp.size == 2) {
                        locked = true
                    }

                    if (assistants?.zoomInOnFlip == true) {
                        locked = true
                        uiThreadHandler.postDelayed(
                                {
                                    enlargeImage.setImageResource(resId)
                                    enlargeImage.visibility = View.VISIBLE

                                    uiThreadHandler.postDelayed(
                                            {
                                                enlargeImage.visibility = View.INVISIBLE
                                                locked = flippedUp.size >= 2
                                            }, zoomUpTime)
                                }, flipDuration)
                    }

                    // sending flips and pirouettes
                    val chosenCard = mBoardArrangement!!.cards!!.get(placementId)
                    EventBus.getDefault().post(FlipCardEvent(chosenCard))

                }
            }

            val scaleXAnimator = ObjectAnimator.ofFloat(tileView, "scaleX", 0.8f, 1f)
            scaleXAnimator.interpolator = BounceInterpolator()
            val scaleYAnimator = ObjectAnimator.ofFloat(tileView, "scaleY", 0.8f, 1f)
            scaleYAnimator.interpolator = BounceInterpolator()
            val animatorSet = AnimatorSet()
            animatorSet.playTogether(scaleXAnimator, scaleYAnimator)
            animatorSet.duration = 500
            tileView.setLayerType(View.LAYER_TYPE_HARDWARE, null)
            animatorSet.start()
        } else {
            tileView.visibility = View.INVISIBLE
        }
    }

    fun flipDownAll() {
        for (id in flippedUp) {
            mTileViews[id].flipDown()
        }
        flippedUp.clear()

        if (assistants?.replayAllFlips == true) {
            uiThreadHandler.postDelayed({ recursiveNextFlipReplay(0) }, replayUpTime)
        } else {
            locked = false
        }
    }

    /**
     * Flips up the given card on the replay list, waits, flips it down and does this to the next
     * one. When all cards were flipped it changes the lock to false.
     */
    private fun recursiveNextFlipReplay(index: Int) {
        if (index < replayCardsList.size) {

            val nextIndex = index + 1

            // if two following cards are the same then we break the flipping so our best bet is
            // to skip it.
            if (nextIndex < replayCardsList.size &&
                    replayCardsList[index] == replayCardsList[nextIndex]) {
                recursiveNextFlipReplay(nextIndex)
            } else {
                val tile = mTileViews[replayCardsList[index]]
                tile.flipUp()
                uiThreadHandler.postDelayed(
                        {
                            tile.flipDown()
                            recursiveNextFlipReplay(index + 1)
                        },
                        replayUpTime)
            }
        } else {
            locked = false
        }
    }

    fun hideCards(id1: Int, id2: Int) {
        animateHide(mTileViews.get(id1))
        animateHide(mTileViews.get(id2))

        // removes the enlarged card from view.
        enlargeImage.visibility = View.INVISIBLE

        // removes all instances of the hidden cards from the card replay.
        replayCardsList.removeAll { it == id1 || it == id2 }

        flippedUp.clear()
        locked = false
    }

    private fun animateHide(v: TileView) {
        val animator = ObjectAnimator.ofFloat(v, "alpha", 0f)
        animator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                v.setLayerType(View.LAYER_TYPE_NONE, null)
                v.visibility = View.INVISIBLE
            }
        })
        animator.duration = 100
        v.setLayerType(View.LAYER_TYPE_HARDWARE, null)
        animator.start()
    }

    companion object {

        fun fromXml(context: Context, parent: ViewGroup): BoardView {
            return LayoutInflater.from(context).inflate(R.layout.board_view, parent, false) as BoardView
        }
    }

}

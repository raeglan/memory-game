package de.rafaelmiranda.memory.ui

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.util.AttributeSet
import android.util.SparseArray
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.BounceInterpolator
import android.widget.LinearLayout
import android.widget.Toast
import com.squareup.picasso.Picasso
import de.rafaelmiranda.memory.R
import de.rafaelmiranda.memory.events.FlipCardEvent
import de.rafaelmiranda.memory.model.BoardArrangement
import de.rafaelmiranda.memory.model.BoardConfiguration
import de.rafaelmiranda.memory.model.Game
import de.rafaelmiranda.memory.utils.Utils
import org.greenrobot.eventbus.EventBus
import java.util.*

open class BoardView @JvmOverloads constructor(context: Context, attributeSet: AttributeSet? = null) : LinearLayout(context, attributeSet) {

    /**
     * For cancelling purposes.
     */
    private var displayedToast: Toast? = null
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

    // constants
    val flipUpAllTimeMillis: Int = context.resources.getInteger(R.integer.flip_all_up_time)

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

    fun setBoard(game: Game) {
        mBoardConfiguration = game.boardConfiguration
        mBoardArrangement = game.boardArrangement
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
            Picasso.get()
                    .load(mBoardArrangement!!.getTileResId(placementId, mSize))
                    .resize(mSize, mSize)
                    .into(tileView.mTileImage)

            tileView.setOnClickListener {
                if (!locked && tileView.isFlippedDown) {
                    tileView.flipUp()
                    flippedUp.add(placementId)
                    if (flippedUp.size == 2) {
                        locked = true
                    }
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
        locked = false
    }

    /**
     * Makes sure everything is down and then flips everything up for a second or two.
     */
    fun flipUpAll(): Boolean {
        return if (flippedUp.size > 0 || locked) {
            displayedToast?.cancel()
            displayedToast = Toast
                    .makeText(context, R.string.flip_all_cards_hint, Toast.LENGTH_SHORT).also {
                        it.show()
                    }
            false
        } else {
            for (i in 0 until mTileViews.size()) {
                val key = mTileViews.keyAt(i)
                mTileViews[key].flipUp()
                flippedUp.add(key)
                locked = true
            }

            this.postDelayed({
                flipDownAll()
            }, flipUpAllTimeMillis.toLong())

            true
        }
    }


    fun hideCards(id1: Int, id2: Int) {
        animateHide(mTileViews.get(id1))
        animateHide(mTileViews.get(id2))
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
